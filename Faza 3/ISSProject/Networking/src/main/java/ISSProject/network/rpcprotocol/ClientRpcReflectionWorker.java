package ISSProject.network.rpcprotocol;

import ISSProject.domain.*;
import ISSProject.service.IObserver;
import ISSProject.service.IService;
import ISSProject.service.MyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;


public class ClientRpcReflectionWorker implements Runnable, IObserver {
    private final IService service;
    private final Socket connection;

    private static final Logger logger= LogManager.getLogger();

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    public ClientRpcReflectionWorker(IService service, Socket connection) {
        logger.info("Creating worker");
        this.service = service;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
            logger.info("Worker created");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error in worker (reading): "+e);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Error in worker (sleeping): "+e);
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error in worker (closing connection): "+e);
        }
    }

    private static final Response okResponse=new Response.Builder().type(ResponseType.OK).build();
    private Response handleRequest(Request request){
        Response response=null;
        String handlerName="handle"+(request).type();
        logger.traceEntry("method entered: "+handlerName+" with parameters "+request);
        try {
            Method method=this.getClass().getDeclaredMethod(handlerName, Request.class);
            response=(Response)method.invoke(this,request);
            logger.info("Method invoked: "+handlerName+" with response "+response);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            logger.error("Error in worker (invoking method handleRequest): "+e);
        }
        return response;
    }

    private Response handleLOGIN_READER(Request request){
        logger.traceEntry("method entered: handleLOGIN_READER with parameters "+request);
        Reader reader = (Reader) request.data();
        try {
            Reader foundReader = service.authenticateReader(reader, this);
            logger.info("Reader logged in");
            return new Response.Builder().type(ResponseType.OK).data(foundReader).build();
        } catch (MyException e) {
            connected=false;
            logger.error("Error in worker (solving method handleLOGIN): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGIN_LIBRARIAN(Request request){
        logger.traceEntry("method entered: handleLOGIN_LIBRARIAN with parameters "+request);
        Librarian librarian = (Librarian) request.data();
        try {
            Librarian foundLibrarian = service.authenticateLibrarian(librarian, this);
            logger.info("Reader logged in");
            return new Response.Builder().type(ResponseType.OK).data(foundLibrarian).build();
        } catch (MyException e) {
            connected=false;
            logger.error("Error in worker (solving method handleLOGIN): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_ALL_BOOKS(Request request){
        logger.traceEntry("method entered: handleGET_ALL_ARTISTS with parameters "+request);
        try {
            List<Book> books= (List<Book>) service.getAllBooks();
            logger.info("Artists found "+books);
            return new Response.Builder().type(ResponseType.GET_ALL_BOOKS).data(books).build();
        } catch (MyException e) {
            logger.error("Error in worker (solving method handleGET_ALL_ARTISTS): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleSECURITY(Request request){
        logger.traceEntry("method entered: handleSECURITY with parameters "+request);
        Integer security= (Integer) request.data();
        try {
            boolean isOk= service.security(security);
            logger.info("Security assured "+security);
            return new Response.Builder().type(ResponseType.OK).data(security).build();
        } catch (MyException e) {
            logger.error("Error in worker (solving method handleSECURITY): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT_LIBRARIAN(Request request){
        logger.traceEntry("method entered: handleLOGOUT with parameters "+request);
        Librarian librarian=(Librarian) request.data();
        try {
            service.logoutLibrarian(librarian);
            connected=false;
            logger.info("User logged out");
            return okResponse;
        } catch (MyException e) {
            logger.error("Error in worker (solving method handleLOGOUT_LIBRARIAN): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT_READER(Request request){
        logger.traceEntry("method entered: handleLOGOUT with parameters "+request);
        Reader reader=(Reader) request.data();
        try {
            service.logoutReader(reader);
            connected=false;
            logger.info("User logged out");
            return okResponse;
        } catch (MyException e) {
            logger.error("Error in worker (solving method handleLOGOUT_READER): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOAN_BOOK(Request request){
        logger.traceEntry("method entered: handleLOAN_BOOK with parameters "+request);
        // Extract the reader and the books from the request which are in a Pair object
        Pair pair = (Pair) request.data();
        Reader reader = pair.getReader();
        Book book = pair.getBook();
        try {
            service.loanBook(reader, book);
            logger.info("Book loaned");
            return okResponse;
        } catch (MyException e) {
            logger.error("Error in worker (solving method handleLOAN_BOOK): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleRETURN_BOOK(Request request){
        logger.traceEntry("method entered: handleRETURN_BOOK with parameters "+request);
        // Extract the reader and the books from the request which are in a Pair object
        PairLibrarianLoan pair = (PairLibrarianLoan) request.data();
        Librarian librarian = pair.getLibrarian();
        Integer loanId = pair.getLoan();
        try {
            service.returnBook(librarian, loanId);
            logger.info("Book returned");
            return okResponse;
        } catch (MyException e) {
            logger.error("Error in worker (solving method handleRETURN_BOOK): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleREGISTER_READER(Request request){
        logger.traceEntry("method entered: handleREGISTER_READER with parameters "+request);
        Reader reader = (Reader) request.data();
        try {
            service.registerReader(reader);
            logger.info("Reader registered");
            return okResponse;
        } catch (MyException e) {
            logger.error("Error in worker (solving method handleREGISTER_READER): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_ALL_BOOK_LOANS(Request request){
        logger.traceEntry("method entered: handleGET_ALL_LOANS with parameters "+request);
        try {
            List<BookLoan> loans= (List<BookLoan>) service.getAllBookLoans();
            logger.info("Loans found "+loans);
            return new Response.Builder().type(ResponseType.OK).data(loans).build();
        } catch (MyException e) {
            logger.error("Error in worker (solving method handleGET_ALL_LOANS): "+e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private void sendResponse(Response response) throws IOException{
        logger.traceEntry("method entered: sendResponse with parameters "+response);
        output.writeObject(response);
        output.flush();
        logger.info("Response sent");
    }

    @Override
    public void showBooks() {
        Response response = new Response.Builder().type(ResponseType.LOAN_BOOK).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            logger.error(e);
        }
    }
}

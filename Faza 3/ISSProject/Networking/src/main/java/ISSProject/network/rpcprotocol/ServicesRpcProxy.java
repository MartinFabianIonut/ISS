package ISSProject.network.rpcprotocol;

import ISSProject.domain.*;
import ISSProject.service.IService;
import ISSProject.service.IObserver;
import ISSProject.service.MyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ServicesRpcProxy implements IService {
    private final String host;
    private final int port;

    private IObserver observer;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private static final Logger logger = LogManager.getLogger();

    private final BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesRpcProxy(String host, int port) {
        logger.info("Creating proxy");
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            observer = null;
            logger.info("Closed connection");
        } catch (IOException e) {
            logger.error("Error closing connection: " + e);
        }
    }

    private void sendRequest(Request request) throws MyException {
        try {
            output.writeObject(request);
            output.flush();
            logger.info("Request sent: " + request);
        } catch (IOException e) {
            logger.error("Error sending object " + e);
            throw new MyException("Error sending object " + e);
        }
    }

    private Response readResponse() throws MyException {
        Response response = null;
        try {
            response = qresponses.take();
            logger.info("Response received: " + response);
        } catch (InterruptedException e) {
            logger.error("Reading response error " + e);
            throw new MyException("Reading response error " + e);
        }
        return response;
    }

    private void initializeConnection() throws MyException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
            logger.info("Connection initialized");
        } catch (IOException e) {
            logger.error("Error connecting to server " + e);
            throw new MyException("Error connecting to server " + e);
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response) {
        observer.showBooks();
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.MANAGEMENT;
    }

    @Override
    public Reader authenticateReader(Reader employee, IObserver employeeObserver) throws MyException {
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN_READER).data(employee).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.observer = employeeObserver;
            logger.info("Logged in");
            return (Reader) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error logging in" + response.data().toString());
            String err = response.data().toString();
            closeConnection();
            throw new MyException(err);
        }
        return null;
    }

    @Override
    public Librarian authenticateLibrarian(Librarian employee, IObserver employeeObserver) throws MyException {
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN_LIBRARIAN).data(employee).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.observer = employeeObserver;
            logger.info("Logged in");
            return (Librarian) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error logging in" + response.data().toString());
            String err = response.data().toString();
            closeConnection();
            throw new MyException(err);
        }
        return null;
    }

    @Override
    public boolean security(Integer key) throws MyException {
        Request req = new Request.Builder().type(RequestType.SECURITY).data(key).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            logger.info("Security ok");
            return true;
        }
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error security" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        }
        return false;
    }

    @Override
    public Iterable<Book> getAllBooks() throws MyException {
        Request req = new Request.Builder().type(RequestType.GET_ALL_BOOKS).data(null).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error getting shows" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Got shows");
        }
        return (List<Book>) response.data();
    }

    @Override
    public void logoutLibrarian(Librarian librarian) throws MyException {
        Request req = new Request.Builder().type(RequestType.LOGOUT_LIBRARIAN).data(librarian).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error logging out" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Logged out");
        }
    }

    @Override
    public void logoutReader(Reader reader) throws MyException {
        Request req = new Request.Builder().type(RequestType.LOGOUT_READER).data(reader).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error logging out" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Logged out");
        }
    }

    @Override
    public void loanBook(Reader reader, Book book) throws MyException {
        Request req = new Request.Builder().type(RequestType.LOAN_BOOK).data(new Pair(reader, book)).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error loaning books" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Loaned books");
        }
    }

    @Override
    public void returnBook(Librarian librarian, Integer loan_id) throws MyException {
        Request req = new Request.Builder().type(RequestType.RETURN_BOOK).data(new PairLibrarianLoan(librarian, loan_id)).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error returning books" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Returned books");
        }
    }

    @Override
    public void registerReader(Reader reader) throws MyException {
        Request req = new Request.Builder().type(RequestType.REGISTER_READER).data(reader).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error registering reader" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Registered reader");
        }
    }

    @Override
    public Iterable<BookLoan> getAllBookLoans() throws MyException {
        Request req = new Request.Builder().type(RequestType.GET_ALL_BOOK_LOANS).data(null).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error getting book loans" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Got book loans");
        }
        return (List<BookLoan>) response.data();
    }

    @Override
    public boolean findByUsername(String username) throws MyException {
        Request req = new Request.Builder().type(RequestType.FIND_BY_USERNAME).data(username).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            logger.info("Found username");
            return true;
        }
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error finding username" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        }
        return false;
    }

    @Override
    public void deleteBook(Book book) throws MyException {
        Request req = new Request.Builder().type(RequestType.DELETE_BOOK).data(book).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error deleting book" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Deleted book");
        }
    }

    @Override
    public void updateBook(Book book) throws MyException {
        Request req = new Request.Builder().type(RequestType.UPDATE_BOOK).data(book).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error updating book" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Updated book");
        }
    }

    @Override
    public void addBook(Book book) throws MyException {
        Request req = new Request.Builder().type(RequestType.ADD_BOOK).data(book).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            logger.error("Error adding book" + response.data().toString());
            String err = response.data().toString();
            throw new MyException(err);
        } else {
            logger.info("Added book");
        }
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    logger.info("response received " + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            logger.error("Queue putting response error: " + e);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    if (e instanceof SocketException)
                        logger.info("Socket closed: " + e);
                    else
                        logger.error("Reading error: " + e);
                }
            }
        }
    }
}

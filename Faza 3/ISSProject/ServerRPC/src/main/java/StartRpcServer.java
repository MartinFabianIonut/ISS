import ISSProject.domain.Book;
import ISSProject.domain.BookLoan;
import ISSProject.domain.Librarian;
import ISSProject.domain.Reader;
import ISSProject.network.utils.AbstractServer;
import ISSProject.network.utils.RpcConcurrentServer;
import ISSProject.network.utils.ServerException;
import ISSProject.persistance.repository.ILibrarianRepository;
import ISSProject.persistance.repository.IReaderRepository;
import ISSProject.persistance.repository.IBookRepository;
import ISSProject.persistance.repository.IBookLoanRepository;
import ISSProject.persistance.repository.jdbc.LibrarianDBIRepository;
import ISSProject.persistance.repository.jdbc.ReaderDBIRepository;
import ISSProject.persistance.repository.jdbc.BookDBIRepository;
import ISSProject.persistance.repository.jdbc.BookLoanDBIRepository;
import ISSProject.server.ServicesImpl;
import ISSProject.service.IService;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;

    public static void main(String[] args) {
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find server.properties "+e);
            return;
        }
        ILibrarianRepository<Integer, Librarian> librarianDBIRepository = new LibrarianDBIRepository();
        IReaderRepository<Integer, Reader> readerDBIRepository = new ReaderDBIRepository();
        IBookRepository<Integer, Book> bookDBIRepository = new BookDBIRepository();
        IBookLoanRepository<Integer, BookLoan> bookLoansDBIRepository = new BookLoanDBIRepository();
        IService serverImpl=new ServicesImpl(bookDBIRepository, bookLoansDBIRepository,readerDBIRepository,librarianDBIRepository);

        int serverPort=defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, serverImpl);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }
}

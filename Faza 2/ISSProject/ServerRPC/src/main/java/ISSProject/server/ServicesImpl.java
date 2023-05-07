package ISSProject.server;

import ISSProject.domain.*;
import ISSProject.persistance.repository.IBookLoansRepository;
import ISSProject.persistance.repository.IBookRepository;
import ISSProject.persistance.repository.ILibrarianRepository;
import ISSProject.persistance.repository.IReaderRepository;
import ISSProject.service.IObserver;
import ISSProject.service.IService;
import ISSProject.service.MyException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServicesImpl implements IService {

    private final IBookRepository<Integer, Book> bookRepository;
    private final IBookLoansRepository<Integer, BookLoans> bookLoansRepository;
    private final IReaderRepository<Integer, Reader> readerRepository;
    private final ILibrarianRepository<Integer, Librarian> librarianRepository;
    private final Map<Integer, IObserver> loggedEmployees;

    public ServicesImpl(IBookRepository<Integer, Book> bookRepository,
                        IBookLoansRepository<Integer, BookLoans> bookLoansRepository,
                        IReaderRepository<Integer, Reader> readerRepository,
                        ILibrarianRepository<Integer, Librarian> librarianRepository) {
        this.bookRepository = bookRepository;
        this.bookLoansRepository = bookLoansRepository;
        this.readerRepository = readerRepository;
        this.librarianRepository = librarianRepository;
        loggedEmployees = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Reader authenticateReader(Reader employee, IObserver employeeObserver) throws MyException {
        Reader authenticateEmployee = readerRepository.authenticateReader(employee.getUsername(), employee.getPassword());
        if (authenticateEmployee!=null){
            if(loggedEmployees.get(authenticateEmployee.getId())!=null)
                throw new MyException("User already logged in.");
            loggedEmployees.put(authenticateEmployee.getId(), employeeObserver);
        }else
            throw new MyException("Authentication failed.");
        return authenticateEmployee;
    }

    @Override
    public Iterable<Book> getAllBooks() throws MyException {
        return bookRepository.getAll();
    }

    @Override
    public synchronized Librarian authenticateLibrarian(Librarian employee, IObserver employeeObserver) throws MyException {
        Librarian authenticateEmployee = librarianRepository.authenticateLibrarian(employee.getUsername(), employee.getPassword());
        if (authenticateEmployee!=null){
            if(loggedEmployees.get(authenticateEmployee.getId())!=null)
                throw new MyException("User already logged in.");
            loggedEmployees.put(authenticateEmployee.getId(), employeeObserver);
        }else
            throw new MyException("Authentication failed.");
        return authenticateEmployee;
    }

    @Override
    public boolean security(Integer key) throws MyException {
        if(key!=1234)
            throw new MyException("Null key.");
        return true;
    }

    @Override
    public synchronized void logoutReader(Reader employee, IObserver employeeObserver) throws MyException {
        IObserver localClient= loggedEmployees.remove(employee.getId());
        if (localClient==null)
            throw new MyException("User "+employee.getId()+" is not logged in.");
    }

    @Override
    public synchronized void logoutLibrarian(Librarian employee, IObserver employeeObserver) throws MyException {
        IObserver localClient = loggedEmployees.remove(employee.getId());
        if (localClient == null)
            throw new MyException("User " + employee.getId() + " is not logged in.");
    }


}

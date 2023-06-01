package ISSProject.server;

import ISSProject.domain.*;
import ISSProject.persistance.repository.IBookLoansRepository;
import ISSProject.persistance.repository.IBookRepository;
import ISSProject.persistance.repository.ILibrarianRepository;
import ISSProject.persistance.repository.IReaderRepository;
import ISSProject.service.IObserver;
import ISSProject.service.IService;
import ISSProject.service.MyException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServicesImpl implements IService {

    private final IBookRepository<Integer, Book> bookRepository;
    private final IBookLoansRepository<Integer, BookLoan> bookLoansRepository;
    private final IReaderRepository<Integer, Reader> readerRepository;
    private final ILibrarianRepository<Integer, Librarian> librarianRepository;
    private final Map<Integer, IObserver> loggedEmployees;

    public ServicesImpl(IBookRepository<Integer, Book> bookRepository,
                        IBookLoansRepository<Integer, BookLoan> bookLoansRepository,
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
    public synchronized void logoutReader(Reader employee) throws MyException {
        IObserver localClient= loggedEmployees.remove(employee.getId());
        if (localClient==null)
            throw new MyException("User "+employee.getId()+" is not logged in.");
    }

    @Override
    public void loanBook(Reader reader, Book book) throws MyException {
        // Create bookLoan with day of loan = today and day of return = today + 14 days, status = STILL_BORROWED,
        // librarian = null, reader = the reader who logged in, book = the book that was loaned
        BookLoan bookLoan = new BookLoan(0, today(), todayPlus14(), Status.STILL_BORROWED);
        bookLoan.setReader(reader);
        bookLoan.setBook(book);
        bookLoansRepository.add(bookLoan);
    }

    @Override
    public void returnBook(Librarian librarian, Integer loan) throws MyException {
        // Find the bookLoan with the given id
        BookLoan bookLoan = bookLoansRepository.findById(loan);
        // Set the librarian who logged in as the librarian who loaned the book
        bookLoan.setLibrarian(librarian);
        // Set the status of the bookLoan to RETURNED
        bookLoan.setStatus(Status.RETURNED);
        // Update the bookLoan in the repository
        bookLoansRepository.update(bookLoan);
        // Update the book in the repository
        Book book = bookLoan.getBook();
        book.setStatus(Status.AVAILABLE);
        bookRepository.update(book);
    }

    @Override
    public void registerReader(Reader reader) {
        readerRepository.add(reader);
    }

    @Override
    public Iterable<BookLoan> getAllBookLoans() {
        return bookLoansRepository.getAll();
    }

    private String todayPlus14() {
        //return today's date + 14 days as a string
        return LocalDate.now().plusDays(14).toString();
    }

    private String today() {
        //return today's date as a string
        return LocalDate.now().toString();
    }


    @Override
    public synchronized void logoutLibrarian(Librarian employee) throws MyException {
        IObserver localClient = loggedEmployees.remove(employee.getId());
        if (localClient == null)
            throw new MyException("User " + employee.getId() + " is not logged in.");
    }


}

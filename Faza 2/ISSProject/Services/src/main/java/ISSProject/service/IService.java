package ISSProject.service;


import ISSProject.domain.Librarian;
import ISSProject.domain.Reader;
import ISSProject.domain.Book;

public interface IService {
     Librarian authenticateLibrarian(Librarian librarian, IObserver employeeObserver) throws MyException;
     boolean security(Integer key) throws MyException;
     Reader authenticateReader(Reader reader, IObserver employeeObserver) throws MyException;
     Iterable<Book> getAllBooks() throws MyException;
     void logoutLibrarian(Librarian librarian, IObserver employeeObserver) throws MyException;
     void logoutReader(Reader reader, IObserver employeeObserver) throws MyException;

}

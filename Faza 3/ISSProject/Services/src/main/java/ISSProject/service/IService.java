package ISSProject.service;


import ISSProject.domain.BookLoan;
import ISSProject.domain.Librarian;
import ISSProject.domain.Reader;
import ISSProject.domain.Book;

import java.util.List;

public interface IService {
     Librarian authenticateLibrarian(Librarian librarian, IObserver librarianObserver) throws MyException;
     boolean security(Integer key) throws MyException;
     Reader authenticateReader(Reader reader, IObserver readerObserver) throws MyException;
     Iterable<Book> getAllBooks() throws MyException;
     void logoutLibrarian(Librarian librarian) throws MyException;
     void logoutReader(Reader reader) throws MyException;
     void loanBook(Reader reader, Book book) throws MyException;
     void returnBook(Librarian librarian, Integer loan) throws MyException;
     void registerReader(Reader reader) throws MyException;
     Iterable<BookLoan> getAllBookLoans() throws MyException;
}

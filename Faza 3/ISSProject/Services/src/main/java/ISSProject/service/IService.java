package ISSProject.service;

import ISSProject.domain.BookLoan;
import ISSProject.domain.Librarian;
import ISSProject.domain.Reader;
import ISSProject.domain.Book;

public interface IService {
     /// Methods for readers 
     /**
      * 
      * @param reader - the reader to be authenticated
      * @param readerObserver - the observer of the reader
      * @return the authenticated reader
      * @throws MyException if the reader does not exist or if the reader is already logged in
      */
     Reader authenticateReader(Reader reader, IObserver readerObserver) throws MyException;
     
     /**
      * 
      * @param reader - the reader who wants to loan a book
      * @param book - the book to be loaned
      * @throws MyException ...
      */
     void loanBook(Reader reader, Book book) throws MyException;

     /**
      * @param reader - the reader to be logged out
      * @throws MyException ...
      */
     void logoutReader(Reader reader) throws MyException;

     // Methods for librarians

     /**
      * 
      * @param librarian - the librarian to be authenticated
      * @param librarianObserver - the observer of the librarian
      * @return the authenticated librarian
      * @throws MyException if the librarian does not exist or if the librarian is already logged in
      */
     Librarian authenticateLibrarian(Librarian librarian, IObserver librarianObserver) throws MyException;

     /**
      * 
      * @param key - the key to be checked
      * @return true if the key is valid, false otherwise
      * @throws MyException if the key is null
      */
     boolean security(Integer key) throws MyException;

     /**
      * 
      * @param reader - the reader to be registered
      * @throws MyException if the reader already exists
      */
     void registerReader(Reader reader) throws MyException;

     /**
      *
      * @param username - the username to be checked
      * @return true if the username does not exist, false otherwise
      * @throws MyException if the username is null
      */
     boolean findByUsername(String username) throws MyException;

     /**
      *
      * @param librarian - the librarian who returns the book
      * @param loan - the loan to be returned
      * @throws MyException ...
      */
     void returnBook(Librarian librarian, Integer loan) throws MyException;

     /**
      *
      * @param book - the book to be added
      * @throws MyException if the book already exists
      */
     void addBook(Book book) throws MyException;

     /**
      *
      * @param book - the book to be deleted
      * @throws MyException if the book does not exist
      */
     void deleteBook(Book book) throws MyException;

     /**
      *
      * @param book - the book to be updated
      * @throws MyException if the book does not exist
      */
     void updateBook(Book book) throws MyException;

     /**
      *
      * @param librarian - the librarian to be logged out
      * @throws MyException ...
      */
     void logoutLibrarian(Librarian librarian) throws MyException;

     // Methods for both readers and librarians

     /**
      *
      * @return all the books
      * @throws MyException ...
      */
     Iterable<Book> getAllBooks() throws MyException;

        /**
        *
        * @return all the book loans
        * @throws MyException ...
        */
     Iterable<BookLoan> getAllBookLoans() throws MyException;



}

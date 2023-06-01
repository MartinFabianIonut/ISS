package ISSProject.domain;

import java.util.List;

public class Pair {
    public Reader reader;
    public Book book;
    public Pair(Reader reader, Book book) {
        this.reader = reader;
        this.book = book;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

}

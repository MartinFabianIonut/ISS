package ISSProject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@javax.persistence.Entity
@Table(name = "book")
@AttributeOverride(name = "id", column = @Column(name = "book_id"))
public class Book extends Entity<Integer> implements Serializable {
    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private List<BookLoan> bookLoan;

    public Book(Integer integer, String title, String author, Status status) {
        super(integer);
        this.title = title;
        this.author = author;
        this.status = status;
        this.bookLoan = new ArrayList<>();
    }

    public List<BookLoan> getBookLoan() {
        return bookLoan;
    }

    public void setBookLoan(List<BookLoan> bookLoan) {
        this.bookLoan = bookLoan;
    }

    public Book(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return title + " by " + author + " with status " + status;
    }
}

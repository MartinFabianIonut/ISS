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
    private Status status;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "bookloansmanytomany",
//            joinColumns = @JoinColumn(name = "book_id"),
//            inverseJoinColumns = @JoinColumn(name = "loan_id"))
//    private List<BookLoans> bookLoans;

    public Book(Integer integer, String title, String author, Status status) {
        super(integer);
        this.title = title;
        this.author = author;
        this.status = status;
        //this.bookLoans = new ArrayList<>();
    }

//    public List<BookLoans> getBookLoans() {
//        return bookLoans;
//    }
//
//    public void setBookLoans(List<BookLoans> bookLoans) {
//        this.bookLoans = bookLoans;
//    }

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

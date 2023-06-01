package ISSProject.domain;

import java.io.Serializable;
import javax.persistence.*;

@javax.persistence.Entity
@Table(name = "bookloan")
@AttributeOverride(name = "id", column = @Column(name = "loan_id"))

public class BookLoan extends Entity<Integer>implements Serializable {
    @Column(name = "dayofloan")
    private String dayOfLoan;
    @Column(name = "deadline")
    private String deadline;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reader_id")
    private Reader reader;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "librarian_id")
    private Librarian librarian;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    public BookLoan(Integer integer, String dayOfLoan, String deadline, Status status) {
        super(integer);
        this.dayOfLoan = dayOfLoan;
        this.deadline = deadline;
        this.status = status;
        this.reader = null;
        this.librarian = null;
    }

    public BookLoan(){}

    public String getDayOfLoan() {
        return dayOfLoan;
    }

    public void setDayOfLoan(String dayOfLoan) {
        this.dayOfLoan = dayOfLoan;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "BookLoan{" +
                "id=" + getId() +
                ", dayOfLoan=" + dayOfLoan +
                ", deadline=" + deadline +
                ", idLibrarian=" + librarian.getId() +
                ", idReader=" + reader.getId() +
                ", status=" + status +
                '}';
    }
}

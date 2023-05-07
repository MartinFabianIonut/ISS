package ISSProject.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@javax.persistence.Entity
@Table(name = "bookloans")
@AttributeOverride(name = "id", column = @Column(name = "loan_id"))

public class BookLoans extends Entity<Integer>implements Serializable {
    @Column(name = "dayofloan")
    private LocalDate dayOfLoan;
    @Column(name = "deadline")
    private LocalDate deadline;
    @Column(name = "idlibrarian")
    private Integer idLibrarian;
    @Column(name = "idreader")
    private Integer idReader;
    @Column(name = "status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id")
    private Reader reader;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "librarian_id")
//    private Librarian librarian;
//
//    @ManyToMany(mappedBy = "bookloans", fetch = FetchType.LAZY)
//    private List<Book> books;

    public BookLoans(Integer integer, LocalDate dayOfLoan, LocalDate deadline, Integer idLibrarian, Integer idClient, Status status) {
        super(integer);
        this.dayOfLoan = dayOfLoan;
        this.deadline = deadline;
        this.idLibrarian = idLibrarian;
        this.idReader = idClient;
        this.status = status;
        this.reader = null;
//        this.librarian = null;
//        this.books = new ArrayList<>();
    }

    public BookLoans(){}

    public LocalDate getDayOfLoan() {
        return dayOfLoan;
    }

    public void setDayOfLoan(LocalDate dayOfLoan) {
        this.dayOfLoan = dayOfLoan;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Integer getIdLibrarian() {
        return idLibrarian;
    }

    public void setIdLibrarian(Integer idLibrarian) {
        this.idLibrarian = idLibrarian;
    }

    public Integer getIdReader() {
        return idReader;
    }

    public void setIdReader(Integer idReader) {
        this.idReader = idReader;
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
//
//    public Librarian getLibrarian() {
//        return librarian;
//    }
//
//    public void setLibrarian(Librarian librarian) {
//        this.librarian = librarian;
//    }
//
//    public List<Book> getBooks() {
//        return books;
//    }
//
//    public void setBooks(List<Book> books) {
//        this.books = books;
//    }

    @Override
    public String toString() {
        return "BookLoans{" +
                "id=" + getId() +
                ", dayOfLoan=" + dayOfLoan +
                ", deadline=" + deadline +
                ", idBook=" + idLibrarian +
                ", idReader=" + idReader +
                ", status=" + status +
                '}';
    }
}

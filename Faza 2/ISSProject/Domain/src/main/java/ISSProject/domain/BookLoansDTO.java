package ISSProject.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class BookLoansDTO extends Entity<Integer>implements Serializable {

    private Integer idLoan;
    private String title;
    private String author;
    private Status status;
    private LocalDate dayOfLoan;

    public BookLoansDTO(Integer integer, Integer idLoan, String title, String author, Status status, LocalDate dayOfLoan) {
        super(integer);
        this.idLoan = idLoan;
        this.title = title;
        this.author = author;
        this.status = status;
        this.dayOfLoan = dayOfLoan;
    }

    public Integer getIdLoan() {
        return idLoan;
    }

    public void setIdLoan(Integer idLoan) {
        this.idLoan = idLoan;
    }

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

    public LocalDate getDayOfLoan() {
        return dayOfLoan;
    }

    public void setDayOfLoan(LocalDate dayOfLoan) {
        this.dayOfLoan = dayOfLoan;
    }

    @Override
    public String toString() {
        return "BookLoansDTO{" +
                "id=" + getId() +
                ", idLoan=" + idLoan +
                ", title=" + title +
                ", author=" + author +
                ", status=" + status +
                ", dayOfLoan=" + dayOfLoan +
                '}';
    }
}

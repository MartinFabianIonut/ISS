package ISSProject.domain;

import javax.persistence.*;
import java.io.Serializable;
@javax.persistence.Entity
@Table(name = "bookloansmanytomany2")
@AttributeOverride(name = "id", column = @Column(name = "many_id"))

public class BookLoansManyToMany extends Entity<Integer>implements Serializable {
    @Column(name = "idbook")
    private Integer idBook;
    @Column(name = "idloan")
    private Integer idLoan;

    public BookLoansManyToMany(Integer id, Integer idBook, Integer idLoan) {
        super(id);
        this.idBook = idBook;
        this.idLoan = idLoan;
    }

    public BookLoansManyToMany(){}

    public Integer getIdBook() {
        return idBook;
    }

    public void setIdBook(Integer idBook) {
        this.idBook = idBook;
    }

    public Integer getIdLoan() {
        return idLoan;
    }

    public void setIdLoan(Integer idLoan) {
        this.idLoan = idLoan;
    }

    @Override
    public String toString() {
        return "BookLoansManyToMany{" +
                "id=" + getId() +
                ", idBook=" + idBook +
                ", idLoan=" + idLoan +
                '}';
    }
}

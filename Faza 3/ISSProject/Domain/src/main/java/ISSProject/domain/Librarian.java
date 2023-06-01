package ISSProject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@javax.persistence.Entity
@Table(name = "librarian")
@AttributeOverride(name = "id", column = @Column(name = "librarian_id"))

public class Librarian extends Entity<Integer>implements Serializable {
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "librarian_id")
    private List<BookLoan> bookLoans;

    public Librarian(Integer id, String username, String password) {
        super(id);
        this.username = username;
        this.password = password;
        this.bookLoans = new ArrayList<>();
    }

    public List<BookLoan> getBookLoans() {
        return bookLoans;
    }

    public void setBookLoans(List<BookLoan> bookLoans) {
        this.bookLoans = bookLoans;
    }

    public Librarian(){}
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return username + " " + password;
    }
}

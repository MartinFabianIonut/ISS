package ISSProject.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@javax.persistence.Entity
@Table(name = "reader")
@AttributeOverride(name = "id", column = @Column(name = "reader_id"))

public class Reader extends Entity<Integer>implements Serializable {
    @Column(name = "cnp")
    private String CNP;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phoneNumber;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id")
    private List<BookLoans> bookLoans;

    public Reader(Integer integer, String CNP, String name, String address, String phoneNumber, String username, String password) {
        super(integer);
        this.CNP = CNP;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.bookLoans = new ArrayList<>();
    }

    public Reader(){}

    public List<BookLoans> getBookLoans() {
        return bookLoans;
    }

    public void setBookLoans(List<BookLoans> bookLoans) {
        this.bookLoans = bookLoans;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

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
        String s = "Reader{" +
                "CNP='" + CNP + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
        return s;
    }
}

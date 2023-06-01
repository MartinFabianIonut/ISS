package ISSProject.domain;

public class PairLibrarianLoan {
    public Librarian librarian;
    public Integer loan;
    public PairLibrarianLoan(Librarian librarian, Integer loan) {
        this.librarian = librarian;
        this.loan = loan;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public Integer getLoan() {
        return loan;
    }

    public void setLoan(Integer loan) {
        this.loan = loan;
    }
}

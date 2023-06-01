package ISSProject.persistance.repository;

import ISSProject.domain.Book;
import ISSProject.domain.Entity;

import java.io.Serializable;
import java.util.List;

public interface IBookLoansRepository<ID extends Serializable, E extends Entity<ID>> extends IRepository<ID, E> {
    void addLoan(E entity, Book book);
}

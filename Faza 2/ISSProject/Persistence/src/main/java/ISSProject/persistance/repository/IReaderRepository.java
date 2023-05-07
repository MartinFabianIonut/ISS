package ISSProject.persistance.repository;

import ISSProject.domain.Entity;

import java.io.Serializable;

public interface IReaderRepository<ID extends Serializable, E extends Entity<ID>> extends IRepository<ID, E> {
    E authenticateReader(String username, String password);
}

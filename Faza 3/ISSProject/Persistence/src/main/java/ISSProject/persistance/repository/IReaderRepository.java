package ISSProject.persistance.repository;

import ISSProject.domain.Entity;

import java.io.Serializable;

public interface IReaderRepository<ID extends Serializable, E extends Entity<ID>> extends IRepository<ID, E> {
    /**
     * @param username the username of the reader
     * @param password the password of the reader
     * @return null if the username and password don't match, otherwise returns the reader
     */
    E authenticateReader(String username, String password);

    /**
     * @param username the username of the reader
     * @return true if the username exists, false otherwise
     */
    boolean findByUsername(String username);
}

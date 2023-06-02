package ISSProject.persistance.repository;
import ISSProject.domain.Entity;

import java.io.Serializable;

public interface ILibrarianRepository<ID extends Serializable, E extends Entity<ID>> extends IRepository<ID, E> {
    /**
     * @param username the username of the librarian
     * @param password the password of the librarian
     * @return null if the username and password don't match, otherwise returns the librarian
     */
    E authenticateLibrarian(String username, String password);
}

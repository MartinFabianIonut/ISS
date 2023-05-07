package ISSProject.persistance.repository;
import ISSProject.domain.Entity;

import java.io.Serializable;

public interface ILibrarianRepository<ID extends Serializable, E extends Entity<ID>> extends IRepository<ID, E> {
    E authenticateLibrarian(String username, String password);
}

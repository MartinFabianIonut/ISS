package ISSProject.persistance.repository;

import ISSProject.domain.Entity;
import ISSProject.service.MyException;

import java.io.Serializable;

public interface IRepository<ID extends Serializable, E extends Entity<ID>>  {
    /**
     * @param id of type ID (template)
     * @return found entity or null
     */
    E findById(ID id);

    /**
     * @return all entities of type E (template)
     */
    Iterable<E> getAll();

    /**
     * @param entity of type E (template) to be added
     */
    void add(E entity);

    /**
     * @param id of type ID (template) to be deleted
     * @throws MyException if the entity with the given id does not exist or
     * if the entity has dependencies
     */
    void delete(ID id) throws MyException;

    /**
     * @param entity of type E (template) to be updated
     */
    void update(E entity);
}

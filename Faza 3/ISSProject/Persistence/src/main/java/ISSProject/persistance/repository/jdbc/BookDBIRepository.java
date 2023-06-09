package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.Book;
import ISSProject.persistance.repository.IBookRepository;
import ISSProject.service.MyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class BookDBIRepository implements IBookRepository<Integer, Book> {
    private static final Logger logger = LogManager.getLogger();
    private static SessionFactory sessionFactory;

    public BookDBIRepository() {
        initialize();
    }

    public void initialize() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.out.println("Exceptie " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Override
    public Book findById(Integer id) {
        logger.traceEntry("find a book by id");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Book book = session.createQuery("from Book where id=:id", Book.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                transaction.commit();
                logger.traceExit(book);
                return book;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("ERROR for findById in BookDBIRepository: " + ex);
            }
        }
        return null;
    }

    @Override
    public Iterable<Book> getAll() {
        logger.traceEntry("find all books");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Book> books = session.createQuery("from Book", Book.class)
                        .list();
                transaction.commit();
                logger.traceExit(books);
                return books;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("ERROR for getAll in BookDBIRepository: " + ex);
            }
        }
        return null;
    }

    @Override
    public void add(Book book) {
        logger.traceEntry("inserting book");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(book);
                transaction.commit();
                logger.trace("insert {} instance", book);
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("ERROR for insert in BookDBIRepository: " + ex);
            }
        }
        logger.traceExit("inserted successfully");
    }


    @Override
    public void delete(Integer id) throws MyException {
        logger.traceEntry("delete book");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Book where id=:id";
                Book book = session.createQuery(hql, Book.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                session.delete(book);
                tx.commit();
                logger.traceExit("book {} deleted successfully", book);
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
                logger.error("ERROR for delete in BookDBIRepository: " + ex);
                throw new MyException("Error for delete in BookDBIRepository: " + ex);
            }
        }
    }

    @Override
    public void update(Book show) {
        logger.traceEntry("updating book");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.update(show);
                tx.commit();
                logger.traceExit("book updated successfully");
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
                logger.error("ERROR for update in BookDBIRepository: " + ex);
            }
        }
    }
}
package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.Reader;
import ISSProject.persistance.repository.IReaderRepository;

import ISSProject.service.MyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class ReaderDBIRepository implements IReaderRepository<Integer, Reader> {
    private static final Logger logger = LogManager.getLogger();
    private static SessionFactory sessionFactory;

    public ReaderDBIRepository() {
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
    public Reader findById(Integer id) {
        logger.traceEntry("find a reader by id");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Reader reader = session.createQuery("from Reader where id=:id", Reader.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                transaction.commit();
                logger.traceExit(reader);
                return reader;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("ERROR for insert in ReaderDBIRepository: " + ex);
            }
        }
        logger.traceExit("no reader found with id {}", id);
        return null;
    }

    @Override
    public Iterable<Reader> getAll() {
        logger.traceEntry("find all readers");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                List<Reader> readers = session.createQuery("from Reader", Reader.class)
                        .list();
                transaction.commit();
                logger.traceExit(readers);
                return readers;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("ERROR for insert in ReaderDBIRepository: " + ex);
            }
        }
        logger.traceExit("no reader found");
        return null;
    }

    @Override
    public void add(Reader reader) {
        logger.traceEntry("add reader {}", reader);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(reader);
                transaction.commit();
                logger.trace("saved {} instance", reader);
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("ERROR for insert in ReaderDBIRepository: " + ex);
            }
        }
        logger.traceExit("reader inserted successfully");
    }


    @Override
    public void delete(Integer id) throws MyException {
        logger.traceEntry("delete reader");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String hql = "from Reader where id=:id";
                Reader reader = session.createQuery(hql, Reader.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                session.delete(reader);
                tx.commit();
                logger.traceExit("reader deleted successfully");
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
                logger.error("ERROR for delete in ReaderDBIRepository: " + ex);
                throw new MyException("ERROR for delete in ReaderDBIRepository: " + ex);
            }
        }
    }

    @Override
    public void update(Reader reader) {
        logger.traceEntry("updating reader");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.update(reader);
                tx.commit();
                logger.traceExit("reader updated successfully");
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
                logger.error("ERROR for update in ReaderDBIRepository: " + ex);
            }
        }
    }

    @Override
    public Reader authenticateReader(String username, String password) {
        logger.traceEntry("authenticating reader");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            System.out.println("Session: " + session);
            try {
                tx = session.beginTransaction();
                Reader reader = session.createQuery("from Reader where username=:username and password=:password", Reader.class)
                        .setParameter("username", username)
                        .setParameter("password", password)
                        .setMaxResults(1)
                        .uniqueResult();
                tx.commit();
                logger.traceExit(reader);
                return reader;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        logger.traceExit("no reader found");
        return null;
    }

    @Override
    public boolean findByUsername(String username) {
        logger.traceEntry("find a reader by username");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                Reader reader = session.createQuery("from Reader where username=:username", Reader.class)
                        .setParameter("username", username)
                        .setMaxResults(1)
                        .uniqueResult();
                transaction.commit();
                logger.traceExit(reader);
                return true;
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("ERROR for finding reader by username in ReaderDBIRepository: " + ex);
            }
        }
        logger.traceExit("no reader found with username {}", username);
        return false;
    }
}

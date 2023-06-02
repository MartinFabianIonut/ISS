package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.BookLoan;
import ISSProject.persistance.repository.IBookLoanRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class BookLoanDBIRepository implements IBookLoanRepository<Integer, BookLoan> {
    private static final Logger logger = LogManager.getLogger();
    private static SessionFactory sessionFactory;

    public BookLoanDBIRepository() {
        initialize();
    }

    public void initialize() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.out.println("Exception " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Override
    public BookLoan findById(Integer id) {
        logger.traceEntry("find a loan by id");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                BookLoan bookLoan = session.createQuery("from BookLoan where id = :id", BookLoan.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                tx.commit();
                logger.traceExit(bookLoan);
                return bookLoan;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        logger.traceExit("No loan found with id {}", id);
        return null;
    }

    @Override
    public Iterable<BookLoan> getAll() {
        logger.traceEntry("find all loans");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<BookLoan> bookLoans = session.createQuery("from BookLoan", BookLoan.class)
                        .list();
                tx.commit();
                logger.traceExit(bookLoans);
                return bookLoans;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        logger.traceExit("No loans found");
        return null;
    }

    @Override
    public void add(BookLoan loan) {
        logger.traceEntry("add loan");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(loan);
                transaction.commit();
                logger.trace("inserted {} instance", loan);
            } catch (RuntimeException ex) {
                if (transaction != null)
                    transaction.rollback();
                logger.error("ERROR for insert in BookDBIRepository: " + ex);
            }
        }
        logger.traceExit("loan inserted successfully");
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry("delete loan");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String hql = "from BookLoan where id=:id";
                BookLoan bookLoan = session.createQuery(hql, BookLoan.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                session.delete(bookLoan);
                tx.commit();
                logger.traceExit("loan deleted successfully");
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
                logger.error("ERROR for delete in BookLoanDBIRepository: " + ex);
            }
        }
    }

    @Override
    public void update(BookLoan loan) {
        logger.traceEntry("updating loan");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.update(loan);
                tx.commit();
                logger.traceExit("loan updated successfully");
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
                logger.error("ERROR for update in BookLoanDBIRepository: " + ex);
            }
        }
    }
}

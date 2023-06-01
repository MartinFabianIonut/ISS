package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.Book;
import ISSProject.domain.BookLoan;
import ISSProject.persistance.repository.IBookLoansRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class BookLoansDBIRepository implements IBookLoansRepository<Integer, BookLoan> {
    private static final Logger logger = LogManager.getLogger();

    private  static SessionFactory sessionFactory;
    public BookLoansDBIRepository(){
        initialize();
    }
    public void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.out.println("Exceptie " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Override
    public BookLoan findById(Integer id) {
        logger.traceEntry("find a show by id");
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
        logger.traceExit("No show found with id {}", id);
        return null;
    }

    @Override
    public Iterable<BookLoan> getAll() {
        logger.traceEntry("find all shows");
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
        logger.traceExit("No show found");
        return null;
    }

    @Override
    public boolean add(BookLoan book) {
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try{
                transaction=session.beginTransaction();
                session.save(book);
                transaction.commit();
                logger.trace("saved {} instance", book);
            }
            catch (RuntimeException ex){
                if(transaction!=null)
                    transaction.rollback();
                logger.error("ERROR for insert in BookDBIRepository: " + ex);
            }
        }
        logger.traceExit("inserted successfully");
        return true;
    }



    @Override
    public boolean delete(Integer id) {
        logger.traceEntry("delete ticket");
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                String hql = "from BookLoan where id=:id";
                BookLoan book = session.createQuery(hql, BookLoan.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                System.out.println("Stergem mesajul "+book.getId());
                session.delete(book);
                tx.commit();
                logger.traceExit("deleted successfully");
                return true;
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
                logger.error("ERROR for delete in BookDBIRepository: " + ex);
            }
        }
        return false;
    }

    @Override
    public boolean update(BookLoan show) {
        logger.traceEntry("updating ticket");
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                session.update(show);
                tx.commit();
                logger.traceExit("updated successfully");
                return true;
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
                logger.error("ERROR for update in BookDBIRepository: " + ex);
            }
        }
        return true;
    }

    @Override
    public void addLoan(BookLoan entity, Book book) {
        // ADD ID of bookloans and book to table bookLoans_borrowedBooks
        logger.traceEntry("add loan");
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                // insert id of bookloans and book in table bookLoans_borrowedBooks



                tx.commit();
                logger.traceExit("added successfully");
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
                logger.error("ERROR for addLoan in BookDBIRepository: " + ex);
            }
        }
    }
}

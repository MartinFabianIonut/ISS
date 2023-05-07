package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.Book;
import ISSProject.domain.BookLoans;
import ISSProject.persistance.repository.IBookLoansRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BookLoansDBIRepository implements IBookLoansRepository<Integer, BookLoans> {
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
    public BookLoans findById(Integer id) {
        logger.traceEntry("find a show by id");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                BookLoans bookLoans = session.createQuery("from BookLoans where id = :id", BookLoans.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                tx.commit();
                logger.traceExit(bookLoans);
                return bookLoans;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        logger.traceExit("No show found with id {}", id);
        return null;
    }

    @Override
    public Iterable<BookLoans> getAll() {
        logger.traceEntry("find all shows");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<BookLoans> bookLoans = session.createQuery("from BookLoans", BookLoans.class)
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
    public boolean add(BookLoans book) {
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
                String hql = "from BookLoans where id=:id";
                BookLoans book = session.createQuery(hql, BookLoans.class)
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
    public boolean update(BookLoans show) {
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
    public void addLoan(BookLoans entity, List<Book> books) {
        return;
    }
}

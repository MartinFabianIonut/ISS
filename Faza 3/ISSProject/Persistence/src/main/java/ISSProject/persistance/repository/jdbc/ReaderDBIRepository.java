package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.Librarian;
import ISSProject.domain.Reader;
import ISSProject.persistance.repository.IReaderRepository;

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

public class ReaderDBIRepository implements IReaderRepository<Integer, Reader> {

    private  static SessionFactory sessionFactory;
    public ReaderDBIRepository(){
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
    private static final Logger logger= LogManager.getLogger();

    @Override
    public Reader findById(Integer id) {
        logger.traceEntry("find a show by id");
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try {
                transaction=session.beginTransaction();
                Reader book=session.createQuery("from Reader where id=:id",Reader.class)
                        .setParameter("id",id)
                        .setMaxResults(1)
                        .uniqueResult();
                transaction.commit();
                logger.traceExit(book);
                return book;
            }
            catch (RuntimeException ex){
                if(transaction!=null)
                    transaction.rollback();
                logger.error("ERROR for insert in BookDBIRepository: " + ex);
            }

        }
        return null;
    }

    @Override
    public Iterable<Reader> getAll() {
        logger.traceEntry("find all books");
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try{
                transaction=session.beginTransaction();
                List<Reader> books=session.createQuery("from Reader",Reader.class)
                        .list();
                transaction.commit();
                logger.traceExit(books);
                return books;
            }
            catch (RuntimeException ex){
                if(transaction!=null)
                    transaction.rollback();
                logger.error("ERROR for insert in BookDBIRepository: " + ex);
            }
        }
        return null;
    }

    @Override
    public boolean add(Reader book) {
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
                String hql = "from Reader where id=:id";
                Reader book = session.createQuery(hql, Reader.class)
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
    public boolean update(Reader show) {
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
    public Reader authenticateReader(String username, String password) {
        try(Session session=sessionFactory.openSession()){
            Transaction tx=null;
            System.out.println("Session: "+session);
            try{
                tx=session.beginTransaction();
                Reader employee=session.createQuery("from Reader where username=:username and password=:password",Reader.class)
                        .setParameter("username",username)
                        .setParameter("password",password)
                        .setMaxResults(1)
                        .uniqueResult();
                tx.commit();
                return employee;
            }
            catch (RuntimeException ex){
                if(tx!=null)
                    tx.rollback();
            }
        }
        return null;
    }
}

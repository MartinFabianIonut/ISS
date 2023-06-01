package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.Librarian;
import ISSProject.persistance.repository.ILibrarianRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class LibrarianDBIRepository implements ILibrarianRepository<Integer, Librarian> {

    private  static SessionFactory sessionFactory;
    public LibrarianDBIRepository(){
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
    public Librarian findById(Integer id) {
        logger.traceEntry("find a show by id");
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try {
                transaction=session.beginTransaction();
                Librarian book=session.createQuery("from Librarian where id=:id",Librarian.class)
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
    public Iterable<Librarian> getAll() {
        logger.traceEntry("find all books");
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try{
                transaction=session.beginTransaction();
                List<Librarian> books=session.createQuery("from Librarian",Librarian.class)
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
    public boolean add(Librarian book) {
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
                String hql = "from Librarian where id=:id";
                Librarian book = session.createQuery(hql, Librarian.class)
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
    public boolean update(Librarian show) {
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
    public Librarian authenticateLibrarian(String username, String password) {
        try(Session session=sessionFactory.openSession()){
            Transaction tx=null;
            System.out.println("Session: "+session);
            try{
                tx=session.beginTransaction();
                Librarian employee=session.createQuery("from Librarian where username=:username and password=:password",Librarian.class)
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

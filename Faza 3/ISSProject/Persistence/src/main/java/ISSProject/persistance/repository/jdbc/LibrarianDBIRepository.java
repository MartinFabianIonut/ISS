package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.Librarian;
import ISSProject.persistance.repository.ILibrarianRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class LibrarianDBIRepository implements ILibrarianRepository<Integer, Librarian> {
    private static final Logger logger= LogManager.getLogger();
    private  static SessionFactory sessionFactory;
    public LibrarianDBIRepository(){
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
    public Librarian findById(Integer id) {
        logger.traceEntry("find a librarian by id");
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try {
                transaction=session.beginTransaction();
                Librarian librarian=session.createQuery("from Librarian where id=:id",Librarian.class)
                        .setParameter("id",id)
                        .setMaxResults(1)
                        .uniqueResult();
                transaction.commit();
                logger.traceExit(librarian);
                return librarian;
            }
            catch (RuntimeException ex){
                if(transaction!=null)
                    transaction.rollback();
                logger.error("ERROR for insert in LibrarianDBIRepository: " + ex);
            }
        }
        logger.traceExit("no librarian found with id {}",id);
        return null;
    }

    @Override
    public Iterable<Librarian> getAll() {
        logger.traceEntry("find all librarians");
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try{
                transaction=session.beginTransaction();
                List<Librarian> librarians=session.createQuery("from Librarian",Librarian.class)
                        .list();
                transaction.commit();
                logger.traceExit(librarians);
                return librarians;
            }
            catch (RuntimeException ex){
                if(transaction!=null)
                    transaction.rollback();
                logger.error("ERROR for insert in LibrarianDBIRepository: " + ex);
            }
        }
        logger.traceExit("no librarian found");
        return null;
    }

    @Override
    public void add(Librarian librarian) {
        logger.traceEntry("add librarian {}", librarian);
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try{
                transaction=session.beginTransaction();
                session.save(librarian);
                transaction.commit();
                logger.trace("saved {} instance", librarian);
            }
            catch (RuntimeException ex){
                if(transaction!=null)
                    transaction.rollback();
                logger.error("ERROR for insert in LibrarianDBIRepository: " + ex);
            }
        }
        logger.traceExit("librarian inserted successfully");
    }



    @Override
    public void delete(Integer id) {
        logger.traceEntry("delete librarian");
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                String hql = "from Librarian where id=:id";
                Librarian librarian = session.createQuery(hql, Librarian.class)
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .uniqueResult();
                session.delete(librarian);
                tx.commit();
                logger.traceExit("librarian deleted successfully");
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
                logger.error("ERROR for delete in LibrarianDBIRepository: " + ex);
            }
        }
    }

    @Override
    public void update(Librarian show) {
        logger.traceEntry("updating librarian");
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                session.update(show);
                tx.commit();
                logger.traceExit("librarian updated successfully");
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
                logger.error("ERROR for update in LibrarianDBIRepository: " + ex);
            }
        }
    }

    @Override
    public Librarian authenticateLibrarian(String username, String password) {
        logger.traceEntry("authenticating librarian");
        try(Session session=sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx=session.beginTransaction();
                Librarian librarian=session.createQuery("from Librarian where username=:username and password=:password",Librarian.class)
                        .setParameter("username",username)
                        .setParameter("password",password)
                        .setMaxResults(1)
                        .uniqueResult();
                tx.commit();
                logger.traceExit(librarian);
                return librarian;
            }
            catch (RuntimeException ex){
                if(tx!=null)
                    tx.rollback();
            }
        }
        logger.traceExit("no librarian found");
        return null;
    }
}

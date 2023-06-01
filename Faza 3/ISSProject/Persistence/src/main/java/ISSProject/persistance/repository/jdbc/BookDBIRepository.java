package ISSProject.persistance.repository.jdbc;

import ISSProject.domain.Book;
import ISSProject.domain.BookLoansDTO;
import ISSProject.domain.Status;
import ISSProject.persistance.repository.IBookRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public class BookDBIRepository implements IBookRepository<Integer, Book> {

    private  static SessionFactory sessionFactory;
    public BookDBIRepository(){
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
//    private final JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

//    public BookDBIRepository(Properties props) {
//        logger.info("Initializing ShowDBRepository with properties: {} ",props);
//        dbUtils=new JdbcUtils(props);
//    }
//
//    private Book getBookFromResultSet(ResultSet resultSet) throws SQLException {
//        int id = resultSet.getInt("id");
//        String title = resultSet.getString("title");
//        String author = resultSet.getString("author");
//        String status = resultSet.getString("status");
//        Status status1 = Status.valueOf(status);
//        return new Book(id, title, author, status1);
//    }
//
//    private ShowDTO getShowDTOFromResultSet(ResultSet resultSet, int available, int unavailable) throws SQLException {
//        int id = resultSet.getInt("id");
//        String title = resultSet.getString("title");
//        LocalDate date = LocalDate.parse(resultSet.getString("date"));
//        String place = resultSet.getString("place");
//        int idArtist = resultSet.getInt("id_artist");
//        return new ShowDTO(id, title, date, place, available, unavailable, idArtist);
//    }

    @Override
    public Book findById(Integer id) {
        logger.traceEntry("find a show by id");
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try {
                transaction=session.beginTransaction();
                Book book=session.createQuery("from Book where id=:id",Book.class)
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
    public Iterable<Book> getAll() {
        logger.traceEntry("find all books");
        try(Session session=sessionFactory.openSession()){
            Transaction transaction=null;
            try{
                transaction=session.beginTransaction();
                List<Book> books=session.createQuery("from Book",Book.class)
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
    public boolean add(Book book) {
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
                String hql = "from Book where id=:id";
                Book book = session.createQuery(hql, Book.class)
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
    public boolean update(Book show) {
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
    public Iterable<BookLoansDTO> getAllBookLoansDTO() {
        return null;
    }

//    public Iterable<ShowDTO> getAllShowsDTO() {
//        logger.traceEntry("finding all shows dto");
//        Connection connection = dbUtils.getConnection();
//        List<ShowDTO> showDTOS = new ArrayList<>();
//        try (PreparedStatement ps = connection.prepareStatement("select * from show")) {
//            try (ResultSet resultSet = ps.executeQuery()) {
//                while (resultSet.next()) {
//                    int available = 0;
//                    int unavailable = 0;
//                    String sql = "SELECT COUNT(*) as available FROM show INNER JOIN ticket ON show.id = ticket.id_show " +
//                            "WHERE show.id = ? and ticket.name_of_costumer is null";
//                    String sql2 = "SELECT COUNT(*) as unavailable FROM show INNER JOIN ticket ON show.id = ticket.id_show " +
//                            "WHERE show.id = ? and ticket.name_of_costumer is not null";
//                    int id = resultSet.getInt("id");
//                    try (PreparedStatement ps2 = connection.prepareStatement(sql)){
//                        ps2.setInt(1, id);
//                        try (ResultSet resultSet2 = ps2.executeQuery()){
//                            if(resultSet2.next()) {
//                                available = resultSet2.getInt("available");
//                            }
//                        }
//                    }
//                    try (PreparedStatement ps3 = connection.prepareStatement(sql2)){
//                        ps3.setInt(1, id);
//                        try (ResultSet resultSet3 = ps3.executeQuery()){
//                            if(resultSet3.next()) {
//                                unavailable = resultSet3.getInt("unavailable");
//                            }
//                        }
//                    }
//                    showDTOS.add(getShowDTOFromResultSet(resultSet, available,unavailable));
//                }
//            }
//        } catch (SQLException exception) {
//            logger.error("ERROR for getAll in ShowDBRepository: " + exception);
//        }
//        logger.traceExit(showDTOS);
//        return showDTOS;
//    }
}

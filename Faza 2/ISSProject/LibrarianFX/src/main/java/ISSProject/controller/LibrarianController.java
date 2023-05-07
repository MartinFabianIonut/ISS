package ISSProject.controller;

import ISSProject.domain.Book;
import ISSProject.domain.BookLoansDTO;
import ISSProject.domain.Librarian;
import ISSProject.domain.Status;
import ISSProject.service.IObserver;
import ISSProject.service.IService;
import ISSProject.service.MyException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class LibrarianController implements Initializable, IObserver {

    private Librarian librarian;
    private IService service;
    private Stage loginStage;

    @FXML
    TableView<Book> allBooksTableView;
    @FXML
    TableView<BookLoansDTO> filteredShowsTableView;
    ObservableList<Book> bookObservableList;
    @FXML
    TableColumn<Book, String> returned;


    public LibrarianController() {
        System.out.println("LibrarianController created");
    }

    @FXML
    public void setLibrarian(Librarian employee) {
        this.librarian = employee;
    }

    @FXML
    public void setService(IService service) throws MyException {
        this.service = service;
    }

    @FXML
    public void setStage(Stage stage) {
        this.loginStage = stage;
    }

    private void showAllBooks() throws MyException {
        List<Book> showDTOS = (List<Book>) this.service.getAllBooks();
        bookObservableList = FXCollections.observableArrayList(showDTOS);
        allBooksTableView.setItems(bookObservableList);
        allBooksTableView.refresh();
    }

    private void initialiseTable() {
        allBooksTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null)
                    setStyle("");
                else if (item.getStatus() == Status.AVAILABLE)
                    setStyle("-fx-background-color: #baffba;");
                else if (item.getStatus() == Status.BORROWED)
                    setStyle("-fx-background-color: #f5b2aa;");
                else
                    setStyle("");
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (librarian != null) {
            try {
                showAllBooks();
            } catch (MyException e) {
                throw new RuntimeException(e);
            }
            initialiseTable();
        }
    }

    public void init() {
        try {
            showAllBooks();
        } catch (MyException e) {
            throw new RuntimeException(e);
        }
        initialiseTable();
    }

//    public void ticketAdded(Ticket ticket) throws MyException {
//        Platform.runLater(() -> {
//            try {
//                showAllShows();
//            } catch (MyException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }

    public void logout() {
        try {
            service.logoutLibrarian(librarian, this);
            loginStage.show();
        } catch (MyException e) {
            System.out.println("Logout error " + e);
        }
    }

    public void logoutAction() {
        logout();
        Stage stage = (Stage) this.allBooksTableView.getScene().getWindow();
        stage.close();
    }

}

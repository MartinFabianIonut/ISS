package ISSProject.controller;

import ISSProject.domain.Book;
import ISSProject.domain.Reader;
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
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReaderController implements Initializable, IObserver {

    private Reader reader;
    private IService service;
    private Stage loginStage;

    @FXML
    TableView<Book> allBooksTableView;;

    ObservableList<Book> bookObservableList;
    @FXML
    TableColumn<Book, String> loan;


    public ReaderController() {
        System.out.println("LibrarianController created");
    }

    @FXML
    public void setReader(Reader employee) {
        this.reader = employee;
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
        List<Book> allBooks = (List<Book>) this.service.getAllBooks();
        bookObservableList = FXCollections.observableArrayList(allBooks);
        allBooksTableView.setItems(bookObservableList);
        allBooksTableView.refresh();
    }

    private void initialiseTable() {
        loan.setCellFactory(new Callback<>() {
            @Override
            public TableCell call(final TableColumn<Book, String> param) {
                return new TableCell<Book, String>() {
                    final CheckBox loanCheckBox = new CheckBox();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setGraphic(loanCheckBox);
                            setText(null);
                        }
                    }
                };
            }
        });
//        allBooksTableView.setRowFactory(tv -> new TableRow<>() {
//            @Override
//            protected void updateItem(Book item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null)
//                    setStyle("");
//                else if (item.getStatus() == Status.AVAILABLE)
//                    setStyle("-fx-background-color: #baffba;");
//                else if (item.getStatus() == Status.BORROWED)
//                    setStyle("-fx-background-color: #f5b2aa;");
//                else
//                    setStyle("");
//            }
//        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (reader != null) {
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
            service.logoutReader(reader, this);
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

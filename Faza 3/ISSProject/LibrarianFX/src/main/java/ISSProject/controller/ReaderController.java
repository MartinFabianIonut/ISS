package ISSProject.controller;

import ISSProject.domain.Book;
import ISSProject.domain.BookLoan;
import ISSProject.domain.Reader;
import ISSProject.domain.Status;
import ISSProject.service.IObserver;
import ISSProject.service.IService;
import ISSProject.service.MyException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReaderController implements Initializable, IObserver {

    private Reader reader;
    private IService service;
    private Stage loginStage;

    @FXML
    TableView<Book> allBooksTableView;
    @FXML
    TableView<BookLoan> borrowedBooksTableView;

    ObservableList<Book> bookObservableList;
    ObservableList<BookLoan> borrowedBookObservableList;
    @FXML
    TableColumn<Book, String> loan;

    @FXML
    Button seeBorrowedBooksButton;


    public ReaderController() {
        System.out.println("ReaderController created");
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
        // stream only the books that are not borrowed by the current reader
        allBooks = allBooks.stream().filter(book -> book.getStatus() == Status.AVAILABLE).toList();
        bookObservableList = FXCollections.observableArrayList(allBooks);
        allBooksTableView.setItems(bookObservableList);
        allBooksTableView.refresh();
    }

    private void initialiseTable() {
        loan.setCellFactory(new Callback<>() {
            @Override
            public TableCell call(final TableColumn<Book, String> param) {
                return new TableCell<Book, String>() {
                    final Button loanButton = new Button("Loan");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            loanButton.setOnAction(event -> {
                                Book book = getTableView().getItems().get(getIndex());
                                try {
                                    service.loanBook(reader, book);
                                    showAllBooks();
                                } catch (MyException e) {
                                    System.out.println("Loan error " + e);
                                }
                            });
                        }
                        setGraphic(loanButton);
                        setText(null);
                    }
                };
            }
        });
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

    public void showBooks() {
        Platform.runLater(() -> {
            try {
                showAllBooks();
                seeBorrowedBooksButtonAction(new ActionEvent());
            } catch (MyException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void logout() {
        try {
            service.logoutReader(reader);
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

    public void seeBorrowedBooksButtonAction(ActionEvent actionEvent) throws MyException {
        List<BookLoan> borrowedBooks = (List<BookLoan>) service.getAllBookLoans();
        borrowedBooks = borrowedBooks.stream().filter(bookLoan -> Objects.equals(bookLoan.getReader().getId(), reader.getId())).toList();
        borrowedBookObservableList = FXCollections.observableArrayList(borrowedBooks);
        borrowedBooksTableView.setItems(borrowedBookObservableList);
        borrowedBooksTableView.refresh();
    }
}

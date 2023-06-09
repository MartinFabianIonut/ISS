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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReaderController implements Initializable, IObserver {

    @FXML
    Button seeBorrowedBooksButton;
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



    public ReaderController() {
        System.out.println("ReaderController created");
    }

    @FXML
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    @FXML
    public void setService(IService service) {
        this.service = service;
    }

    @FXML
    public void setStage(Stage stage) {
        this.loginStage = stage;
    }

    private void showAvailableBooks() throws MyException {
        List<Book> availableBooks = (List<Book>) this.service.getAllBooks();
        availableBooks = availableBooks.stream().filter(book -> book.getStatus() == Status.AVAILABLE).toList();
        bookObservableList = FXCollections.observableArrayList(availableBooks);
        allBooksTableView.setItems(bookObservableList);
        allBooksTableView.refresh();
    }

    @FXML
    private void showBorrowedBooks() throws MyException {
        List<BookLoan> borrowedBooks = (List<BookLoan>) service.getAllBookLoans();
        borrowedBooks = borrowedBooks.stream().filter(bookLoan -> Objects.equals(bookLoan.getReader().getId(), reader.getId())).toList();
        borrowedBookObservableList = FXCollections.observableArrayList(borrowedBooks);
        borrowedBooksTableView.setItems(borrowedBookObservableList);
        borrowedBooksTableView.refresh();
    }

    private void initialiseTable() {
        loan.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Book,String> call(final TableColumn<Book, String> param) {
                return new TableCell<>() {
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
                                book.setStatus(Status.BORROWED);
                                try {
                                    service.loanBook(reader, book);
                                } catch (MyException e) {
                                    System.out.println("Loan error " + e);
                                }
                            });
                            setGraphic(loanButton);
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    public void init() {
        try {
            showAvailableBooks();
        } catch (MyException e) {
            throw new RuntimeException(e);
        }
    }

    public void showBooks() {
        Platform.runLater(() -> {
            try {
                showAvailableBooks();
                showBorrowedBooks();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseTable();
    }
}

package ISSProject.controller;

import ISSProject.domain.Book;
import ISSProject.domain.BookLoan;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ManagementController implements Initializable, IObserver {
    private IService service;
    private Stage librarianStage;

    @FXML
    TableView<Book> allBooksTableView;

    ObservableList<Book> bookObservableList;
    @FXML
    TableColumn<Book, String> actionForDeleteOrUpdate;

    @FXML
    Button addBookButton;

    @FXML
    TextField titlu, autor;

    @FXML
    TableColumn<Book, String> id, title, author;

    public ManagementController() {
        System.out.println("ReaderController created");
    }

    @FXML
    public void setService(IService service) throws MyException {
        this.service = service;
    }

    @FXML
    public void setStage(Stage stage) {
        this.librarianStage = stage;
    }

    private void showAllBooks() throws MyException {
        List<Book> allBooks = (List<Book>) this.service.getAllBooks();
        bookObservableList = FXCollections.observableArrayList(allBooks);
        allBooksTableView.setItems(bookObservableList);
        allBooksTableView.refresh();
    }

    private void initialiseTable() {
        allBooksTableView.setEditable(true);

        title.setCellFactory(TextFieldTableCell.forTableColumn());
        author.setCellFactory(TextFieldTableCell.forTableColumn());

        actionForDeleteOrUpdate.setCellFactory(new Callback<>() {
            @Override
            public TableCell call(final TableColumn<Book, String> param) {
                return new TableCell<Book, String>() {
                    final Button deleteButton = new Button("Delete");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            deleteButton.setOnAction(event -> {
                                Book book = getTableView().getItems().get(getIndex());
                                try {
                                    service.deleteBook(book);
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Success");
                                    alert.setHeaderText("Success");
                                    alert.setContentText("Book deleted successfully");
                                    alert.showAndWait();
                                    showAllBooks();
                                } catch (MyException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Error");
                                    alert.setContentText(e.getMessage());
                                    alert.showAndWait();
                                }
                            });
                            setGraphic(deleteButton);
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialiseTable();
    }

    public void init() {
        try {
            showAllBooks();
        } catch (MyException e) {
            throw new RuntimeException(e);
        }
    }

    public void showBooks() {
        Platform.runLater(() -> {
            try {
                showAllBooks();
            } catch (MyException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void addBookButtonAction(ActionEvent actionEvent) {
        String title = titlu.getText();
        String author = autor.getText();
        if (Objects.equals(title, "") || Objects.equals(author, "")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please fill all the fields!");
            alert.showAndWait();
            return;
        }
        try {
            Book book = new Book(1, title, author, Status.AVAILABLE);
            service.addBook(book);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Success");
            alert.setContentText("Book added successfully");
            alert.showAndWait();
        } catch (MyException e) {
            System.out.println("Loan error " + e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void onEditCommitTitle(TableColumn.CellEditEvent<Book, String> bookStringCellEditEvent) {
        Book book = allBooksTableView.getSelectionModel().getSelectedItem();
        book.setTitle(bookStringCellEditEvent.getNewValue());
        update(book);
    }

    public void onEditCommitAuthor(TableColumn.CellEditEvent<Book, String> bookStringCellEditEvent) {
        Book book = allBooksTableView.getSelectionModel().getSelectedItem();
        book.setAuthor(bookStringCellEditEvent.getNewValue());
        update(book);
    }

    private void update(Book book) {
        try {
            service.updateBook(book);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Success");
            alert.setContentText("Book updated successfully");
            alert.showAndWait();
        } catch (MyException e) {
            System.out.println("Loan error " + e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}

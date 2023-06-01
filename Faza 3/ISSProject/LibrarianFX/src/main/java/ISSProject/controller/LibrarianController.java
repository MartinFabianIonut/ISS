package ISSProject.controller;

import ISSProject.domain.*;
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
    TableView<BookLoan> borrowedBooksTableView;
    ObservableList<Book> bookObservableList;
    ObservableList<BookLoan> borrowedBookObservableList;
    @FXML
    TableColumn<Book, String> returned;

    @FXML
    Button returnButton, registerButton;

    @FXML
    TextField loadIdTextField, CNPTextField, NumeTextField, AdresaTextField, TelefonTextField, UsernameTextField, PasswordTextField;


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
        List<Book> allBooks = (List<Book>) this.service.getAllBooks();
        allBooks = allBooks.stream().filter(book -> book.getStatus() == Status.AVAILABLE).toList();
        bookObservableList = FXCollections.observableArrayList(allBooks);
        allBooksTableView.setItems(bookObservableList);
        allBooksTableView.refresh();
    }

    private void showAllBookLoans() throws MyException {
        List<BookLoan> allBookLoans = (List<BookLoan>) this.service.getAllBookLoans();
        allBookLoans = allBookLoans.stream().filter(bookLoan -> bookLoan.getStatus() == Status.STILL_BORROWED).toList();
        ObservableList<BookLoan> bookLoanObservableList = FXCollections.observableArrayList(allBookLoans);
        borrowedBooksTableView.setItems(bookLoanObservableList);
        borrowedBooksTableView.refresh();
    }

    private void initialiseTable() {
        returned.setCellFactory(new Callback<>() {
            @Override
            public TableCell call(final TableColumn<Book, String> param) {
                return new TableCell<Book, String>() {
                    final Button returnButton = new Button("Return");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        }
                        setGraphic(returnButton);
                        setText(null);
                    }
                };
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

     public void showBooks() {
        Platform.runLater(() -> {
            try {
                showAllBooks();
            } catch (MyException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void logout() {
        try {
            service.logoutLibrarian(librarian);
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

    public void returnButtonAction(ActionEvent actionEvent) {
        String loan_id = loadIdTextField.getText();
        if(loan_id.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please enter a loan id");
            alert.showAndWait();
            return;
        }
        try {
            service.returnBook(librarian, Integer.parseInt(loan_id));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Success");
            alert.setContentText("Book returned successfully");
            alert.showAndWait();
            loadIdTextField.setText("");
        } catch (MyException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void registerButtonAction(ActionEvent actionEvent) {
        String CNP = CNPTextField.getText();
        String Nume = NumeTextField.getText();
        String Adresa = AdresaTextField.getText();
        String Telefon = TelefonTextField.getText();
        String Username = UsernameTextField.getText();
        String Password = PasswordTextField.getText();
        if(CNP.equals("") || Nume.equals("") || Adresa.equals("") || Telefon.equals("") || Username.equals("") || Password.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
            return;
        }
        try {
            service.registerReader(new Reader(0, CNP, Nume, Adresa, Telefon, Username, Password));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Success");
            alert.setContentText("Reader registered successfully");
            alert.showAndWait();
            CNPTextField.setText("");
            NumeTextField.setText("");
            AdresaTextField.setText("");
            TelefonTextField.setText("");
            UsernameTextField.setText("");
            PasswordTextField.setText("");
        } catch (MyException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
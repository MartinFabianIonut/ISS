package ISSProject.controller;

import ISSProject.HelloApplication;
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
import java.util.ResourceBundle;

public class LibrarianController implements Initializable, IObserver {
    private Librarian librarian;
    private IService service;
    private Stage loginStage;
    private ManagementController managementController;

    @FXML
    TableView<Book> allBooksTableView;
    @FXML
    TableView<BookLoan> borrowedBooksTableView;
    ObservableList<Book> bookObservableList;
    ObservableList<BookLoan> borrowedBookObservableList;
    @FXML
    TableColumn<BookLoan, String> returned;
    @FXML
    Button registerButton, managementButton, seeBorrowedBooksButton;
    @FXML
    TextField CNPTextField, NumeTextField, AdresaTextField, TelefonTextField, UsernameTextField, PasswordTextField;


    public LibrarianController() {
        System.out.println("LibrarianController created");
    }

    @FXML
    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
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
        List<BookLoan> borrowedBooks = (List<BookLoan>) this.service.getAllBookLoans();
        borrowedBooks = borrowedBooks.stream().filter(bookLoan -> bookLoan.getStatus() == Status.STILL_BORROWED).toList();
        borrowedBookObservableList = FXCollections.observableArrayList(borrowedBooks);
        borrowedBooksTableView.setItems(borrowedBookObservableList);
        borrowedBooksTableView.refresh();
    }

    private void initialiseTable() {
        returned.setCellFactory(new Callback<>() {
            @Override
            public TableCell<BookLoan, String> call(final TableColumn<BookLoan, String> param) {
                return new TableCell<>() {
                    final Button returnButton = new Button("Return");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            returnButton.setOnAction(event -> {
                                BookLoan loan = getTableView().getItems().get(getIndex());
                                try {
                                    service.returnBook(librarian, loan.getId());
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Success");
                                    alert.setHeaderText("Success");
                                    alert.setContentText("Book returned successfully");
                                    alert.showAndWait();
                                } catch (MyException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Error");
                                    alert.setContentText(e.getMessage());
                                    alert.showAndWait();
                                }
                            });
                            setGraphic(returnButton);
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
        if(managementController != null) {
            managementController.showBooks();
        }
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

    public void registerButtonAction() {
        String CNP = CNPTextField.getText();
        String Nume = NumeTextField.getText();
        String Adresa = AdresaTextField.getText();
        String Telefon = TelefonTextField.getText();
        String Username = UsernameTextField.getText();
        String Password = PasswordTextField.getText();
        if (CNP.equals("") || Nume.equals("") || Adresa.equals("") || Telefon.equals("") || Username.equals("") || Password.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
            return;
        }
        try {
            boolean exists = service.findByUsername(Username);
            if (exists) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Username already exists");
                alert.showAndWait();
                return;
            }
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

    public void managementButtonAction() {
        // open a new window
        try {
            FXMLLoader managementLoader = new FXMLLoader();
            managementLoader.setLocation(HelloApplication.class.getResource("management.fxml"));
            Stage managementStage = new Stage();
            Scene managementScene;
            try {
                managementScene = new Scene(managementLoader.load(), 1200, 800);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            managementController = managementLoader.getController();
            managementController.setService(service);
            // set stage of librarian
            managementController.init();
            managementStage.setTitle("Managementul bibliotecii");
            managementStage.setScene(managementScene);
            managementStage.show();
            managementStage.setOnCloseRequest(event -> {
                managementController = null;
            });

        } catch (MyException e) {
            throw new RuntimeException(e);
        }
    }
}

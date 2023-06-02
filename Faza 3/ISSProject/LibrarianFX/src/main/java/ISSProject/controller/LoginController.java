package ISSProject.controller;

import ISSProject.domain.Librarian;
import ISSProject.domain.Reader;
import ISSProject.service.IService;
import ISSProject.service.MyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField,verifyField;
    @FXML
    Button loginButton, verifyButton;
    @FXML
    ComboBox<String> comboType;
    @FXML
    Label controlLabel;

    @FXML
    AnchorPane securityPane;
    private IService service;
    private LibrarianController librarianController;
    private ReaderController readerController;
    private Parent parentForLibrarian, parentForReader;
    private final Stage stage = new Stage();

    @FXML
    public void setService(IService service) {
        this.service = service;
    }

    public void setLibrarianController(LibrarianController librarianController) {
        this.librarianController = librarianController;
    }

    public void setReaderController(ReaderController readerController) {
        this.readerController = readerController;
    }

    public void setParentForLibrarian(Parent parent) {
        this.parentForLibrarian = parent;
    }

    public void setParentForReader(Parent parent) {
        this.parentForReader = parent;
    }

    public void init() {
        comboType.getItems().add("Reader");
        comboType.getItems().add("Librarian");
        securityPane.setVisible(false);
    }

    @FXML
    private void loginAction(ActionEvent actionEvent) {
        if (usernameField.getText().length() > 0 && passwordField.getText().length() > 0 && comboType.getValue() != null) {
            try {
                if (comboType.getSelectionModel().getSelectedItem().equals("Reader")) {
                    Reader trying = new Reader(0, null, null, null, null,
                            usernameField.getText(), passwordField.getText());
                    Reader reader = service.authenticateReader(trying, readerController);
                    if (reader != null) {
                        stage.setOnCloseRequest(event -> {
                            readerController.logoutAction();
                        });
                        stage.setTitle("Reader: " + reader);
                        if (stage.getScene() == null)
                            stage.setScene(new Scene(parentForReader, 1200, 800));
                        stage.show();
                        readerController.setReader(reader);
                        readerController.setStage((Stage) usernameField.getScene().getWindow());
                        readerController.init();
                        Stage stage2 = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
                        stage2.close();
                    } else
                        controlLabel.setText("Error: no such reader!");
                } else {
                    securityPane.setVisible(true);
                    return;
                }
            } catch (IllegalArgumentException | MyException e) {
                controlLabel.setText("Error: " + e.getMessage() + "!");
            }
            usernameField.clear();
            passwordField.clear();
            comboType.getSelectionModel().clearSelection();
        } else
            controlLabel.setText("Not all fields are filled!");
    }

    @FXML
    private void verifyAction(ActionEvent actionEvent) {
        try {
            Librarian trying = new Librarian(0, usernameField.getText(), passwordField.getText());
            Librarian librarian = service.authenticateLibrarian(trying, librarianController);
            boolean isOk = service.security(Integer.parseInt(verifyField.getText()));
            if (librarian != null && isOk) {
                stage.setOnCloseRequest(event -> {
                    librarianController.logoutAction();
                });
                stage.setTitle("Librarian: " + librarian);
                if (stage.getScene() == null)
                    stage.setScene(new Scene(parentForLibrarian, 1200, 800));
                stage.show();
                librarianController.setLibrarian(librarian);
                librarianController.setStage((Stage) usernameField.getScene().getWindow());
                librarianController.init();
                Stage stage2 = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
                stage2.close();
            } else
                controlLabel.setText("Error: no such librarian or wrong key!");
        } catch (IllegalArgumentException | MyException e) {
            controlLabel.setText("Error: " + e.getMessage() + "!");
        }
        usernameField.clear();
        passwordField.clear();
        verifyField.clear();
        comboType.getSelectionModel().clearSelection();
        securityPane.setVisible(false);
    }
}

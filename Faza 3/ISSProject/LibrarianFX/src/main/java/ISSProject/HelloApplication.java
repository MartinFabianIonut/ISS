package ISSProject;

import ISSProject.controller.LibrarianController;
import ISSProject.controller.ReaderController;
import ISSProject.network.rpcprotocol.ServicesRpcProxy;
import ISSProject.service.IService;
import ISSProject.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

public class HelloApplication extends Application {
    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";

    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(HelloApplication.class.getResourceAsStream("/library.properties"));
            System.out.println("Reader properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find library.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IService service = new ServicesRpcProxy(serverIP, serverPort);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);

        FXMLLoader librarianLoader = new FXMLLoader(getClass().getResource("bibliotecar.fxml"));
        Parent parentForLibrarian=librarianLoader.load();
        LibrarianController librarianController = librarianLoader.getController();
        librarianController.setService(service);
        loginController.setLibrarianController(librarianController);
        loginController.setParentForLibrarian(parentForLibrarian);

        FXMLLoader readerLoader = new FXMLLoader(getClass().getResource("cititor.fxml"));
        Parent parentForReader=readerLoader.load();
        ReaderController readerController = readerLoader.getController();
        readerController.setService(service);
        loginController.setReaderController(readerController);
        loginController.setParentForReader(parentForReader);

        loginController.init();

        primaryStage.setTitle("MPP server-client app");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
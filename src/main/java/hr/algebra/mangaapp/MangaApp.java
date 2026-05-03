package hr.algebra.mangaapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MangaApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MangaApp.class.getResource("/hr/algebra/mangaapp/view/login.fxml")
        );

        Scene scene = new Scene(loader.load(), 400, 300);

        primaryStage.setTitle("MangaApp - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}

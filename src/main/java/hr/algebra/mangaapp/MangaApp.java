package hr.algebra.mangaapp;

import hr.algebra.mangaapp.util.XmlConfigUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MangaApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MangaApp.class.getResource("/hr/algebra/mangaapp/view/login.fxml")
        );

        Dimension2D smallScreen = XmlConfigUtils.getSmallScreen();
        Scene scene = new Scene(
                loader.load(),
                smallScreen.getWidth(),
                smallScreen.getHeight()
        );

        primaryStage.setTitle("MangaApp - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}

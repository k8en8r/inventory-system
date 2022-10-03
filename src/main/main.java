package main;

import controller.FirstScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import model.InHouse;
import model.Part;

/**
 * FUTURE ENHANCEMENT. Allow adding and modifying of parts and products from the tables on the first screen as opposed
 * to utilizing new screens to add and modify parts and products.
 */
public class main extends Application {
    @Override

    public void start(Stage stage) throws Exception {
        // open first screen
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/FirstScreen.fxml"));
        stage.setTitle("First Screen");
        Scene scene = new Scene(root, 900, 360);
        scene.getRoot().setStyle("-fx-font-family: 'serif'");
        stage.setScene(scene);
        stage.show();

    }



    public static void main(String[] args) {


        launch(args);
    }


}


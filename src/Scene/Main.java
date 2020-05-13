package Scene;

import common.Loader;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Coordinate;
import model.Street;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    public void listen(Observable o) {

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene.fxml"));
        BorderPane pane = loader.load();

//        Loader loader = new Loader();
//        Map map = new Map();
//        loader.getStreets().forEach(street -> map.add(street));

        Controller controller = loader.getController();
        controller.loadData(new Loader());

        Scene scene = new Scene(pane, 1000, 800);

        primaryStage.setTitle("xsalih01 (a xvever12)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

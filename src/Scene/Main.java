package Scene;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.scene.Group;
import javafx.scene.Scene;
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
        Random rand = new Random();

        Map map = new Map();

        // mock data
        Coordinate s1c1 = new Coordinate(100, 100);
        Coordinate s1c2 = new Coordinate(100, 200);
        Coordinate s1c3 = new Coordinate(250, 200);
        Street s1 = new Street("first", Arrays.asList(s1c1, s1c2, s1c3));

        Coordinate s2c1 = new Coordinate(380, 200);
        Coordinate s2c2 = new Coordinate(280, 200);
        Coordinate s2c3 = new Coordinate(280, 350);
        Coordinate s2c4 = new Coordinate(400, 350);
        Street s2 = new Street("second", Arrays.asList(s2c1, s2c2, s2c3, s2c4));

        Coordinate s3c1 = new Coordinate(400, 100);
        Coordinate s3c2 = new Coordinate(400, 200);
        Coordinate s3c3 = new Coordinate(500, 200);
        Coordinate s3c4 = new Coordinate(600, 200);
        Coordinate s3c5 = new Coordinate(600, 300);
        Street s3 = new Street("third", Arrays.asList(s3c1, s3c2, s3c3, s3c4, s3c5));
        map.add(s1);
        map.add(s2);
        map.add(s3);

//        Group root = new Group(map);
        Scene scene = new Scene(map, 800, 700);

        primaryStage.setTitle("xsalih01 (a xvever12)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

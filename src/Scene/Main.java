package Scene;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    public void listen(Observable o) {

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene.fxml"));
        BorderPane pane = loader.load();

        Controller controller = loader.getController();
        controller.viewDidLoad(new ViewModel());

        Scene scene = new Scene(pane, 1000, 800);

        primaryStage.setTitle("xsalih01 (a xvever12)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

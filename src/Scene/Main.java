package Scene;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application implements SceneDelegate {

    private Stage primaryStage;

    public void listen(Observable o) {

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        resetScene();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void resetScene() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene.fxml"));
        try {
            BorderPane pane = loader.load();
            Controller controller = loader.getController();
            controller.viewDidLoad(new ViewModel());
            controller.delegate = this;

            Scene scene = new Scene(pane, 1200, 1000);

            primaryStage.setTitle("xsalih01 a xvever12");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

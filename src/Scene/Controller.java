package Scene;

import common.Loader;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import model.Street;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Controller {
    @FXML
    private Group content;

    private List<StreetView> streets = new ArrayList<>();

    void loadData(Loader loader) {
        loader.getStreets().forEach(street -> this.add(street));
    }

    void add(Street street) {
        StreetView view = new StreetView(street);
        for (Line line: view.lines) {
            this.content.getChildren().add(line);
        }
        streets.add(view);
    }
}

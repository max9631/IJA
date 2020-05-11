package Scene;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import model.Street;

import java.util.ArrayList;

public class Map extends Group {
    private ArrayList<StreetView> streets = new ArrayList<>();

    public Map() {

    }

    void add(Street street) {
        StreetView view = new StreetView(street);
        for (Line line: view.lines) {
            this.getChildren().add(line);
        }
        streets.add(view);
    }
}

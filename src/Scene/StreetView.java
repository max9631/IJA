package Scene;

import javafx.scene.shape.Line;
import model.Coordinate;
import model.Street;

import java.util.ArrayList;

public class StreetView {

    public ArrayList<Line> lines;

    private Street street;

    public StreetView(Street street) {
        this.street = street;
        Coordinate c = null;
        lines = new ArrayList<>();

        for (int i = 0; i < street.getCoordinates().size() ; i++) {
            Coordinate coord = street.getCoordinates().get(i);
            if (c != null) {
                Line line = new Line(c.getX(), c.getY(), coord.getX(), coord.getY());
                int radius = 5;
                line.setStrokeWidth(2*radius);
                line.setStroke(Color.GRAY);
                line.setOnMouseClicked(this::userSelectedStreet);
                lines.add(line);
            }
            c = coord;
        }
    }
}

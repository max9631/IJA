package Scene;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.Coordinate;
import model.Street;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

interface StreetViewDelegate {
    void didSelect(StreetView street);
}

public class StreetView {

    public ArrayList<Line> lines;

    private Street street;

    public StreetViewDelegate delegate;

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
                line.setStroke(Color.LIGHTGRAY);
                line.setOnMouseClicked(this::userSelectedStreet);
                lines.add(line);
            }
            c = coord;
        }
    }

    public Street getStreet() {
        return street;
    }

    private void userSelectedStreet(Event event) {
        if (delegate != null){
            this.delegate.didSelect(this);
        }
    }
}

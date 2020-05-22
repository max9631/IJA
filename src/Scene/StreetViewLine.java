package Scene;

import javafx.event.Event;
import javafx.scene.shape.Line;
import model.Coordinate;
import model.Street;

import java.util.AbstractMap;

public class StreetViewLine extends Line {

    private Street street;
    private int index;


    public StreetViewLine(Street street, int index, double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        this.index = index;
        this.street = street;
    }

    public boolean isClosed() {
        return street.getCoordinates().get(index).getValue();
    }

    public void setIsClosed(boolean isClosed) {
        AbstractMap.SimpleImmutableEntry<Coordinate, Boolean> entry = street.getCoordinates().get(index);
        AbstractMap.SimpleImmutableEntry<Coordinate, Boolean> newEntry = new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), new Boolean(isClosed));
        street.getCoordinates().set(index, newEntry);
    }
}

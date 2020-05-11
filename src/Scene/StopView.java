package Scene;

import javafx.scene.shape.Rectangle;
import model.Stop;

public class StopView extends Rectangle {
    Stop stop;

    public StopView(Stop stop) {
        super(
            stop.getCoordinate().getX() - 5,
            stop.getCoordinate().getY() - 5,
            stop.getCoordinate().getX() + 5,
            stop.getCoordinate().getY() + 5
        );
        this.stop = stop;
    }
}

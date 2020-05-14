package Scene;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Stop;

public class StopView extends Rectangle {
    Stop stop;

    public StopView(Stop stop) {
        super(
            stop.getCoordinate().getX() - 5,
            stop.getCoordinate().getY() - 5,
            10,
            10
        );
        this.setFill(Color.YELLOW);
        this.stop = stop;
    }
}

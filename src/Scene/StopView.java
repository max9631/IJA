package Scene;

import javafx.event.Event;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Stop;

interface StopViewDelegate {
    void didSelectStop(StopView view);
}

public class StopView extends Rectangle {
    private Stop stop;

    public StopViewDelegate delegate;

    public StopView(Stop stop) {
        super(
            stop.getCoordinate().getX() - 5,
            stop.getCoordinate().getY() - 5,
            10,
            10
        );
        this.setFill(Color.YELLOW);
        this.stop = stop;
        this.setOnMouseClicked(this::didSelectStop);
    }

    public Stop getStop() {
        return stop;
    }

    public void didSelectStop(Event event) {
        event.consume();
        if (delegate != null) {
            delegate.didSelectStop(this);
        }
    }
}

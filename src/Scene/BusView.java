package Scene;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import model.TransportLine;

public class BusView extends Circle {
    TransportLine line;
    private double v = 10;
    private Paint paint = new Color(255, 0,0,1.0);

    public BusView(TransportLine line) {
        super(8, new Color(255, 0,0,1.0));
        this.line = line;
    }
}



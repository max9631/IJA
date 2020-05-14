package Scene;

import com.sun.org.apache.xml.internal.security.encryption.CipherReference;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import model.Coordinate;
import model.Stop;
import model.Street;
import model.TransportLine;

import java.util.AbstractMap;
import java.util.List;

interface BusViewDelegate{
    Circle getBus();
}

public class BusView extends Circle {
    private TransportLine line;
    private Coordinate startPos;
    private double velocity = 10;
    private Circle bus;
    private String busId;

    private Paint paint = new Color(1.0, 0,0,1.0);


    public BusView(TransportLine line) {
        super(8, new Color(1.0, 0.0,0.0, 1.0));
        busId = line.getId();
        this.line = line;

        List<AbstractMap.SimpleImmutableEntry<Street, Stop>> tmpLineInfo = line.getRoute();
        startPos = tmpLineInfo.get(0).getKey().begin();
        bus = new Circle(startPos.getX(), startPos.getY(), 8, paint);
    }

    public Coordinate getStartPos() {
        return startPos;
    }

    public String getBusId() {
        return busId;
    }

    public Circle getBus(){
        return this.bus;
    }
}



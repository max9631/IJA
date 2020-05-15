package Scene;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import model.Coordinate;
import model.Stop;
import model.Street;
import model.TransportLine;


import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class BusView {
    private TransportLine line;
    private Coordinate position;
    private Coordinate textPosition;
    private double velocity = 10;
    private Circle busIcon;
    private Text busText;
    private int startTime;
    private Dispatching dispatcher;
    private Paint paint = new Color(1.0, 0, 0, 1.0);

    public void end(){
        this.line = null;
        this.position = null;
        this.textPosition = null;
        this.busIcon = null;
        this.busText = null;
        this.dispatcher = null;
        this.paint = null;
    }

    public BusView(TransportLine line, int startTime, double timeMultiplier) {
        this.line = line;
        List<AbstractMap.SimpleImmutableEntry<Street, Stop>> tmpLineInfo = line.getRoute();

        position = tmpLineInfo.get(0).getKey().begin();
        //System.out.println("Multiplier is: " + timeMultiplier);
        //timeMultiplier -= 1;
        //position.setX(position.getX()+(int)timeMultiplier);
        //position.setY(position.getY()+(int)timeMultiplier);

        busText = new Text(position.getX()-3,position.getY()+5,line.getId());
        textPosition = new Coordinate(position.getX()-3, position.getY()+5);
        busIcon = new Circle(position.getX(), position.getY(), 8, paint);
        this.startTime = startTime;
    }

    public Coordinate getPosition() {
        return position;
    }

    public Coordinate getTextPosition() {
        return textPosition;
    }

    public void setBusIcon(Coordinate position){
        busIcon = new Circle(position.getX(), position.getY(), 8, paint);
    }

    public Circle getBusIcon() {
        return busIcon;
    }

    public TransportLine getLine() {
        return line;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setPosition(Coordinate position, Coordinate textPosition) {
        this.position = position;
        this.textPosition = textPosition;
    }

    public Text getBusText() {
        return busText;
    }

    public List<Node> getBus(){
        ArrayList<Node> busInfo = new ArrayList<Node>();
        busInfo.add(this.busIcon);
        busInfo.add(this.busText);
        return busInfo;
    }
}



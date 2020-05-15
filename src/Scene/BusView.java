package Scene;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import model.Coordinate;
import model.Stop;
import model.Street;
import model.TransportLine;


import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

interface BusViewDelegate {
    void userSelected(BusView view);
}

public class BusView {
    private TransportLine line;
    private Coordinate position;
    private Coordinate textPosition;
    private int velocity = 10;
    private Circle busIcon;
    private Text busText;
    private int startTime;
    private Dispatching dispatcher;
    private Paint paint = new Color(1.0, 0, 0, 1.0);
    private ArrayList<Coordinate> routeCoords;
    private ArrayList<Line> routeLines;

    public BusViewDelegate delegate;

    public BusView(TransportLine line, int startTime, double timeMultiplier) {
        this.line = line;
        SimpleImmutableEntry<Street, Stop> lastEntry = null;
        routeCoords = new ArrayList<>();
        for (SimpleImmutableEntry<Street, Stop> path: line.getRoute()) {
            Street street = path.getKey();
            if (lastEntry == null) {
                lastEntry = path;
                routeCoords.add(path.getValue().getCoordinate());
                continue;
            }
            routeCoords.add(Routing.intersection(lastEntry.getKey(), street));
            lastEntry = path;
        }
        routeCoords.add(lastEntry.getValue().getCoordinate());

        routeLines = new ArrayList<>();
        Coordinate c = null;
        for (int i = 0; i < routeCoords.size() ; i++) {
            Coordinate coord = routeCoords.get(i);
            if (c != null) {
                Line routeLine = new Line(c.getX(), c.getY(), coord.getX(), coord.getY());
                int radius = 1;
                routeLine.setStrokeWidth(2*radius);
                routeLine.setStroke(Color.GREEN);
                routeLines.add(routeLine);
            }
            c = coord;
        }

        position = getFirstPoint();
        //System.out.println("Multiplier is: " + timeMultiplier);
        //timeMultiplier -= 1;
        //position.setX(position.getX()+(int)timeMultiplier);
        //position.setY(position.getY()+(int)timeMultiplier);

        busText = new Text(position.getX()-3,position.getY()+5,line.getId());
        textPosition = new Coordinate(position.getX()-3, position.getY()+5);
        busIcon = new Circle(position.getX(), position.getY(), 8, paint);
        busIcon.setOnMouseClicked(this::userSelectedBus);
        busText.setOnMouseClicked(this::userSelectedBus);
        this.startTime = startTime;
    }

    public Coordinate getFirstPoint(){
        return this.routeCoords.get(0);
    }

    public ArrayList<Coordinate> getRouteCoords() {
        return routeCoords;
    }

    private int coordIndex = 0;

    public int getCoordIndex() {
        return coordIndex;
    }

    private Coordinate nextPoint;

    public void setNextPoint(){
        if(this.routeCoords.size() <= coordIndex){
            nextPoint = null;
        }
        nextPoint = routeCoords.get(this.coordIndex++);
    }

    public Coordinate getNextPoint() {
        return nextPoint;
    }

    public Coordinate getCurrentPoint(){
        if(this.routeCoords.size() <= coordIndex){
            return null;
        }
        return this.routeCoords.get(this.coordIndex);
    }


    public void end(){
        this.line = null;
        this.position = null;
        this.textPosition = null;
        this.busIcon = null;
        this.busText = null;
        this.dispatcher = null;
        this.paint = null;
    }

    public Coordinate getPosition() {
        return position;
    }


    public Circle getBusIcon() {
        return busIcon;
    }

    public TransportLine getLine() {
        return line;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public Text getBusText() {
        return busText;
    }

    public List<Line> getRouteLines() {
        return routeLines;
    }

    public String getStopItinerary() {
        List<String> routes = line
                .getRoute()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getValue().getId() + ": ") // TODO: Add estimated arival time
                .collect(Collectors.toList());
        System.out.println(String.join("\n\n", routes));
        return String.join("\n", routes);
    }

    public List<Node> getBus(){
        ArrayList<Node> busInfo = new ArrayList<Node>();
        busInfo.add(this.busIcon);
        busInfo.add(this.busText);
        return busInfo;
    }

    public void userSelectedBus(Event event) {
        event.consume();
        if (delegate != null) {
            delegate.userSelected(this);
        }

    }
}



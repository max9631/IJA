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
    private double velocity = 10;
    private Circle busIcon;
    private Text busText;
    private Dispatching dispatcher;
    private Paint paint = new Color(1.0, 0, 0, 1.0);
    private ArrayList<Coordinate> routeCoords;
    private ArrayList<Line> routeLines;

    public BusViewDelegate delegate;

    public BusView(TransportLine line, Dispatching dispatcher) {
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

        List<SimpleImmutableEntry<Street, Stop>> tmpLineInfo = line.getRoute();
        position = tmpLineInfo.get(0).getKey().begin();
        busText = new Text(position.getX()-3,position.getY()+5,line.getId());
        textPosition = new Coordinate(position.getX()-3, position.getY()+5);
        busIcon = new Circle(position.getX(), position.getY(), 8, paint);
        busIcon.setOnMouseClicked(this::userSelectedBus);
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



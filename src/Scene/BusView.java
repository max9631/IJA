package Scene;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
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
    private Circle busIcon;
    private Text busText;

    private TransportLine line;
    private Coordinate currentPosition;
    private int velocity = 10;
    private int startingTimestamp;

    int nextCoordinateIndex = 0;
    private ArrayList<Coordinate> routeCoords;
    private ArrayList<Line> routeLines;

    public BusViewDelegate delegate;

    public BusView(TransportLine line, int startingTimestamp) {
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
        nextCoordinateIndex = 1;
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
        currentPosition = routeCoords.get(0);
        busText = new Text(currentPosition.getX()-3,currentPosition.getY()+5,line.getId());
        busIcon = new Circle(currentPosition.getX(), currentPosition.getY(), 8, new Color(1.0, 0, 0, 1.0));
        busIcon.setOnMouseClicked(this::userSelectedBus);
        busText.setOnMouseClicked(this::userSelectedBus);
        this.startingTimestamp = startingTimestamp;
        setCurrentPosition(routeCoords.get(0));
    }

    public void setCurrentPosition(Coordinate position) {
        currentPosition = position;
        busIcon.setCenterX(position.getX());
        busIcon.setCenterY(position.getY());
        busText.setX(position.getX()-(busText.getLayoutBounds().getWidth()/2));
        busText.setY(position.getY()+(busText.getLayoutBounds().getHeight()/4));
    }

    public String getStopItinerary() {
        List<String> routes = line
                .getRoute()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getValue().getId() + ": ") // TODO: Add estimated arival time
                .collect(Collectors.toList());
        return String.join("\n", routes);
    }

    public ArrayList<Line> getRouteLines() {
        return routeLines;
    }

    public void setStroke(Paint color) {
        busIcon.setStroke(color);
    }

    private Coordinate getNextCoordinate() {
        return nextCoordinateIndex < routeCoords.size() ? routeCoords.get(nextCoordinateIndex) : null;
    }

    private Street getCurrentStreet() {
        int index = nextCoordinateIndex - 1;
        return index < line.getRoute().size() ? line.getRoute().get(index).getKey() : null;
    }

    public List<Node> getNodes(){
        ArrayList<Node> busInfo = new ArrayList<>();
        busInfo.add(busIcon);
        busInfo.add(busText);
        return busInfo;
    }

    public void userSelectedBus(Event event) {
        event.consume();
        if (delegate != null) {
            delegate.userSelected(this);
        }
    }

    public boolean reachedEnd() {
        return currentPosition.equals(routeCoords.get(routeCoords.size()-1));
    }

    public void moveByTime(int timeDelta) {
        if (timeDelta < 1) return;
        Street currentStreet = getCurrentStreet();
        int velocity = currentStreet.velocity(this.velocity);
        double estimatedDistanceTravel = (double) velocity * timeDelta;
        double distanceUntilNextStreet = Routing.distance(currentPosition, getNextCoordinate());
        if (estimatedDistanceTravel < distanceUntilNextStreet) {
            setCurrentPosition(Routing.move(currentPosition, getNextCoordinate(), estimatedDistanceTravel));
        } else {
            setCurrentPosition(getNextCoordinate());
            nextCoordinateIndex++;
            if (!reachedEnd()) {
                int timeForNextStreetTravel = (int) distanceUntilNextStreet / velocity;
                moveByTime(timeDelta - timeForNextStreetTravel);
            }
        }
    }
}



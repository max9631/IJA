package Scene;

import common.Formatter;
import common.Routing;
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


import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;

interface BusViewDelegate {
    void userSelected(BusView view);
    List<Coordinate> getClosedCoordinates();
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
        if (lastEntry.getValue() != null) {
            routeCoords.add(lastEntry.getValue().getCoordinate());
        }
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
        busIcon = new Circle(currentPosition.getX(), currentPosition.getY(), 8,  line.isAlternativeTransport() ? Color.BLUE : Color.RED);
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
        if (reachedEnd()) return "";
        List<String> itinerary = new ArrayList<>();
        List<SimpleImmutableEntry<Street, Stop>> route = line.getRoute();
        Street street = route.get(nextCoordinateIndex - 1).getKey();
        Stop stop = route.get(nextCoordinateIndex - 1).getValue();
        int velocity = street.velocity(this.velocity);
        if (stop != null && Routing.isOnLine(currentPosition, getNextCoordinate(), stop.getCoordinate())) {
            String time = "-";
            if (velocity != 0) {
                double timeLeft = Routing.distance(currentPosition, stop.getCoordinate()) / velocity;
                double estimatedArrival = Dispatching.shared.getTimestamp() + timeLeft;
                time = Formatter.formatTime(estimatedArrival);
            }
            itinerary.add(stop.getId() + ": " + time);
        }
        double lastTime = Dispatching.shared.getTimestamp() + (Routing.distance(currentPosition, getNextCoordinate()) / velocity);
        Coordinate lastCoordinate = getNextCoordinate();
        boolean velocityWasZero = false;

        for (int i = nextCoordinateIndex; i < route.size(); i++) {
            street = route.get(i).getKey();
            stop = route.get(i).getValue();
            velocity = street.velocity(this.velocity);
            velocityWasZero = velocityWasZero || velocity == 0;
            if (stop != null) {
                String time = "-";
                if (velocity != 0 && !velocityWasZero) {
                    double timeLeft = Routing.distance(lastCoordinate, stop.getCoordinate()) / velocity;
                    double estimatedArrival = lastTime + timeLeft;
                    time = Formatter.formatTime(estimatedArrival);
                }
                itinerary.add(stop.getId() + ": " + time);
            }
            if (i < route.size()) {
                lastTime += Routing.distance(lastCoordinate, routeCoords.get(i + 1)) / velocity;
                lastCoordinate = routeCoords.get(i + 1);
            }
        }
        return String.join("\n\n", itinerary);
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

    public TransportLine getTransportLine() {
        return line;
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

    public boolean hasClosedStreetInRoute() {
        if (delegate == null) return false;
        for (int i = nextCoordinateIndex; i < line.getRoute().size(); i++) {
            Coordinate from = routeCoords.get(i - 1);
            Coordinate to = routeCoords.get(i);
            for (Coordinate closedCoord: delegate.getClosedCoordinates()) {
                if (Routing.isOnLine(from, to , closedCoord)) {
                    return true;
                }
            }

        }
        return false;
    }

    public void moveByTime(int timeDelta) {
        if (timeDelta < 1) return;
        if (hasClosedStreetInRoute()) return;
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



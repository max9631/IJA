package model;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;

public class TransportLine{
    private String id;
    private List<SimpleImmutableEntry<Street, Stop>> route;
    private double interval;
    private ArrayList<Coordinate> routeCoords;

    public TransportLine(String id, double interval) {
        this.id = id;
        this.interval = interval;
        this.route = new ArrayList<>();
        this.routeCoords = new ArrayList<>();
    }

    private Street lastStreet() {
        int size = route.size();
        if (size == 0) {
            return null;
        }
        return route.get(size - 1).getKey();
    }


    public ArrayList<Coordinate> calculateRoute (){
        return new ArrayList<>();
    }


    public boolean addStop(Stop stop) {
        Street street = stop.getStreet();
        boolean routeIsEmpty = route.size() == 0;
        boolean onSameStreet = street.equals(lastStreet());
        boolean streetFollowsLast = routeIsEmpty ? false : lastStreet().follows(street);
        if (routeIsEmpty || onSameStreet || streetFollowsLast) {
            route.add(new SimpleImmutableEntry<>(street, stop));
            return true;
        }
        return false;
    }

    public boolean addStreet(Street street) {
        if (route.size() == 0) {
            return  false;
        }
        if (lastStreet().follows(street)) {
            route.add(new SimpleImmutableEntry<>(street, null));
            return true;
        }
        return false;
    }

    public List<SimpleImmutableEntry<Street, Stop>> getRoute() {
        return new ArrayList<>(route);
    }

    public String getId() {
        return id;
    }

    public double getInterval() {
        return interval;
    }
}

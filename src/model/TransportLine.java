package model;

import Scene.Routing;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;

public class TransportLine{
    private String id;
    private List<SimpleImmutableEntry<Street, Stop>> route;
    private int interval;
    private int lastGen = 0;


    public void setLastGen(int busTime) {
        this.lastGen = busTime;
    }

    public int getLastGen() {
        return lastGen;
    }

    public TransportLine(String id, int interval) {
        this.id = id;
        this.interval = interval;
        this.route = new ArrayList<>();
    }

    public Street lastStreet() {
        int size = route.size();
        if (size == 0) {
            return null;
        }
        return route.get(size - 1).getKey();
    }


    public boolean addStop(Stop stop) {
        Street street = stop.getStreet();
        boolean routeIsEmpty = route.size() == 0;
        boolean onSameStreet = street.equals(lastStreet());
        boolean streetFollowsLast = routeIsEmpty ? false : Routing.intersection(lastStreet(), street) != null;
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
        if (Routing.intersection(lastStreet(), street) != null) {
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

    public int getInterval() {
        return interval;
    }
}

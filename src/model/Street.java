package model;

import java.util.*;
import java.lang.Math;
import java.util.stream.Collectors;

public class Street {
    private String id;
    private List<AbstractMap.SimpleImmutableEntry<Coordinate, Boolean>> coordinates;
    private List<Stop> stops;
    private double frictionCoefficient = 0;

    public Street(String id, List<Coordinate> coordinates) {
        this.id = id;
        this.stops = new ArrayList<>();
        this.coordinates = coordinates.stream()
                .map(coordinate -> new AbstractMap.SimpleImmutableEntry<>(coordinate, new Boolean(false)))
                .collect(Collectors.toList());
//        this.coordinates = new ArrayList<>(coordinates);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Street myStreet = (Street) o;
        return Objects.equals(id, myStreet.id);
    }

    public String toString() {
        return id;
    }

    public String getId() {
        return id;
    }

    public List<AbstractMap.SimpleImmutableEntry<Coordinate, Boolean>> getCoordinates() {
        return coordinates;
    }

    public List<Stop> getStops() {
        return this.stops;
    }

    public double getFrictionCoefficient() {
        return frictionCoefficient;
    }

    public void setFrictionCoefficient(double frictionCoeficient) {
        this.frictionCoefficient = frictionCoeficient;
    }

    public int velocity(int initialVelocity){ return initialVelocity - (int)(initialVelocity * frictionCoefficient); }

    public List<Coordinate> getClosedCoordinates() {
        List<Coordinate> closed = new ArrayList<>();
        for (int i = 1; i < getCoordinates().size(); i++) {
            if (getCoordinates().get(i).getValue()) {
                int x = (getCoordinates().get(i-1).getKey().getX() + getCoordinates().get(i).getKey().getX())/2;
                int y = (getCoordinates().get(i-1).getKey().getY() + getCoordinates().get(i).getKey().getY())/2;
                closed.add(new Coordinate(x, y));
            }
        }
        return closed;
    }

    public boolean addStop(Stop stop) {
        if (this.stops.contains(stop)) {
            return false;
        }
        if (coordinates.size() == 0) {
            return false;
        }
        Coordinate c = coordinates.get(0).getKey();
        Coordinate stopCoor = stop.getCoordinate();
        if (coordinates.size() == 1) {
            if (c.equals(stopCoor)) {
                this.stops.add(stop);
                stop.setStreet(this);
                return true;
            }
        } else {
            for (int i = 1; i < coordinates.size(); i++) {
                Coordinate next = coordinates.get(i).getKey();
                if (isOnLine(c, next, stopCoor)) {
                    this.stops.add(stop);
                    stop.setStreet(this);
                    return true;
                }
                c = next;
            }
        }
        return false;
    }

    boolean isOnLine(Coordinate from, Coordinate to, Coordinate test) {
        int dxc = test.getX() - from.getX();
        int dyc = test.getY() - from.getY();
        int dxl = to.getX() - from.getX();
        int dyl = to.getY() - from.getY();
        int cross = dxc * dyl - dyc * dxl;
        if (cross != 0) {
            return false;
        }
        if (Math.abs(dxl) >= Math.abs(dyl))
            return dxl > 0 ?
                    from.getX() <= test.getX() && test.getX() <= to.getX() :
                    to.getX() <= test.getX() && test.getX() <= from.getX();
        else
            return dyl > 0 ?
                    from.getY() <= test.getY() && test.getY() <= to.getY() :
                    to.getY() <= test.getY() && test.getY() <= from.getY();
    }

    public Coordinate begin() {
        if (coordinates.size() == 0){
            return null;
        }
        return this.coordinates.get(0).getKey();
    }

    public Coordinate end() {
        if (coordinates.size() == 0){
            return null;
        }
        return this.coordinates.get(this.coordinates.size() - 1).getKey();
    }
}

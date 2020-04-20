package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.lang.Math;

public class Street {
    private String id;
    private List<Coordinate> coordinates;
    private List<Stop> stops;

    public Street(String id, List<Coordinate> coordinates) {
        this.id = id;
        this.stops = new ArrayList<>();
        this.coordinates = new ArrayList<>(coordinates);
//        this.coordinates.stream()
//        Arrays.stream(coordinates)
//                .forEach((coordinate) -> this.coordinates.add(coordinate));
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

    public List<Coordinate> getCoordinates() {
        return new ArrayList<>(coordinates);
    }

    public List<Stop> getStops() {
        return this.stops;
    }

    public boolean addStop(Stop stop) {
        if (this.stops.contains(stop)) {
            return false;
        }
        if (coordinates.size() == 0) {
            return false;
        }
        Coordinate c = coordinates.get(0);
        Coordinate stopCoor = stop.getCoordinate();
        if (coordinates.size() == 1) {
            if (c.equals(stopCoor)) {
                this.stops.add(stop);
                stop.setStreet(this);
                return true;
            }
        } else {
            for (int i = 1; i < coordinates.size(); i++) {
                Coordinate next = coordinates.get(i);
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
        return this.coordinates.get(0);
    }

    public Coordinate end() {
        if (coordinates.size() == 0){
            return null;
        }
        return this.coordinates.get(this.coordinates.size() - 1);
    }

    public boolean follows(Street s) {
        boolean b1 = this.begin().equals(s.begin());
        boolean b2 = this.begin().equals(s.end());
        boolean b3 = this.end().equals(s.begin());
        boolean b4 = this.end().equals(s.end());
        return b1 || b2 || b3 || b4;
    }
}

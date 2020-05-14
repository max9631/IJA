package model;

import java.util.Objects;

public class Stop{

    private String id;
    private Coordinate coordinate;
    private Street street;

    public Stop(String id) {
        this.id = id;
        this.coordinate = null;
        this.street = null;
    }

    public Stop(String id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
        this.street = null;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop myStop = (Stop) o;
        return Objects.equals(id, myStop.id);
    }

    public String toString() {
        return "stop(" + id + ")";
    }

    public String getId() {
        return id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setStreet(Street s) {
        street = s;
    }

    public Street getStreet() {
        return this.street;
    }
}

package model;

import java.util.ArrayList;

public class Map  {
    private ArrayList<Street> streets;

    public Map() {
        this.streets = new ArrayList<Street>();
    }

    public void addStreet(Street s) {
        streets.add(s);
    }

    public Street getStreet(String id) {
        return streets
                .stream()
                .filter((s) -> s.getId().equals(id))
                .findFirst()
                .get();
    }
}

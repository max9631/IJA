package Scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Coordinate;
import model.Stop;
import model.Street;
import model.TransportLine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ViewModel {
    private List<Street> streets;
    private List<Stop> stops;
    private List<TransportLine> transportLines;

    public ViewModel() {
        JSONParser parser = new JSONParser();
        try {
            InputStream inputData = getClass().getResourceAsStream("/PublicTransport.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputData));

            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray jsonStreets = (JSONArray) jsonObject.get("streets");
            Stream<JSONObject> jsonStreetsStream = jsonStreets.stream();
            this.streets = jsonStreetsStream
                    .map(jsonStreet -> {
                        List<Coordinate> coordinates = this.coordinatesFor((JSONArray) jsonStreet.get("coordinates"));
                        String streetName = (String) jsonStreet.get("name");
                        return new Street(streetName, coordinates);
                    }).collect(Collectors.toList());


            JSONArray jsonStops = (JSONArray) jsonObject.get("stops");
            Stream<JSONObject> jsonStopsStream = jsonStops.stream();
            this.stops = jsonStopsStream
                    .map(jsonStop -> {
                        JSONObject jsonCoordinate = (JSONObject) jsonStop.get("coordinate");
                        Coordinate coordinate = this.coordinateFor(jsonCoordinate);
                        String name = (String) jsonStop.get("name");
                        Stop stop = new Stop(name, coordinate);
                        stop.setStreet(this.getStreetFor((String) jsonStop.get("street")));
                        return stop;
                    }).collect(Collectors.toList());


            JSONArray jsonLines = (JSONArray) jsonObject.get("lines");
            Stream<JSONObject> jsonLinesStream = jsonLines.stream();
            this.transportLines = jsonLinesStream
                    .map(jsonStop -> {
                        String name = (String) jsonStop.get("name");
                        Number interval = (Number) jsonStop.get("interval");
                        TransportLine line = new TransportLine(name, interval.intValue());
                        JSONArray jsonRoutes = (JSONArray) jsonStop.get("route");
                        Stream<JSONObject> jsonRoutesStream = jsonRoutes.stream();
                        jsonRoutesStream.forEach(jsonRoute -> {
                            Stop stop = this.getStopFor((String) jsonRoute.get("stop"));
                            if (stop != null) {
                                line.addStop(stop);
                            }
                            Street street = this.getStreetFor((String) jsonRoute.get("street"));
                            if (street != null) {
                                line.addStreet(street);
                            }
                        });
                        return line;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Coordinate> coordinatesFor(JSONArray jsonCoordinates) {
        Stream<JSONObject> coordinateStream = jsonCoordinates.stream();
        return coordinateStream
                .map(this::coordinateFor).collect(Collectors.toList());
    }

    private Coordinate coordinateFor(JSONObject jsonCoordinate) {
        Long x = (Long) jsonCoordinate.get("x");
        Long y = (Long) jsonCoordinate.get("y");
        return new Coordinate(x.intValue(), y.intValue());
    }

    public List<Street> getStreets() {
        return streets;
    }

    public Street getStreetFor(String key) {
        if (key == null) return null;
        return getStreets()
                .stream()
                .filter(str -> str.getId().equals(key))
                .findFirst()
                .get();
    }

    public List<Stop> getStops() {
        return this.stops;
    }

    public Stop getStopFor(String key) {
        if (key == null) return null;
        return getStops()
                .stream()
                .filter(stop -> stop.getId().equals(key))
                .findFirst()
                .get();
    }

    public List<TransportLine> getTransportLines() {
        return this.transportLines;
    }
}

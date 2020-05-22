package Scene;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Routing;
import model.Coordinate;
import model.Stop;
import model.Street;
import model.TransportLine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

interface ViewModelDelegate {
    void updateAlternativeLine(BusView view);
}

public class ViewModel {
    private List<Street> streets;
    private List<Stop> stops;
    private List<TransportLine> transportLines;


    public String closeStreetString = "Uzavřít ulice";
    public String openStreetString = "Otevřít ulice";

    public String defineNewRouteString = "Definovat linku";
    public String completeNewRouteString = "Dokončit";
    public String removeRouteString = "Odstranit";

    private TransportLine alternativeLine;

    public ViewModelDelegate delegate;

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

    public boolean isInDefinitionMode() {
        return alternativeLine != null;
    }

    public void defineAlternativeRoute(TransportLine alternativeLine) {
        this.alternativeLine = alternativeLine;
    }

    public TransportLine finishDefiningRoute() {
        TransportLine line = alternativeLine;
        alternativeLine = null;
        return line;
    }

    private void updateAlterantiveRoute() {
        if (delegate != null && alternativeLine.getRoute().size() > 1) {
            BusView busView = new BusView(alternativeLine, Dispatching.shared.getTimestamp());
            delegate.updateAlternativeLine(busView);
        }
    }

    public void addToAlternativeRoute(StreetView view) {
        if (!isInDefinitionMode()) return;
        Street lastStreet = alternativeLine.lastStreet();
        if (lastStreet == null || (Routing.intersection(lastStreet, view.getStreet()) != null && !lastStreet.equals(view.getStreet()))) {
            alternativeLine.addStreet(view.getStreet());
        }
        updateAlterantiveRoute();
    }

    public void addToAlternativeRoute(StopView view) {
        if (!isInDefinitionMode()) return;
        Street lastStreet = alternativeLine.lastStreet();
        if (lastStreet == null || (Routing.intersection(lastStreet, view.getStop().getStreet()) != null)) {
            alternativeLine.addStop(view.getStop());
        }
        updateAlterantiveRoute();
    }
}

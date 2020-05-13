package Scene;

import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Coordinate;
import model.Street;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ViewModel {
    private List<Street> streets;

    public ViewModel() {
        JSONParser parser = new JSONParser();
        try {
            String resourcePath = getClass().getResource("/PublicTransport.json").getPath();
            Object obj = parser.parse(new FileReader(resourcePath));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonStreets = (JSONArray) jsonObject.get("streets");

            Stream<JSONObject> jsonStreetsStream = jsonStreets.stream();
            this.streets = jsonStreetsStream
                    .map(jsonStreet -> {
                        JSONArray jsonCoordinates = (JSONArray) jsonStreet.get("coordinates");
                        Stream<JSONObject> coordinateStream = jsonCoordinates.stream();
                        List<Coordinate> coordinates = coordinateStream
                                .map(jsonCoordinate -> {
                                    Long x = (Long) jsonCoordinate.get("x");
                                    Long y = (Long) jsonCoordinate.get("y");
                                    return new Coordinate(x.intValue(), y.intValue());
                                }).collect(Collectors.toList());
                        String streetName = (String) jsonStreet.get("name");
                        return new Street(streetName, coordinates);
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Street> getStreets() {
        return streets;
    }
}

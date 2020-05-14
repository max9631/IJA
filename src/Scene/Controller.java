package Scene;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import model.Stop;
import model.Street;
import model.TransportLine;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class Controller implements StreetViewDelegate {
    @FXML private Group content;
    @FXML private Slider timeMultiplierSlider;
    @FXML private Text timeMultiplaerText;
    @FXML private Text timeText;
    @FXML private Slider traficJamSlider;
    @FXML private AnchorPane jamCoeficientView;
    @FXML private Text jamCoeficientText;

    private ViewModel model;
    private Dispatching dispatching;

    void viewDidLoad(ViewModel model) {
        AbstractMap.SimpleImmutableEntry<Integer, Integer> time = getDefaultTime(timeText.getText());
        dispatching = new Dispatching(time.getKey(), time.getValue(), timeText, timeMultiplierSlider);
        model.getStreets().forEach(this::add);
        model.getStops().forEach(this::add);
        model.getTransportLines().forEach(this::add);
        timeMultiplierSlider.valueProperty().addListener(this::didDragTimeMultiplyer);
        timeMultiplaerText.setText(((int) timeMultiplierSlider.getValue())+"");
        this.model = model;
    }

    private AbstractMap.SimpleImmutableEntry<Integer, Integer> getDefaultTime(String text) {
        int hours, minutes;
        String[] newText = text.split(":");

        hours = Integer.parseInt(newText[0]);
        minutes = Integer.parseInt(newText[1]);

        return new AbstractMap.SimpleImmutableEntry<>(hours, minutes);
    }

    void add(Street street) {
        StreetView view = new StreetView(street);
        view.delegate = this;
        double x = view.lines.get(0).getStartX();
        double y = view.lines.get(0).getStartY() - 20;
        Text text = new Text(x, y, view.getStreet().getId());
        text.setX(x - (text.getLayoutBounds().getWidth()/2));
        this.content.getChildren().add(text);
        for (Line line: view.lines) {
            this.content.getChildren().add(line);
        }
    }

    void add(Stop stop) {
        StopView view = new StopView(stop);
        double x = stop.getCoordinate().getX();
        double y = stop.getCoordinate().getY() - 8;
        Text text = new Text(x, y, stop.getId());
        text.setX(x - (text.getLayoutBounds().getWidth()/2));
        this.content.getChildren().add(text);
        this.content.getChildren().add(view);
    }

    void add(TransportLine line){
        BusView view = new BusView(line);
        this.content.getChildren().addAll(view.getBus());
    }

    @FXML public void didZoom(ScrollEvent event) {
        event.consume();
        double zoom = event.getDeltaY() > 0 ? 1.1 : 0.9;
        content.setScaleX(zoom*content.getScaleX());
        content.setScaleY(zoom*content.getScaleY());
        content.layout();
    }

    @FXML public void didDeselectStreet(Event event) {
    }

    @Override
    public void didSelect(StreetView street) {
        System.out.println("Selected: "+street.getStreet().getId() );
    }

    public void didDragTimeMultiplyer(ObservableValue observable, Number oldValue, Number newValue) {
        System.out.println("current time multiplier: " + newValue);
        timeMultiplaerText.setText(newValue.intValue()+"");
    }
}

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

interface ControllerDelegate {
    void resetScene();
}

public class Controller implements StreetViewDelegate, BusViewDelegate, DispatchingDelegate {
    @FXML private Group content;
    @FXML private Slider timeMultiplierSlider;
    @FXML private Text timeMultiplaerText;
    @FXML private Text timeText;
    @FXML private Slider traficJamSlider;
    @FXML private AnchorPane jamCoeficientView;
    @FXML private Text jamCoeficientText;
    @FXML private Text itinerary;

    private ViewModel model;
    private Dispatching dispatching;

    private StreetView selectedStreetView;

    private BusView selectedBusView;

    public ControllerDelegate delegate;

    void viewDidLoad(ViewModel model) {
        AbstractMap.SimpleImmutableEntry<Integer, Integer> time = getDefaultTime(timeText.getText());
        dispatching = new Dispatching(model.getTransportLines(), this);
        model.getStreets().forEach(this::add);
        model.getStops().forEach(this::add);
        traficJamSlider.valueProperty().addListener(this::didDragJamCoeficient);
        timeMultiplierSlider.valueProperty().addListener(this::didDragTimeMultiplier);
        timeMultiplaerText.setText(((int) timeMultiplierSlider.getValue())+"");
        jamCoeficientView.setOpacity(0);
        itinerary.setText("");
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
        content.getChildren().add(text);
        for (Line line: view.lines) {
            content.getChildren().add(line);
        }
    }

    void add(Stop stop) {
        StopView view = new StopView(stop);
        double x = stop.getCoordinate().getX();
        double y = stop.getCoordinate().getY() - 8;
        Text text = new Text(x, y, stop.getId());
        text.setX(x - (text.getLayoutBounds().getWidth()/2));
        content.getChildren().add(text);
        content.getChildren().add(view);
    }


    @FXML public void didZoom(ScrollEvent event) {
        event.consume();
        double zoom = event.getDeltaY() > 0 ? 1.1 : 0.9;
        content.setScaleX(zoom*content.getScaleX());
        content.setScaleY(zoom*content.getScaleY());
        content.layout();
    }

    @FXML public void didDeselectStreet(Event event) {
        event.consume();
        deselectStreet();
        deselectBus();
    }

    @FXML public void resetScene() {
        if (delegate != null) {
            delegate.resetScene();
            dispatching.cancelTimer();
        }
    }

    private void deselectStreet() {
        if (selectedStreetView != null) {
            selectedStreetView.lines.forEach(line -> line.setStroke(Color.LIGHTGRAY));
        }
        dispatching.getBusViews().stream().forEach(busView -> busView.getNodes().stream().forEach(node -> node.toFront()));
        jamCoeficientView.setOpacity(0);
        selectedStreetView = null;
    }

    @Override
    public void didSelect(StreetView street) {
        deselectStreet();
        selectedStreetView = street;
        traficJamSlider.setValue(street.getStreet().getFrictionCoefficient());
        setJamCoefficient(street, street.getStreet().getFrictionCoefficient());
        jamCoeficientView.setOpacity(1);
    }

    @Override
    public void userSelected(BusView view) {
        deselectBus();
        view.setStroke(Color.BLUE);
        selectedBusView = view;
        itinerary.setText(view.getStopItinerary());
        view.getRouteLines().forEach(line -> content.getChildren().add(line));
    }

    private void deselectBus() {
        if (selectedBusView != null) {
            selectedBusView.setStroke(Color.RED);
            selectedBusView.getRouteLines().forEach(line -> content.getChildren().remove(line));
        }
        itinerary.setText("");
        selectedBusView = null;

    }

    private void setJamCoefficient(StreetView street, double jamCoefficient) {
        if (street == null) return;
        street.getStreet().setFrictionCoefficient(jamCoefficient);
        double percentage = jamCoefficient / traficJamSlider.getMax();
        double redDouble = 255 * percentage;
        int red = (int) redDouble;
        double greenDouble = 255 * (1 - percentage);
        int green = (int) greenDouble;
        street.lines.forEach(line -> line.setStroke(Color.rgb(red, green, 0)));
    }

    private void updateItinerary() {
        itinerary.setText(selectedBusView == null ? "" : selectedBusView.getStopItinerary());
    }

    public void didDragTimeMultiplier(ObservableValue observable, Number oldValue, Number newValue) {
        timeMultiplaerText.setText(newValue.intValue()+"");
    }

    public void didDragJamCoeficient(ObservableValue observable, Number oldValue, Number newValue) {
        jamCoeficientText.setText(newValue.doubleValue()+"");
        setJamCoefficient(selectedStreetView, newValue.doubleValue());
        updateItinerary();
    }

    @Override
    public int getTimeMultiplier() {
        return (int) timeMultiplierSlider.getValue();
    }

    @Override
    public void updateTime(int timestamp) {
        String secondsPrefix = timestamp%60 > 9 ? "" : "0";
        timeText.setText(timestamp/60 + ":" + secondsPrefix + timestamp%60);
    }

    @Override
    public void showBusView(BusView view) {
        view.delegate = this;
        content.getChildren().addAll(view.getNodes());
    }

    @Override
    public void remove(BusView view) {
        if (selectedBusView != null && selectedBusView.equals(view)) {
            deselectBus();
        }
        content.getChildren().removeAll(view.getNodes());
    }
}

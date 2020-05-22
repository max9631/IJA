package Scene;

import common.Formatter;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import model.Coordinate;
import model.Stop;
import model.Street;
import model.TransportLine;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

interface ControllerDelegate {
    void resetScene();
}

public class Controller implements StreetViewDelegate, BusViewDelegate, DispatchingDelegate, StopViewDelegate, ViewModelDelegate {
    @FXML private Group content;
    @FXML private Slider timeMultiplierSlider;
    @FXML private Text timeMultiplaerText;
    @FXML private Text timeText;
    @FXML private Slider traficJamSlider;
    @FXML private AnchorPane jamCoeficientView;
    @FXML private Text jamCoeficientText;
    @FXML private Text itinerary;
    @FXML private Button closeStreetLineButton;
    @FXML private Button defineNewLineButton;

    private ViewModel model;
    private Dispatching dispatching;

    private StreetView selectedStreetView;
    private List<StreetViewLine> selectedStreetViewLines = new ArrayList<>();

    private BusView selectedBusView;
    private BusView alternativeLineBusView;

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
        closeStreetLineButton.setOpacity(0);
        itinerary.setText("");
        defineNewLineButton.setOpacity(0);
        this.model = model;
        this.model.delegate = this;
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
        double x = view.getLines().get(0).getStartX();
        double y = view.getLines().get(0).getStartY() - 20;
        Text text = new Text(x, y, view.getStreet().getId());
        text.setX(x - (text.getLayoutBounds().getWidth()/2));
        content.getChildren().add(text);
        for (Line line: view.getLines()) {
            content.getChildren().add(line);
        }
    }

    void add(Stop stop) {
        StopView view = new StopView(stop);
        view.delegate = this;
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

    @FXML public void closeStreetLines() {
        boolean close = !closeStreetLineButton.getText().equals(model.openStreetString);
        selectedStreetViewLines.stream().forEach(line -> {
            line.setStroke(close ? Color.RED : Color.ORANGE);
            line.setIsClosed(close);
        });
        selectedStreetViewLines.stream().forEach(line -> line.setIsClosed(close));
        closeStreetLineButton.setText(close ? model.openStreetString : model.closeStreetString);
    }

    @FXML public void defineAlternativeLine() {
        if (selectedBusView != null) {
            if (selectedBusView.getTransportLine().isAlternativeTransport()) {
                if (selectedBusView.getTransportLine().isActive) {
                    selectedBusView.getTransportLine().isActive = false;
                    removeAlternativeLine();
                    defineNewLineButton.setOpacity(0);
                }
            } else {
                TransportLine alternativeLine = selectedBusView.getTransportLine().getNewAlternativeLine();
                model.defineAlternativeRoute(alternativeLine);
                deselectBus();
                defineNewLineButton.setText(model.completeNewRouteString);
            }
        } else if (model.isInDefinitionMode()) {
            TransportLine line = model.finishDefiningRoute();
            Dispatching.shared.addLine(line);
            defineNewLineButton.setOpacity(0);
            removeAlternativeLine();
        }
    }

    private void deselectStreet() {
        if (selectedStreetView != null) {
            selectedStreetView.getLines().forEach(line -> {
                if (!line.isClosed()) {
                    line.setStroke(Color.LIGHTGRAY);
                }
            });
        }
        selectedStreetViewLines = new ArrayList<>();
        dispatching.getBusViews().stream()
                .forEach(busView -> busView.getNodes().stream().forEach(node -> node.toFront()));
        jamCoeficientView.setOpacity(0);
        selectedStreetView = null;
        closeStreetLineButton.setOpacity(0);
    }

    @Override
    public List<Coordinate> getClosedCoordinates() {
        List<Coordinate> closed = new ArrayList<>();
        model.getStreets().stream().forEach(street -> closed.addAll(street.getClosedCoordinates()));
        return closed;
    }

    @Override
    public void didSelect(StreetView street, StreetViewLine line) {
        if (model.isInDefinitionMode()) {
            model.addToAlternativeRoute(street);
            return;
        }
        if (!street.equals(selectedStreetView)) {
            deselectStreet();
        }
        if (selectedStreetView == null) {
            selectedStreetView = street;
            traficJamSlider.setValue(street.getStreet().getFrictionCoefficient());
            setJamCoefficient(street, street.getStreet().getFrictionCoefficient());
            jamCoeficientView.setOpacity(1);
        } else {
            line.setStroke(line.isClosed() ? Color.RED :Color.ORANGE);
            selectedStreetViewLines.add(line);
        }
        if (selectedStreetViewLines.size() > 0) {
            closeStreetLineButton.setOpacity(1);
            boolean everyLineIsOpen = true;
            for (StreetViewLine selectedLine: selectedStreetViewLines) {
                everyLineIsOpen = everyLineIsOpen && !selectedLine.isClosed();
            }
            closeStreetLineButton.setText(everyLineIsOpen ? model.closeStreetString : model.openStreetString);
        }
    }

    @Override
    public void userSelected(BusView view) {
        if (model.isInDefinitionMode()) return;
        deselectBus();
        view.setStroke(Color.BLUE);
        selectedBusView = view;
        itinerary.setText(view.getStopItinerary());
        view.getRouteLines().forEach(line -> content.getChildren().add(line));
        defineNewLineButton.setOpacity(1);
        if (view.getTransportLine().isAlternativeTransport()) {
            if (view.getTransportLine().isActive) {
                defineNewLineButton.setText(model.removeRouteString);
            }
        } else {
            defineNewLineButton.setText(model.defineNewRouteString);
        }
    }

    private void deselectBus() {
        if (selectedBusView != null) {
            selectedBusView.setStroke(Color.RED);
            selectedBusView.getRouteLines().forEach(line -> content.getChildren().remove(line));
        }
        itinerary.setText("");
        selectedBusView = null;
        if (!model.isInDefinitionMode()) {
            defineNewLineButton.setOpacity(0);
        }
    }

    private void setJamCoefficient(StreetView street, double jamCoefficient) {
        if (street == null) return;
        street.getStreet().setFrictionCoefficient(jamCoefficient);
        double percentage = jamCoefficient / traficJamSlider.getMax();
        double redDouble = 255 * percentage;
        int red = (int) redDouble;
        double greenDouble = 255 * (1 - percentage);
        int green = (int) greenDouble;
        street.getLines().stream()
                .filter(line -> !line.isClosed())
                .filter(line -> !selectedStreetViewLines.contains(line))
                .forEach(line -> line.setStroke(Color.rgb(red, green, 0)));
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
    public void update(int timestamp) {
        timeText.setText(Formatter.formatTime(timestamp));
        updateItinerary();
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

    // MARK: StopViewDelegate

    @Override
    public void didSelectStop(StopView view) {
        if (!model.isInDefinitionMode()) return;
        model.addToAlternativeRoute(view);
    }

    // MARK: ViewModelDelegate

    private void removeAlternativeLine() {
        if (alternativeLineBusView != null) {
            content.getChildren().removeAll(alternativeLineBusView.getRouteLines());
        }
    }

    public void updateAlternativeLine(BusView view) {
        removeAlternativeLine();
        content.getChildren().addAll(view.getRouteLines());
        alternativeLineBusView = view;
    };
}

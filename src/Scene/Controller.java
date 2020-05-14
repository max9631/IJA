package Scene;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import model.Stop;
import model.Street;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Controller implements StreetViewDelegate {
    @FXML
    private Group content;
    @FXML
    private Slider timeMultiplierSlider;
    @FXML
    private Text timeMultiplaerText;
    @FXML
    private Text timeText;

    private ViewModel model;

    void viewDidLoad(ViewModel model) {
        model.getStreets().forEach(this::add);
        model.getStops().forEach(this::add);
        timeMultiplierSlider.valueProperty().addListener(this::didDragTimeMultiplyer);
        timeMultiplaerText.setText(timeMultiplierSlider.getValue()+"");
        this.model = model;
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

    @Override
    public void didSelect(StreetView street) {
        System.out.println("Selected: "+street.getStreet().getId() );
    }

    public void didDragTimeMultiplyer(ObservableValue observable, Number oldValue, Number newValue) {
        System.out.println("current time multiplier: " + newValue);
        timeMultiplaerText.setText(newValue.toString());
    }
}

package Scene;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Slider;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
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

    void loadData(Loader loader) {
        loader.getStreets().forEach(street -> this.add(street));
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

    @Override
    public void didSelect(StreetView street) {

        System.out.println("Selected: "+street.getStreet().getId() );
    }
}

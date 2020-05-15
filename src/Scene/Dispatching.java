package Scene;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import model.Coordinate;
import model.TransportLine;

import java.util.*;


public class Dispatching extends TimerTask {

    private Text timeText;
    private Slider timeMultiplierSlider;
    private Controller controller;

    private int hours, minutes;
    private List<AbstractMap.SimpleImmutableEntry<BusView, Double>> busLines;
    private Timer timer;

    private boolean clockInit = true;

    public Dispatching(int hours, int minutes, Text timeText, Slider timeMultiplierSlider, Controller controller) {
        this.hours = hours;
        this.minutes = minutes;
        this.busLines = new ArrayList<>();
        this.timeText = timeText;
        this.timeMultiplierSlider = timeMultiplierSlider;
        this.controller = controller;

        this.timer = new Timer(true);
        long delay = 1000;
        timer.schedule(this, 0, delay);
    }

    public void addBus(BusView busLine){
        double interval = busLine.getLine().getInterval();
        this.busLines.add(new AbstractMap.SimpleImmutableEntry<>(busLine, interval));
    }
    
    public void cancelTimer(){
        timer.cancel();
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
        this.minutes += 1*this.timeMultiplierSlider.getValue();

        if(this.minutes >= 60){
            this.minutes = this.minutes % 60;
            this.hours++;
        }
        if(this.hours == 24){
            this.hours = 0;
        }

        String hrs, mins;
        if(this.hours < 10){
            hrs = "0" + this.hours;
        }
        else{
            hrs = this.hours + "";
        }
        if(this.minutes < 10){
            mins = "0" + this.minutes;
        }
        else{
            mins = this.minutes + "";
        }

        for (AbstractMap.SimpleImmutableEntry<BusView, Double> line: this.busLines) {
            Coordinate position = line.getKey().getPosition();
            Coordinate textPosition = line.getKey().getTextPosition();

            int multiplier = 1;
            multiplier *= line.getKey().getVelocity()*this.timeMultiplierSlider.getValue();

            line.getKey().getBusIcon().setCenterX(position.getX()+multiplier);
            line.getKey().getBusIcon().setCenterY(position.getY()+multiplier);
            line.getKey().getBusText().setX(textPosition.getX()+multiplier);
            line.getKey().getBusText().setY(textPosition.getY()+multiplier);

            line.getKey().setPosition(new Coordinate(position.getX()+multiplier, position.getY()+multiplier), new Coordinate(textPosition.getX()+multiplier, textPosition.getY()+multiplier));

            System.out.print("Line: " + " " + line.getKey().getBusText().getText() + " X: ");
            System.out.println(position.getX() + " Y: " + position.getY());
        }

        if(!clockInit) {
            for (AbstractMap.SimpleImmutableEntry<BusView, Double> line : this.busLines) {
                double intervalValue = line.getValue();
                if (minutes % (int) intervalValue == 0) {
                    System.out.println("Linka: " + line.getKey().getBusText().getText() + " " + " Out!");

                    this.controller.add(line.getKey().getLine());

                }
            }
        }

        timeText.setText(hrs+":"+mins);
        clockInit = false;
        });
    }
}
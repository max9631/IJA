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
    private ArrayList<BusView> busLines;
    private Timer timer;

    private int busTime = 0;
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
            this.busLines.add(busLine);
            busLine.getLine().calculateRoute();
    }
    
    public void cancelTimer(){
        timer.cancel();
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            this.busTime += 1*this.timeMultiplierSlider.getValue();
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

            for (BusView line: this.busLines) {
                Coordinate position = line.getPosition();
                Coordinate textPosition = line.getTextPosition();

                int multiplier = 1;
                multiplier *= line.getVelocity()*this.timeMultiplierSlider.getValue();

                line.getBusIcon().setCenterX(position.getX()+multiplier);
                line.getBusIcon().setCenterY(position.getY()+multiplier);
                line.getBusText().setX(textPosition.getX()+multiplier);
                line.getBusText().setY(textPosition.getY()+multiplier);

                line.setPosition(new Coordinate(position.getX()+multiplier, position.getY()+multiplier), new Coordinate(textPosition.getX()+multiplier, textPosition.getY()+multiplier));

                System.out.print("Line: " + " " + line.getBusText().getText() + " X: ");
                System.out.println(position.getX() + " Y: " + position.getY());
            }

            if(!clockInit) {
                ArrayList<BusView> busInfo = new ArrayList<>(this.busLines);
                for (BusView line : busInfo) {
                    double intervalValue = line.getLine().getInterval();
                    if (busTime % (int) intervalValue == 0) {
                        System.out.println("Linka: " + line.getBusText().getText() + " " + " Out!");

                        this.controller.add(line.getLine());

                    }
                }
            }

            timeText.setText(hrs+":"+mins);
            clockInit = false;
        });
    }
}
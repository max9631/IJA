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
import java.util.concurrent.locks.ReentrantLock;


public class Dispatching extends TimerTask {

    private Text timeText;
    private Slider timeMultiplierSlider;
    private Controller controller;
    private int lastBusId = 0;

    private int hours, minutes;
    private ArrayList<BusView> busLines;
    private Timer timer;
    private ReentrantLock lock;

    private List<AbstractMap.SimpleImmutableEntry<Integer, Double>> busesDeparted;

    private int busTime = 0;
    private boolean clockInit = true;

    public Dispatching(int hours, int minutes, Text timeText, Slider timeMultiplierSlider, Controller controller) {
        this.hours = hours;
        this.minutes = minutes;
        this.busLines = new ArrayList<>();
        this.busesDeparted = new ArrayList<>();
        this.timeText = timeText;
        this.timeMultiplierSlider = timeMultiplierSlider;
        this.controller = controller;
        lock = new ReentrantLock();

        this.timer = new Timer(true);
        long delay = 1000;
        timer.schedule(this, 0, delay);
    }

    public ArrayList<BusView> getBusLines() {
        return busLines;
    }

    public boolean addBus(BusView busLine, int time, double interval){
        for (AbstractMap.SimpleImmutableEntry<Integer, Double> record : this.busesDeparted){
            if (record.getKey() == time && record.getValue() == interval) {
                return false;
            }
        }
        busesDeparted.add(new AbstractMap.SimpleImmutableEntry<>(time, interval));
        this.busLines.add(busLine);
        busLine.getLine().calculateRoute();
        return true;
    }

    public void cancelTimer(){
        timer.cancel();
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            lock.lock();
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
                busUpdate(line);
            }

            if(!clockInit) {
                ArrayList<BusView> busInfo = new ArrayList<>(this.busLines);
                for (BusView line : busInfo) {
                    double intervalValue = line.getLine().getInterval();
                    if (busTime % (int) intervalValue == 0 || timeMultiplierSlider.getValue() % (int) intervalValue == 0) {
                        //System.out.println("Linka: " + line.getBusText().getText() + " " + " Out!");
                        int timeElapsed = (busTime - line.getLine().getLastGen()) / (int) intervalValue;
                        System.out.println("Curren time: " + busTime + " Last time : " +line.getLine().getLastGen()+ " On line: " + line.getLine().getId());
                        if(timeElapsed > 1){
                            System.out.println("Here! " + timeElapsed);
                            for(int i = 0; i < timeElapsed; i++){
                                this.controller.add(line.getLine(), (i+1)*(int)intervalValue, busTime);
                            }
                        }
                        else{
                            this.controller.add(line.getLine(), 1, busTime);
                        }
                        line.getLine().setLastGen(busTime);
                    }
                }
            }

            timeText.setText(hrs+":"+mins);
            clockInit = false;
            lock.unlock();
        });
    }

    public void setLastBusId(int lastBusId) {
        this.lastBusId = lastBusId;
    }

    public int getLastBusId() {
        return lastBusId;
    }

    private void busUpdate(BusView line){
        Coordinate position = line.getPosition();
        Coordinate textPosition = line.getTextPosition();

        int multiplier = 1;
        multiplier *= line.getVelocity()*this.timeMultiplierSlider.getValue();

        List<Coordinate> lastStreetCoords = line.getLine().lastStreet().getCoordinates();
        Coordinate endCoordinates = lastStreetCoords.get(lastStreetCoords.size()-1);

        if(endCoordinates.getX() <= position.getX() && endCoordinates.getY() <= position.getY()){
            ArrayList<BusView> newLines = new ArrayList<>();
            for(BusView lines : this.busLines){
                if(lines == line){
                    continue;
                }
                newLines.add(lines);
            }
            busLines = newLines;
            line.end();
            return;
        }

        line.getBusIcon().setCenterX(position.getX()+multiplier);
        line.getBusIcon().setCenterY(position.getY()+multiplier);
        line.getBusText().setX(textPosition.getX()+multiplier);
        line.getBusText().setY(textPosition.getY()+multiplier);

        line.setPosition(new Coordinate(position.getX()+multiplier, position.getY()+multiplier), new Coordinate(textPosition.getX()+multiplier, textPosition.getY()+multiplier));

        //System.out.print("Line: " + " " + line.getBusText().getText() + " X: ");
        //System.out.println(position.getX() + " Y: " + position.getY());
    }
}

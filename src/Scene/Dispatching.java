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

    private int hours, minutes;
    private ArrayList<BusView> busLines;
    private Timer timer;
    private ReentrantLock lock;

    private List<AbstractMap.SimpleImmutableEntry<Integer,AbstractMap.SimpleImmutableEntry<Integer, Double>>> busesDeparted;

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
        timer.schedule(this, delay, delay);
    }

    public boolean addBus(BusView busLine, int time, double interval, int lineId){
        for (AbstractMap.SimpleImmutableEntry<Integer, AbstractMap.SimpleImmutableEntry<Integer, Double>> record : this.busesDeparted){
            if ((record.getKey() == time && record.getValue().getValue() == interval) && lineId == record.getKey()) {
                return false;
            }
        }
        busesDeparted.add(new AbstractMap.SimpleImmutableEntry<>(lineId ,new AbstractMap.SimpleImmutableEntry<>(time, interval)));
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

    private void busUpdate(BusView line){
        if(line.getCoordIndex() == 0){
            line.setCurrentPoint();
        }
        Coordinate currPosition = line.getPosition();
        Coordinate followPosition = line.getCurrentPoint();

        int multiplier = 1;
        multiplier *= this.timeMultiplierSlider.getValue();

        if(followPosition == null){
            ArrayList<BusView> newLines = new ArrayList<>();
            for(BusView lines : this.busLines){
                if(lines == line){
                    continue;
                }
                newLines.add(lines);
            }
            busLines = newLines;
            controller.removeBus(line);
            line.end();
            return;
        }

        Coordinate newPosition = new Coordinate(0, 0);

        if(Integer.parseInt(line.getLine().getId()) != 1) {
            System.out.println(line.getLine().getId() + " : " + line.getCoordIndex());
            ArrayList<Coordinate> coords = line.getRouteCoords();
//            System.out.println("X: " + coords.get(minutes-1).getX() + " AND " + coords.get(minutes).getX());
//            System.out.println("Y: " + coords.get(minutes-1).getY() + " AND " + coords.get(minutes).getY());
            System.out.println("curr X: " + (currPosition.getX()-line.getVelocity()) + ", next pos X: " + followPosition.getX());
            System.out.println("curr Y: " + currPosition.getY() + ", next pos Y: " + followPosition.getY());
        }

        if(currPosition.getX() != followPosition.getX()){
            //zmena na x
            if(currPosition.getX() > followPosition.getX()){
                //zprava doleva
                //rychlosti autobusu, zpozdeni
                int X = currPosition.getX()-line.getVelocity();
                int Y = currPosition.getY();
                //v pripade zatacky vypocitat "prejezd"
                if(X >= currPosition.getX()){
                    line.setCurrentPoint();
                    if(line.getCurrentPoint() != null) {
                        X = line.getCurrentPoint().getX();
                    }
                }

                newPosition.setX(X);
                newPosition.setY(Y);
            }
            else{
                //zleva doprava
                int X = currPosition.getX()+line.getVelocity();
                int Y = currPosition.getY();
                //v pripade zatacky vypocitat "prejezd"
                if(X <= currPosition.getX()){
                    line.setCurrentPoint();
                    if(line.getCurrentPoint() != null) {
                        X = line.getCurrentPoint().getX();
                    }
                }

                newPosition.setX(X);
                newPosition.setY(Y);
            }
        }
        else{
            if(Integer.parseInt(line.getLine().getId()) != 1) System.out.println("Here!");
            //zmena na y
            if(currPosition.getY() > followPosition.getY()){
                //zdola nahoru
                int X = currPosition.getX();
                int Y = currPosition.getY()-line.getVelocity();
                //v pripade zatacky vypocitat "prejezd"
                if(Y <= currPosition.getY()){
                    line.setCurrentPoint();
                    if(line.getCurrentPoint() != null) {
                        //Y = line.getCurrentPoint().getY();
                    }
                }

                newPosition.setX(X);
                newPosition.setY(Y);
            }
            else{
                //zhora dolu
                int X = currPosition.getX();
                int Y = currPosition.getY()+line.getVelocity();
                //v pripade zatacky vypocitat "prejezd"
                if(Y >= currPosition.getY()){
                    line.setCurrentPoint();
                    if(line.getCurrentPoint() != null) {
                        //Y = line.getCurrentPoint().getY();
                    }
                }

                newPosition.setX(X);
                newPosition.setY(Y);
            }
        }
        if(Integer.parseInt(line.getLine().getId()) != 1)
            System.out.println("NEW ONES: " + newPosition.getX() + " " + newPosition.getY());
        line.getBusIcon().setCenterX((newPosition.getX())*multiplier); line.getBusIcon().setCenterY((newPosition.getY())*multiplier);
        line.getBusText().setX((newPosition.getX()-3)*multiplier); line.getBusText().setY((newPosition.getY()+5)*multiplier);

        line.setPosition(new Coordinate(newPosition.getX()*multiplier, newPosition.getY()*multiplier));

        //System.out.print("Line: " + " " + line.getBusText().getText() + " X: ");
        //System.out.println(position.getX() + " Y: " + position.getY());
    }
}

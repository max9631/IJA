package Scene;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import model.TransportLine;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Dispatching extends TimerTask {

    private Text timeText;
    private Slider timeMultiplierSlider;


    private int hours, minutes;
    private ArrayList<TransportLine> bus;

    public Dispatching(int hours, int minutes, Text timeText, Slider timeMultiplierSlider) {
        this.hours = hours;
        this.minutes = minutes;
        this.bus = new ArrayList<>();
        this.timeText = timeText;
        this.timeMultiplierSlider = timeMultiplierSlider;

        Timer timer = new Timer();
        long delay = 1000;
        timer.schedule(this, delay, delay);
    }

    public void addBus(TransportLine line){
        bus.add(line);
    }




    @Override
    public void run() {
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

        timeText.setText(hrs+":"+mins);
    }
}
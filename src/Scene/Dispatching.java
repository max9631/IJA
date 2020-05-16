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
import java.util.stream.Collectors;

interface DispatchingDelegate {
    int getTimeMultiplier();
    void update(int timestamp);
    void showBusView(BusView view);
    void remove(BusView view);
}


public class Dispatching extends TimerTask {

    public static Dispatching shared;

    private DispatchingDelegate delegate;

    private Timer timer;
    private ReentrantLock lock;

    private List<TransportLine> lines;

    private List<BusView> busViews = new ArrayList<>();

    private int timestamp = 480;

    public Dispatching(List<TransportLine> lines, DispatchingDelegate delegate) {
        Dispatching.shared = this;
        this.delegate = delegate;
        this.lines = lines;
        lock = new ReentrantLock();
        timer = new Timer(true);
        timer.schedule(this, (long) 1000, (long) 1000);
    }

    public List<BusView> getBusViews() {
        return busViews;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void cancelTimer(){
        timer.cancel();
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            lock.lock();
            int delta = 1 * delegate.getTimeMultiplier();
            timestamp += delta;
            delegate.update(timestamp);
            generateBuses(delta);
            recalculateBusPositions(delta);
            lock.unlock();
        });
    }

    void generateBuses(int delta) {
        for (TransportLine line: lines) {
            int interval = line.getInterval();
            int offset = (timestamp-delta)%interval;
            int numberOfBuses = (delta + offset) / interval;
            int startingTimestamp = timestamp - delta - offset;
            for (int i = 0; i < numberOfBuses; i++) {
                startingTimestamp += interval;
                BusView bus = new BusView(line, startingTimestamp);
                bus.moveByTime(timestamp - startingTimestamp);
                delegate.showBusView(bus);
                busViews.add(bus);
            }
        }
    }

    void recalculateBusPositions(int delta) {
        busViews = busViews
                .stream()
                .filter(bus -> {
                    if (bus.reachedEnd()) {
                        delegate.remove(bus);
                    }
                    return !bus.reachedEnd();
                })
                .collect(Collectors.toList());
        busViews.stream()
                .forEach(bus -> bus.moveByTime(delta));
    }
}

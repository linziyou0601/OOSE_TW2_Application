package devices;

import database.DBMgr;
import database.MySQLDBMgrImpl;
import main.APIService;
import main.MainApplication;
import observer.and.adapter.Observable;
import observer.and.adapter.Observer;
import org.reactfx.util.FxTimer;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SmartSpeaker implements IoTDevice, Observable {
    private int id;
    private String name;
    private String state;
    private List<Observer> observerList = new ArrayList<>();

    public SmartSpeaker(int id, String name, String state) {
        this.id = id;
        this.name = name;
        this.state = state;
        FxTimer.runPeriodically(Duration.ofMillis(1000), () -> loadState());
    }

    public int getId() {
        return id;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getState(){
        return state;
    }

    @Override
    public void uploadState() {
        APIService.shareIoTState(this);
        notifyObservers();
    }

    @Override
    public void loadState() {
        this.state = APIService.loadIoTState(this.id);
        notifyObservers();
    }

    @Override
    public void switchState() {
        if(state.equals("ON")) state = "OFF";
        else state = "ON";
        uploadState();
    }

    @Override
    public void addObserve(Observer observer) {
        observerList.add(observer);
        observer.update(this);
    }

    @Override
    public void removeObserve(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(){
        Iterator<Observer> observerItr = observerList.iterator();
        while(observerItr.hasNext()){
            observerItr.next().update(this);
        }
    }
}

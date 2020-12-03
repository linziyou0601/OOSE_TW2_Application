package devices;


import observer.and.adapter.Observable;
import observer.and.adapter.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SmartLock implements IoTDevice, Observable {
    private String id;
    private String name;
    private boolean state;
    private List<Observer> observerList = new ArrayList<>();

    public SmartLock(String id, String name, boolean state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public String getId() {
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

    public boolean getState(){
        return state;
    }

    @Override
    public void uploadState() {
        notifyObservers();
    }

    @Override
    public void loadState() {

    }

    @Override
    public void switchState() {
        state = !state;
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

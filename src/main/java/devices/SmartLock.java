package devices;


import main.APIService;
import mvvm.RxJavaObserver;
import observer.and.adapter.IObservable;
import observer.and.adapter.IObserver;
import org.reactfx.util.FxTimer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SmartLock implements IoTDevice, IObservable {
    private int id;
    private String name;
    private String state;
    private List<IObserver> observerList = new ArrayList<>();

    public SmartLock() {}
    public void initialize(int id, String name, String state) {
        this.id = id;
        this.name = name;
        this.state = state;
        FxTimer.runPeriodically(Duration.ofMillis(5000), () -> loadState());
    }

    // ==================== For IoTDevice Interface ====================
    @Override
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

    @Override
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
        APIService.loadIoTState(this.id).subscribe(new RxJavaObserver<>(){
            @Override
            public void onNext(String result) { state = result; }
            @Override
            public void onComplete(){ notifyObservers(); }
            @Override
            public void onError(Throwable e){}
        });
    }

    @Override
    public void switchState() {
        if(state.equals("ON")) state = "OFF";
        else state = "ON";
        uploadState();
    }

    // ==================== For IObservable Interface ====================
    @Override
    public void addObserve(IObserver observer) {
        observerList.add(observer);
        observer.update(this);
    }

    @Override
    public void removeObserve(IObserver observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(){
        Iterator<IObserver> observerItr = observerList.iterator();
        while(observerItr.hasNext()){
            observerItr.next().update(this);
        }
    }
}

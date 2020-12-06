package observer.and.adapter;

public interface IObservable {
    void addObserve(IObserver observer);
    void removeObserve(IObserver observer);
    void notifyObservers();
}

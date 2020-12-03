package observer.and.adapter;

public interface Observable {
    void addObserve(Observer observer);
    void removeObserve(Observer observer);
    void notifyObservers();
}

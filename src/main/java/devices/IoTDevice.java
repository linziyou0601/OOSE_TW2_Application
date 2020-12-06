package devices;

public interface IoTDevice {
    int getId();
    String getType();
    String getName();
    String getState();
    void uploadState();
    void loadState();
    void switchState();
}

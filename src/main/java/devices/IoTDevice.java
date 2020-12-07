package devices;

public interface IoTDevice {
    void initialize(int id, String name, String state);
    int getId();
    String getType();
    String getName();
    String getState();
    void uploadState();
    void loadState();
    void switchState();
}

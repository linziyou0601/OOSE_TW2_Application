package devices;

public interface IoTDevice {
    String getId();
    String getType();
    String getName();
    boolean getState();
    void uploadState();
    void loadState();
    void switchState();
}

package model;

import devices.IoTDevice;
import devices.SmartComputer;
import devices.SmartLock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Classroom {
    private String id;
    private String type;
    private List<IoTDevice> devices = new ArrayList<>();

    public Classroom(String id, String type) {
        this.id = id;
        this.type = type;
        devices.add(new SmartLock(id+"_IOT001", "Lock Door 1", true));
        devices.add(new SmartLock(id+"_IOT002", "Lock Door 2", true));
        devices.add(new SmartComputer(id+"_IOT003", "Computer 1", false));
        devices.add(new SmartComputer(id+"_IOT004", "Computer 2", false));
        devices.add(new SmartComputer(id+"_IOT005", "Computer 3", false));
        devices.add(new SmartComputer(id+"_IOT006", "Computer 4", false));
        devices.add(new SmartComputer(id+"_IOT007", "Computer 5", false));
        devices.add(new SmartComputer(id+"_IOT008", "Computer 6", false));
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<IoTDevice> getDevices() {
        return devices;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    // 待資料庫補齊需要改
    public boolean hasComputer() {
        return true;
    }

    public boolean hasProjector() {
        return true;
    }

    public boolean hasBlackboard() {
        return false;
    }

    public boolean hasAirCond() {
        return true;
    }

    public boolean hasSpeaker() {
        return false;
    }

    public List<Boolean> getAvailableTimes() {
        return new ArrayList<>(Arrays.asList(true, true, true, false, true, true, false, true, false, true, true, false, true, true, false, true, true, false, false, false, true, true, true, true));
    }
}

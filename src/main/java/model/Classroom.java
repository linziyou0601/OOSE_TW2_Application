package model;

import devices.*;

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
        devices.add(new SmartLock(id+"_IOT001", "Lock Door", true));
        devices.add(new SmartLight(id+"_IOT002", "Light 1-1", false));
        devices.add(new SmartLight(id+"_IOT003", "Light 1-2", false));
        devices.add(new SmartLight(id+"_IOT004", "Light 2-1", false));
        devices.add(new SmartLight(id+"_IOT005", "Light 2-2", false));
        devices.add(new SmartProjector(id+"_IOT006", "Projector", false));
        devices.add(new SmartComputer(id+"_IOT007", "Computer", false));
        devices.add(new SmartAirConditioner(id+"_IOT008", "Air Cond", false));
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

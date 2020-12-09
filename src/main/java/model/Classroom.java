package model;

import devices.*;

import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private String id;
    private String type;
    private List<IoTDevice> devices = new ArrayList<>();

    public Classroom() {}

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

    public void setDevices(List<IoTDevice> devices) {
        this.devices = devices;
    }

    // 待資料庫補齊需要改
    public boolean hasComputer() {
        for(IoTDevice device: devices)
            if(device instanceof SmartComputer)
                return true;
        return false;
    }

    public boolean hasProjector() {
        for(IoTDevice device: devices)
            if(device instanceof SmartProjector)
                return true;
        return false;
    }

    public boolean hasBlackboard() {
        return true;
    }

    public boolean hasAirCond() {
        for(IoTDevice device: devices)
            if(device instanceof SmartAirConditioner)
                return true;
        return false;
    }

    public boolean hasSpeaker() {
        for(IoTDevice device: devices)
            if(device instanceof SmartSpeaker)
                return true;
        return false;
    }
}

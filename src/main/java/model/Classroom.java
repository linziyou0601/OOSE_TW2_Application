package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Classroom {
    private String id;
    private String type;

    public Classroom(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
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
        return new ArrayList<Boolean>(Arrays.asList(true, false, false ,false ,true, true, false, true, false ,true ,true, false, true, true, false ,true ,true, false, false, false, true ,false ,true, true));
    }
}

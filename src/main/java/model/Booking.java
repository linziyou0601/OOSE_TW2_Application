package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Booking {
    public static int count = 0;
    private int id;
    private String date;
    private int startTime;
    private int endTime;
    private String classroomId;
    private String account;
    private boolean activate = false;

    public Booking(String date, int startTime, int endTime, String classroomId, String account) {
        this.id = Booking.count++;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroomId = classroomId;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public String getAccount() {
        return account;
    }

    public boolean getActivate() {
        return activate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    // 待資料庫補齊需要改
    public boolean isPeriod() {
        LocalDate date_booking = LocalDate.parse(date);
        LocalDate date_today = LocalDate.now();
        if(date_booking.equals(date_today)){
            LocalTime time_now = LocalTime.now();
            if(time_now.getHour() >= startTime && time_now.getHour() <= (endTime+1)){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isFuture() {
        LocalDate date_booking = LocalDate.parse(date);
        LocalDate date_today = LocalDate.now();
        if(date_booking.isAfter(date_today)){
            return true;
        } else {
            LocalTime time_now = LocalTime.now();
            if(time_now.getHour() < startTime){
                return true;
            } else {
                return false;
            }
        }
    }
}

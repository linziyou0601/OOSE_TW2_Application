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
    private Classroom classroom;
    private User user;
    private boolean activate = false;

    public Booking(String date, int startTime, int endTime, Classroom classroom, User user) {
        this.id = Booking.count++;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroom = classroom;
        this.user = user;
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

    public Classroom getClassroom() {
        return classroom;
    }

    public User getUser() {
        return user;
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

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    // 中介方法（守LoD）
    public String getClassroomId() {
        return classroom.getId();
    }

    public String getUserAccount() {
        return user.getAccount();
    }

    // 待資料庫補齊需要改
    public boolean isPeriod() {
        LocalDate date_booking = LocalDate.parse(date);
        LocalDate date_today = LocalDate.now();
        if(date_booking.equals(date_today)){
            LocalTime time_now = LocalTime.now();
            if(time_now.getHour() >= startTime && time_now.getHour() <= endTime){
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

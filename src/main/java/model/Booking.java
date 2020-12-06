package model;

import main.MainApplication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Booking {
    private int id;
    private String date;
    private int startTime;
    private int endTime;
    private String classroomId;
    private Classroom classroom;
    private User user;
    private boolean activate = false;

    public Booking() {}

    public Booking(String date, int startTime, int endTime, Classroom classroom, User user) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroom = classroom;
        this.classroomId = classroom.getId();
        this.user = user;
    }

    public Integer getId() {
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
    public String getClassroomId() {
        return classroomId;
    }
    public User getUser() {
        return user;
    }
    public String getUserAccount() {
        return user.getAccount();
    }
    public boolean getActivate() {
        return activate;
    }

    public void setId(int id) {
        this.id = id;
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
    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setActivate(boolean activate) {
        this.activate = activate;
    }

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
        if(!date_booking.isBefore(date_today)){
            LocalTime time_now = LocalTime.now();
            if(startTime > time_now.getHour() || date_booking.isAfter(date_today)){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}

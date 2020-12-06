package database;

import devices.IoTDevice;
import model.Booking;
import model.Classroom;
import model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MySQLDBMgrImplProxy implements DBMgrImpl{
    private final MySQLDBMgrImpl mySQLDBMgr = new MySQLDBMgrImpl();

    private HashMap<String, Classroom> classroomCache = new HashMap<>();
    private HashMap<String, List<IoTDevice>> iotDeviceCache = new HashMap<>();

    // ============================== For User ==============================
    @Override   //無快取
    public void insertUser(User user) {
        mySQLDBMgr.insertUser(user);
    }
    @Override   //無快取
    public User getUserByAccount(String account) {
        return mySQLDBMgr.getUserByAccount(account);
    }
    @Override   //無快取
    public boolean getDuplicateBooking(String userAccount, String date, int startTime, int endTime) {
        return mySQLDBMgr.getDuplicateBooking(userAccount, date, startTime, endTime);
    }

    // ============================== For Classroom ==============================
    @Override   //重建快取
    public List<Classroom> getClassrooms() {
        List<Classroom> result = mySQLDBMgr.getClassrooms();
        for(Classroom classroom: result){
            classroomCache.put(classroom.getId(), classroom);
        }
        return result;
    }
    @Override   //快取
    public Classroom getClassroomById(String id) {
        Classroom classroom = classroomCache.get(id);
        if(classroom == null) {
            classroom = mySQLDBMgr.getClassroomById(id);
        }
        return classroom;
    }
    @Override   //無快取
    public List<Boolean> getAvailableTime(String classroomId, String date) {
        return mySQLDBMgr.getAvailableTime(classroomId, date);
    }

    // ============================== For Booking ==============================
    @Override   //無快取
    public void insertBooking(Booking booking) {
        //資料庫操作
        mySQLDBMgr.insertBooking(booking);
    }
    @Override   //無快取
    public void updateBooking(Booking booking) {
        mySQLDBMgr.updateBooking(booking);
    }
    @Override   //無快取
    public void deleteBookingById(int id) {
        mySQLDBMgr.deleteBookingById(id);
    }
    @Override   //快取內部之Classroom
    public List<Booking> getBookings() {
        List<Booking> result = mySQLDBMgr.getBookings();
        for(Booking booking: result){
            booking.setClassroom(getClassroomById(booking.getClassroomId()));
        }
        return result;
    }
    @Override   //快取內部之Classroom
    public Booking getBookingById(int id) {
        Booking booking = mySQLDBMgr.getBookingById(id);
        booking.setClassroom(getClassroomById(booking.getClassroomId()));
        return booking;
    }
    @Override   //快取內部之Classroom
    public List<Booking> getBookingsByAccount(String account) {
        List<Booking> result = mySQLDBMgr.getBookingsByAccount(account);
        for(Booking booking: result){
            booking.setClassroom(getClassroomById(booking.getClassroomId()));
        }
        return result;
    }

    // ============================== For IoTDevice ==============================
    @Override   //無快取
    public void updateIotDevice(IoTDevice device){
        mySQLDBMgr.updateIotDevice(device);
    }
    @Override   //無快取
    public String getStateFromIoTDevicesById(int id) {
        return mySQLDBMgr.getStateFromIoTDevicesById(id);
    }
    @Override   //快取
    public List<IoTDevice> getIoTDevicesByClassroomId(String id) {
        List<IoTDevice> devices = iotDeviceCache.get(id);
        if(devices == null) {
            devices = mySQLDBMgr.getIoTDevicesByClassroomId(id);
        }
        return devices;
    }
}

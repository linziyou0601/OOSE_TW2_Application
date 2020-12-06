package database;

import devices.IoTDevice;
import model.Booking;
import model.Classroom;
import model.User;

import java.util.*;
import java.util.stream.Collectors;

public class DBMgr {
    private DBMgrImpl dbmgrImpl;

    public DBMgr(DBMgrImpl dbmgrImpl) {
        this.dbmgrImpl = dbmgrImpl;
    }

    // For User
    public void insertUser(User user) {
        dbmgrImpl.insertUser(user);
    }
    public User getUserByAccount(String account) {
        return dbmgrImpl.getUserByAccount(account);
    }
    public boolean getDuplicateBooking(String userAccount, String date, int startTime, int endTime) { return dbmgrImpl.getDuplicateBooking(userAccount, date, startTime, endTime); };

    // For Classroom
    public List<Classroom> getClassrooms() {
        return dbmgrImpl.getClassrooms();
    }
    public Classroom getClassroomById(String id) {
        return dbmgrImpl.getClassroomById(id);
    }
    public List<Boolean> getAvailableTime(String classroomId, String date) { return dbmgrImpl.getAvailableTime(classroomId, date); };

    // For Booking
    public void insertBooking(Booking booking) {
        dbmgrImpl.insertBooking(booking);
    }
    public void updateBooking(Booking booking) {
        dbmgrImpl.updateBooking(booking);
    }
    public void deleteBookingById(int id) {
        dbmgrImpl.deleteBookingById(id);
    }
    public List<Booking> getBookings() {
        return dbmgrImpl.getBookings();
    }
    public Booking getBookingById(int id) {
        return dbmgrImpl.getBookingById(id);
    }
    public List<Booking> getBookingsByAccount(String account) {
        return dbmgrImpl.getBookingsByAccount(account);
    }

    // For IoTDevice
    public void updateIotDevice(IoTDevice device){ dbmgrImpl.updateIotDevice(device); }
    public String getStateFromIoTDevicesById(int id){ return dbmgrImpl.getStateFromIoTDevicesById(id); }
    public List<IoTDevice> getIoTDevicesByClassroomId(String id) { return dbmgrImpl.getIoTDevicesByClassroomId(id); }
}

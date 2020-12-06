package database;

import devices.IoTDevice;
import model.Booking;
import model.Classroom;
import model.User;

import java.util.List;

public interface DBMgrImpl {

    // For User
    void insertUser(User user);
    User getUserByAccount(String account);
    boolean getDuplicateBooking(String userAccount, String date, int startTime, int endTime);

    // For Classroom
    List<Classroom> getClassrooms();
    List<Classroom> getClassroomsByKeyword(String keyword);
    Classroom getClassroomById(String id);
    List<Boolean> getAvailableTime(String classroomId, String date);

    // For Booking
    void insertBooking(Booking booking);
    void updateBooking(Booking booking);
    void deleteBookingById(int id);
    Booking getBookingById(int id);
    List<Booking> getBookings();
    List<Booking> getBookingsByAccount(String account);

    // For IoTDevice
    void updateIotDevice(IoTDevice device);
    String getStateFromIoTDevicesById(int id);
    List<IoTDevice> getIoTDevicesByClassroomId(String id);
}

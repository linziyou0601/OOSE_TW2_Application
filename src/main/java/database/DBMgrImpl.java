package database;

import model.Booking;
import model.Classroom;
import model.User;

import java.util.List;

public interface DBMgrImpl {

    // For User
    void saveUser(User user);
    List<User> getUsers();
    User getUserByAccount(String account);

    // For Classroom
    void saveClassroom(Classroom classroom);
    List<Classroom> getClassrooms();
    Classroom getClassroomById(String id);

    // For Booking
    void saveBooking(Booking booking);
    List<Booking> getBookings();
    Booking getBookingById(int id);
    List<Booking> getBookingsByAccount(String account);
}

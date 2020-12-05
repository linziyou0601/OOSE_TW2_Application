package database;

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

    public void testQuery() {
        ((MySQLDBMgrImpl)dbmgrImpl).testQuery();
    }

    // For User
    public void saveUser(User user) {
        dbmgrImpl.saveUser(user);
    }

    public List<User> getUsers() {
        return dbmgrImpl.getUsers();
    }

    public User getUserByAccount(String account) {
        return dbmgrImpl.getUserByAccount(account);
    }

    // For Classroom
    public void saveClassroom(Classroom classroom) {
        dbmgrImpl.saveClassroom(classroom);
    }

    public List<Classroom> getClassrooms() {
        return dbmgrImpl.getClassrooms();
    }

    public Classroom getClassroomById(String id) {
        return dbmgrImpl.getClassroomById(id);
    }

    // For Booking
    public void saveBooking(Booking booking) {
        dbmgrImpl.saveBooking(booking);
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
}

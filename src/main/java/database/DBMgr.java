package database;

import model.Booking;
import model.Classroom;
import model.User;

import java.util.*;
import java.util.stream.Collectors;

public class DBMgr {
    private HashMap<String, User> userStorage = new HashMap<>();
    private HashMap<String, Classroom> classroomStorage = new HashMap<>();
    private HashMap<Integer, Booking> bookingStorage = new HashMap<>();

    public DBMgr() {

    }

    // For User
    public void saveUser(User user) {
        userStorage.put(user.getAccount(), user);
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.values());
    }

    public User getUserByAccount(String account) {
        return userStorage.get(account);
    }

    // For Classroom
    public void saveClassroom(Classroom classroom) {
        classroomStorage.put(classroom.getId(), classroom);
    }

    public List<Classroom> getClassrooms() {
        List<Classroom> result = classroomStorage.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> e.getValue()).collect(Collectors.toList());
        return result;
    }

    public Classroom getClassroomById(String id) {
        return classroomStorage.get(id);
    }

    // For Booking
    public void saveBooking(Booking booking) {
        bookingStorage.put(booking.getId(), booking);
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookingStorage.values());
    }

    public Booking getBookingById(int id) {
        return bookingStorage.get(id);
    }

    public List<Booking> getBookingsByAccount(String account) {
        ArrayList<Booking> result = new ArrayList<>();
        Iterator<Booking> bookingItr = bookingStorage.values().iterator();
        while (bookingItr.hasNext()) {
            Booking booking = bookingItr.next();
            if(booking.getUserAccount().equals(account)){
                result.add(booking);
            }
        }
        return result;
    }
}

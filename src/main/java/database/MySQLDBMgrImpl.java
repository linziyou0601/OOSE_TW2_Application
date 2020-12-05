package database;

import model.Booking;
import model.Classroom;
import model.User;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MySQLDBMgrImpl implements DBMgrImpl{
    private final String url = "jdbc:mysql://localhost:3306/dbmshw";
    private final String user = "root";
    private final String password = "";
    private Connection con;
    private PreparedStatement statement;
    private ResultSet rs;

    private HashMap<String, User> userStorage = new HashMap<>();
    private HashMap<String, Classroom> classroomStorage = new HashMap<>();
    private HashMap<Integer, Booking> bookingStorage = new HashMap<>();

    public void testQuery() {
        try {
            con = DriverManager.getConnection(url, user, password);         // 開啟與MySQL資料庫之間的連線
            statement = con.prepareStatement("SELECT * from pokemon");  // 取得實例化的Statement物件以執行Query
            rs = statement.executeQuery();                                  // 執行Select Query
            while (rs.next()) {
                System.out.println(
                        String.format(
                                "pokemonID: %s, pokemonName: %s, description: %s",
                                rs.getInt("pokemonID"),
                                rs.getString("pokemonName"),
                                rs.getString("description").replaceAll("\\r\\n", "")
                        )
                );
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection , stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { statement.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }

    @Override
    public void saveUser(User user) {
        userStorage.put(user.getAccount(), user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User getUserByAccount(String account) {
        return userStorage.get(account);
    }

    @Override
    public void saveClassroom(Classroom classroom) {
        classroomStorage.put(classroom.getId(), classroom);
    }

    @Override
    public List<Classroom> getClassrooms() {
        List<Classroom> result = classroomStorage.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> e.getValue()).collect(Collectors.toList());
        return result;
    }

    @Override
    public Classroom getClassroomById(String id) {
        return classroomStorage.get(id);
    }

    @Override
    public void saveBooking(Booking booking) {
        bookingStorage.put(booking.getId(), booking);
    }

    @Override
    public List<Booking> getBookings() {
        return new ArrayList<>(bookingStorage.values());
    }

    @Override
    public Booking getBookingById(int id) {
        return bookingStorage.get(id);
    }

    @Override
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

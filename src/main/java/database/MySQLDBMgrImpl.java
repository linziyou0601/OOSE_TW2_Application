package database;

import devicefactory.IoTDeviceFactory;
import devicefactory.IoTDeviceFactoryProducer;
import devices.IoTDevice;
import model.Admin;
import model.Booking;
import model.Classroom;
import model.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLDBMgrImpl implements DBMgrImpl{
    /*private final String url = "jdbc:mariadb://localhost:3306/oosetw2?useUnicode=true&characterEncoding=UTF-8";
    private final String user = "root";
    private final String password = "";*/

    private final String url = "jdbc:mysql://oosetw2.linziyou.info:3306/oosetw2?useUnicode=true&characterEncoding=UTF-8";
    private final String user = "oosetw2";
    private final String password = "drjennyoosetw";

    // =======================================================================
    // ======                                                           ======
    // ======                     For Configuration                     ======
    // ======                                                           ======
    // =======================================================================
    public Connection openConnection() {
        // 開啟與MySQL資料庫之間的連線
        Connection con = null;
        try { con = DriverManager.getConnection(url, user, password); }
        catch (SQLException throwables) { throwables.printStackTrace(); }
        return con;
    }

    public void closeConnection(Connection con, Statement stmt, ResultSet rs) {
        // 關閉連線
        try { con.close(); } catch(SQLException se) {}
        try { stmt.close(); } catch(SQLException se) {}
        if(rs!=null) try { rs.close(); } catch(SQLException se) {}
    }

    // =======================================================================
    // ======                                                           ======
    // ======                         For Admin                         ======
    // ======                                                           ======
    // =======================================================================
    @Override
    public Admin getAdminByAccount(String account){
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from admin WHERE account = ?");       // 取得實例化的Statement物件以執行Query
            stmt.setString(1,account);                                        // 設定參數
            rs = stmt.executeQuery();                                                       // 執行Select Query
            while (rs.next()) {
                Admin admin = new Admin();
                admin.setAccount(rs.getString("account"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                return admin;
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return null;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                          For User                         ======
    // ======                                                           ======
    // =======================================================================
    @Override
    public void insertUser(User user) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("INSERT INTO user VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, user.getAccount());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getPoint());
            stmt.setString(6, user.getPosition());
            stmt.executeUpdate();
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
    }
    @Override
    public User getUserByAccount(String account) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from user WHERE account = ?");
            stmt.setString(1,account);
            rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setAccount(rs.getString("account"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPoint(rs.getInt("point"));
                user.setPosition(rs.getString("position"));
                return user;
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return null;
    }
    @Override
    public boolean getDuplicateBooking(String userAccount, String date, int startTime, int endTime) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT count(*) as Numbers from booking WHERE userAccount = ? AND date = ? AND (endTime>=? AND startTime<=?)");
            stmt.setString(1, userAccount);
            stmt.setString(2, date);
            stmt.setInt(3, startTime);
            stmt.setInt(4, endTime);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt("Numbers") > 0;
            }
            // 重疊 == (start <= 已存在的end) && (end >= 已存在的start)
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return false;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                       For Classroom                       ======
    // ======                                                           ======
    // =======================================================================
    @Override
    public List<Classroom> getClassrooms() {
        List<Classroom> result = new ArrayList<>();

        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from classroom");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Classroom classroom = getClassroomFromResultSet(rs);
                result.add(classroom);
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return result;
    }
    @Override
    public List<Classroom> getClassroomsByKeyword(String keyword) {
        List<Classroom> result = new ArrayList<>();

        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from classroom WHERE id LIKE CONCAT('%', UPPER(?), '%')");
            stmt.setString(1, keyword);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Classroom classroom = getClassroomFromResultSet(rs);
                result.add(classroom);
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return result;
    }

    @Override
    public Classroom getClassroomById(String id) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from classroom WHERE id = ?");
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return getClassroomFromResultSet(rs);
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            closeConnection(con, stmt, rs);
        }
        return null;
    }
    @Override
    public List<Boolean> getAvailableTime(String classroomId, String date) {
         List<HashMap<String, Integer>> bookedList = new ArrayList<>();
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT startTime, endTime from booking WHERE classroomId = ? AND date = ?");
            stmt.setString(1, classroomId);
            stmt.setString(2, date);
            rs = stmt.executeQuery();
            while (rs.next()) {
                HashMap<String, Integer> valueSet = new HashMap<>();
                valueSet.put("startTime", rs.getInt("startTime"));
                valueSet.put("endTime", rs.getInt("endTime"));
                bookedList.add(valueSet);
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }

        //開始找有無重疊
        List<Boolean> result = new ArrayList<>();
        for(int i = 0; i <24; i++) {
            LocalDate date_booking = LocalDate.parse(date);
            LocalDate date_today = LocalDate.now();
            // 比較選定日期及目前日期
            if(!date_booking.isBefore(date_today)){
                LocalTime time_now = LocalTime.now();
                // 若選定日期為當天或未來，則比較時間
                if(i >= time_now.getHour() || date_booking.isAfter(date_today)){
                    //若時間為未來、或時間為當天的當下時刻之後
                    boolean overlap = false;
                    for(HashMap<String, Integer> booked: bookedList){
                        // 重疊 == (start <= 已存在的end) && (end >= 已存在的start)
                        if(booked.get("endTime")>=i && booked.get("startTime")<=i)
                            overlap = true;
                    }
                    result.add(i, !overlap);
                } else {
                    //若時間當天的過去
                    result.add(i, false);
                }
            } else {
                // 若選定日期為過去
                result.add(i, false);
            }
        }
        return result;
    }

    private Classroom getClassroomFromResultSet(ResultSet rs) {
        try {
            Classroom classroom = new Classroom();
            classroom.setId(rs.getString("id"));
            classroom.setType(rs.getString("type"));
            //classroom.setDevices(getIoTDevicesByClassroomId(rs.getString("id")));
            return classroom;
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        return null;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                        For Booking                        ======
    // ======                                                           ======
    // =======================================================================
    @Override
    public void insertBooking(Booking booking) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("INSERT INTO booking (date, startTime, endTime, classroomId, userAccount, activate) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, booking.getDate());
            stmt.setInt(2, booking.getStartTime());
            stmt.setInt(3, booking.getEndTime());
            stmt.setString(4, booking.getClassroomId());
            stmt.setString(5, booking.getUserAccount());
            stmt.setBoolean(6, booking.getActivate());
            stmt.executeUpdate();
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
    }
    @Override
    public void updateBooking(Booking booking) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("UPDATE booking SET activate = ? WHERE id = ?");
            stmt.setBoolean(1, booking.getActivate());
            stmt.setInt(2, booking.getId());
            stmt.executeUpdate();
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
    }
    @Override
    public void deleteBookingById(int id) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("DELETE FROM booking WHERE id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
    }
    @Override
    public List<Booking> getBookings() {
        List<Booking> result = new ArrayList<>();

        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from booking");
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(getBookingFromResultSet(rs));
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return result;
    }
    @Override
    public Booking getBookingById(int id) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from booking WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return getBookingFromResultSet(rs);
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return null;
    }
    @Override
    public List<Booking> getBookingsByAccount(String account) {
        List<Booking> result = new ArrayList<>();

        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from booking WHERE userAccount = ?");
            stmt.setString(1, account);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(getBookingFromResultSet(rs));
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return result;
    }

    private Booking getBookingFromResultSet(ResultSet rs) {
        try {
            Booking booking = new Booking();
            booking.setId(rs.getInt("id"));
            booking.setDate(rs.getString("date"));
            booking.setStartTime(rs.getInt("startTime"));
            booking.setEndTime(rs.getInt("endTime"));
            booking.setClassroomId(rs.getString("classroomId"));
            //booking.setClassroom(getClassroomById(rs.getString("classroomId")));// 有Proxy
            booking.setUser(getUserByAccount(rs.getString("userAccount")));
            booking.setActivate(rs.getBoolean("activate"));
            return booking;
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        return null;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                       For IoTDevice                       ======
    // ======                                                           ======
    // =======================================================================
    @Override
    public void updateIotDevice(IoTDevice device){
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("UPDATE iotdevice SET state = ? WHERE id = ?");
            stmt.setString(1, device.getState());
            stmt.setInt(2, device.getId());
            stmt.executeUpdate();
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
    }
    @Override
    public Map<Integer, String> getStateFromIoTDevices() {
        Map<Integer, String> result = new HashMap<>();
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT id, state from iotdevice");
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("id"), rs.getString("state"));
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return result;
    }
    @Override
    public String getStateFromIoTDevicesById(int id) {
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT state from iotdevice where id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getString("state");
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return null;
    }
    @Override
    public List<IoTDevice> getIoTDevicesByClassroomId(String classroomId) {
        List<IoTDevice> result = new ArrayList<>();
        //資料庫操作
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = openConnection();
            stmt = con.prepareStatement("SELECT * from iotdevice where classroomId = ?");
            stmt.setString(1, classroomId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String state = rs.getString("state");
                // 使用Factory Method取得IoT裝置物件
                IoTDeviceFactory deviceFactory = IoTDeviceFactoryProducer.getFactory(type);
                IoTDevice iotDevice = deviceFactory.instantiateIoTDevice(id, name, state);
                result.add(iotDevice);
            }
        }
        catch (Exception throwables) { throwables.printStackTrace(); }
        finally { closeConnection(con, stmt, rs); }
        return result;
    }
}

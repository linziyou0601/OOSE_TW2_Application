package database;

import devices.IoTDevice;
import model.Admin;
import model.Booking;
import model.Classroom;
import model.User;

import java.util.*;
import java.util.regex.Pattern;

public class MySQLDBMgrImplProxy implements DBMgrImpl{
    private final MySQLDBMgrImpl mySQLDBMgr = new MySQLDBMgrImpl();

    private HashMap<String, Classroom> classroomCache = new HashMap<>();        //快取物件暫存區
    private HashMap<String, List<IoTDevice>> iotDeviceCache = new HashMap<>();  //快取物件暫存區

    // =======================================================================
    // ======                                                           ======
    // ======                         For Admin                         ======
    // ======                                                           ======
    // =======================================================================
    @Override   //無快取
    public Admin getAdminByAccount(String account) {
        return mySQLDBMgr.getAdminByAccount(account);
    }

    // =======================================================================
    // ======                                                           ======
    // ======                          For User                         ======
    // ======                                                           ======
    // =======================================================================
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

    // =======================================================================
    // ======                                                           ======
    // ======                       For Classroom                       ======
    // ======                                                           ======
    // =======================================================================
    @Override   //快取
    public List<Classroom> getClassrooms() {
        List<Classroom> result;
        if(classroomCache.size()>0){
            result = (List<Classroom>)classroomCache.values();
        } else {
            result = mySQLDBMgr.getClassrooms();
            for (Classroom classroom : result) {
                classroom.setDevices(getIoTDevicesByClassroomId(classroom.getId()));
                classroomCache.put(classroom.getId(), classroom);
            }
        }
        return result;
    }
    @Override   //快取
    public List<Classroom> getClassroomsByKeyword(String keyword) {
        List<Classroom> result;
        if(classroomCache.size()>0){                            //若快取有資料
            Pattern p = Pattern.compile(".*"+keyword+".*");     // 關鍵字比對
            result = new ArrayList<>();
            Iterator classroomCacheItr = classroomCache.entrySet().iterator();
            while(classroomCacheItr.hasNext()){
                Map.Entry<String, Classroom> entry = (Map.Entry<String, Classroom>)classroomCacheItr.next();
                if(p.matcher(entry.getKey()).matches())
                    result.add(entry.getValue());
            }
        } else {                                                //若快取無資料
            result = mySQLDBMgr.getClassroomsByKeyword(keyword);        //代理給RealSubject，但IoT資料先不拿
            Iterator resultItr = result.iterator();
            while(resultItr.hasNext()){
                Classroom classroom = (Classroom)resultItr.next();
                classroom.setDevices(getIoTDevicesByClassroomId(classroom.getId())); //IoT一樣用Proxy的快取拿
                classroomCache.put(classroom.getId(), classroom);
            }
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

    // =======================================================================
    // ======                                                           ======
    // ======                        For Booking                        ======
    // ======                                                           ======
    // =======================================================================
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
        Iterator resultItr = result.iterator();
        while(resultItr.hasNext()){
            Booking booking = (Booking)resultItr.next();
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
        Iterator resultItr = result.iterator();
        while(resultItr.hasNext()){
            Booking booking = (Booking)resultItr.next();
            booking.setClassroom(getClassroomById(booking.getClassroomId()));
        }
        return result;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                       For IoTDevice                       ======
    // ======                                                           ======
    // =======================================================================
    @Override   //無快取
    public void updateIotDevice(IoTDevice device){
        mySQLDBMgr.updateIotDevice(device);
    }
    @Override
    public Map<Integer, String> getStateFromIoTDevices() {
        return mySQLDBMgr.getStateFromIoTDevices();
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
            iotDeviceCache.put(id, devices);
        }
        return devices;
    }
}

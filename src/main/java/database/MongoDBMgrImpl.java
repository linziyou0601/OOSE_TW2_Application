package database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import devicefactory.IoTDeviceFactory;
import devicefactory.IoTDeviceFactoryProducer;
import devices.IoTDevice;
import model.Admin;
import model.Booking;
import model.Classroom;
import model.User;
import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MongoDBMgrImpl implements DBMgrImpl{
    private final MongoClientURI uri = new MongoClientURI("mongodb://oosetw2:drjennyoosetw@oosetw2.linziyou.info:27017/oosetw2");

    private HashMap<String, Classroom> classroomCache = new HashMap<>();        //快取物件暫存區
    private HashMap<String, List<IoTDevice>> iotDeviceCache = new HashMap<>();  //快取物件暫存區

    // =======================================================================
    // ======                                                           ======
    // ======                     For Configuration                     ======
    // ======                                                           ======
    // =======================================================================
    public MongoDatabase openConnection() {
        // 開啟與MongoDB資料庫之間的連線
        MongoDatabase db = null;
        try {
            // 連接到 MongoDB 伺服器及資料庫
            MongoClient mongoClient = new MongoClient(uri);
            db = mongoClient.getDatabase("oosetw2");
        }
        catch (Exception e) { e.printStackTrace(); }
        return db;
    }

    public void closeConnection(MongoCursor<Document> cursor) {
        // 關閉連線的 Cursor
        try { cursor.close(); } catch(Exception e) {}
    }

    public Object getNextSequence(String name) {
        // 取得自增 id 值
        MongoDatabase db = openConnection();
        MongoCollection<Document> countersCollection = db.getCollection("counters");
        Document searchQuery = new Document("_id", name);
        Document increase = new Document("seq", 1);
        Document updateQuery = new Document("$inc", increase);
        Document result = countersCollection.findOneAndUpdate(searchQuery, updateQuery);
        return result.get("seq");
    }

    // =======================================================================
    // ======                                                           ======
    // ======                         For Admin                         ======
    // ======                                                           ======
    // =======================================================================
    @Override  //無快取
    public Admin getAdminByAccount(String account){
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("admin");   // 取得Collection
        Document searchQuery = new Document("account", account);                          // 設定Query Document
        FindIterable<Document> findIterable = collection.find(searchQuery);               // 依Query Document條件執行查詢
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()){
            Document document = mongoCursor.next();
            Admin admin = new Admin();
            admin.setAccount(document.getString("account"));
            admin.setUsername(document.getString("username"));
            admin.setPassword(document.getString("password"));
            return admin;
        }
        closeConnection(mongoCursor);
        return null;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                          For User                         ======
    // ======                                                           ======
    // =======================================================================
    @Override  //無快取
    public void insertUser(User user) {
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("user");
        Document document = new Document("account", user.getAccount())
                                .append("username", user.getUsername())
                                .append("password", user.getPassword())
                                .append("email", user.getEmail())
                                .append("point", user.getPoint())
                                .append("position", user.getPosition());
        collection.insertOne(document);
    }
    @Override  //無快取
    public User getUserByAccount(String account) {
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("user");
        Document searchQuery = new Document("account", account);
        FindIterable<Document> findIterable = collection.find(searchQuery);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()){
            Document document = mongoCursor.next();
            User user = new User();
            user.setAccount(document.getString("account"));
            user.setUsername(document.getString("username"));
            user.setPassword(document.getString("password"));
            user.setEmail(document.getString("email"));
            user.setPoint(document.getInteger("point"));
            user.setPosition(document.getString("position"));
            return user;
        }
        closeConnection(mongoCursor);
        return null;
    }
    @Override  //無快取
    public boolean getDuplicateBooking(String userAccount, String date, int startTime, int endTime) {
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("booking");
        Document searchQuery = new Document("userAccount", userAccount)
                                   .append("date", date)
                                   .append("endTime", new Document("$gte", startTime))
                                   .append("startTime", new Document("$lte", endTime));
        FindIterable<Document> findIterable = collection.find(searchQuery);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()) {
            return true;
        }
        closeConnection(mongoCursor);

        return false;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                       For Classroom                       ======
    // ======                                                           ======
    // =======================================================================
    @Override  //快取
    public List<Classroom> getClassrooms() {
        List<Classroom> result;
        if(classroomCache.size()>0){
            result = (List<Classroom>)classroomCache.values();
        } else {
            result = new ArrayList<>();

            //資料庫操作
            MongoDatabase db = openConnection();
            MongoCollection<Document> collection = db.getCollection("classroom");
            FindIterable<Document> findIterable = collection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                Classroom classroom = getClassroomFromDocument(document);
                classroom.setDevices(getIoTDevicesByClassroomId(classroom.getId()));    //取得快取裝置物件
                classroomCache.put(classroom.getId(), classroom);                       //儲存教室快取
                result.add(classroom);
            }
            closeConnection(mongoCursor);
        }
        return result;
    }
    @Override  //快取
    public List<Classroom> getClassroomsByKeyword(String keyword) {
        List<Classroom> result = new ArrayList<>();

        if(classroomCache.size()>0){
            Pattern p = Pattern.compile(".*"+keyword+".*");
            result = new ArrayList<>();
            for(String key: classroomCache.keySet())
                if(p.matcher(key).matches())
                    result.add(classroomCache.get(key));
        } else {
            //資料庫操作
            MongoDatabase db = openConnection();
            MongoCollection<Document> collection = db.getCollection("classroom");
            Document searchQuery = new Document("id", Pattern.compile(".*" + keyword + ".*", Pattern.CASE_INSENSITIVE));
            FindIterable<Document> findIterable = collection.find(searchQuery);
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                Classroom classroom = getClassroomFromDocument(document);
                classroom.setDevices(getIoTDevicesByClassroomId(classroom.getId()));    //取得快取裝置物件
                classroomCache.put(classroom.getId(), classroom);                       //儲存教室快取
                result.add(classroom);
            }
            closeConnection(mongoCursor);
        }
        return result;
    }

    @Override  //快取
    public Classroom getClassroomById(String id) {
        Classroom classroom = classroomCache.get(id);
        if(classroom == null) {
            //資料庫操作
            MongoDatabase db = openConnection();
            MongoCollection<Document> collection = db.getCollection("classroom");
            Document searchQuery = new Document("id", id);
            FindIterable<Document> findIterable = collection.find(searchQuery);
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                classroom = getClassroomFromDocument(document);
            }
        }
        return classroom;
    }
    @Override  //無快取
    public List<Boolean> getAvailableTime(String classroomId, String date) {
        List<HashMap<String, Integer>> bookedList = new ArrayList<>();

        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("booking");
        Document searchQuery = new Document("classroomId", classroomId).append("date", date);
        FindIterable<Document> findIterable = collection.find(searchQuery);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            HashMap<String, Integer> valueSet = new HashMap<>();
            valueSet.put("startTime", document.getInteger("startTime"));
            valueSet.put("endTime", document.getInteger("endTime"));
            bookedList.add(valueSet);
        }
        closeConnection(mongoCursor);

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

    private Classroom getClassroomFromDocument(Document document) {
        Classroom classroom = new Classroom();
        classroom.setId(document.getString("id"));
        classroom.setType(document.getString("type"));
        return classroom;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                        For Booking                        ======
    // ======                                                           ======
    // =======================================================================
    @Override  //無快取
    public void insertBooking(Booking booking) {
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("booking");
        Document document = new Document("_id", getNextSequence("bookingid"))
                                .append("date", booking.getDate())
                                .append("startTime", booking.getStartTime())
                                .append("endTime", booking.getEndTime())
                                .append("classroomId", booking.getClassroomId())
                                .append("userAccount", booking.getUserAccount())
                                .append("activate", booking.getActivate());
        collection.insertOne(document);
    }
    @Override  //無快取
    public void updateBooking(Booking booking) {
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("booking");
        Document searchQuery = new Document("_id", booking.getId());
        Document updateQuery = new Document("activate", booking.getActivate());
        collection.updateOne(searchQuery, new Document("$set", updateQuery));
    }
    @Override  //無快取
    public void deleteBookingById(int id) {
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("booking");
        Document searchQuery = new Document("_id", id);
        collection.deleteOne(searchQuery);
    }
    @Override
    public List<Booking> getBookings() {
        List<Booking> result = new ArrayList<>();

        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("booking");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()){
            Document document = mongoCursor.next();
            Booking booking = getBookingFromDocument(document);
            result.add(booking);
        }
        closeConnection(mongoCursor);
        return result;
    }
    @Override  //快取內部之Classroom
    public Booking getBookingById(int id) {
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("booking");
        Document searchQuery = new Document("_id", id);
        FindIterable<Document> findIterable = collection.find(searchQuery);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()){
            Document document = mongoCursor.next();
            Booking booking = getBookingFromDocument(document);
            booking.setClassroom(getClassroomById(booking.getClassroomId()));   //快取
            return booking;
        }
        closeConnection(mongoCursor);
        return null;
    }
    @Override  //快取內部之Classroom
    public List<Booking> getBookingsByAccount(String account) {
        List<Booking> result = new ArrayList<>();

        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("booking");
        Document searchQuery = new Document("userAccount", account);
        FindIterable<Document> findIterable = collection.find(searchQuery);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()){
            Document document = mongoCursor.next();
            Booking booking = getBookingFromDocument(document);
            booking.setClassroom(getClassroomById(booking.getClassroomId()));   //快取
            result.add(booking);
        }
        closeConnection(mongoCursor);
        return result;
    }

    private Booking getBookingFromDocument(Document document) {
        Booking booking = new Booking();
        booking.setId(document.getDouble("_id").intValue());
        booking.setDate(document.getString("date"));
        booking.setStartTime(document.getInteger("startTime"));
        booking.setEndTime(document.getInteger("endTime"));
        booking.setClassroomId(document.getString("classroomId"));
        booking.setUser(getUserByAccount(document.getString("userAccount")));
        booking.setActivate(document.getBoolean("activate"));
        return booking;
    }

    // =======================================================================
    // ======                                                           ======
    // ======                       For IoTDevice                       ======
    // ======                                                           ======
    // =======================================================================
    @Override  //無快取
    public void updateIotDevice(IoTDevice device){
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("iotdevice");
        Document searchQuery = new Document("_id", device.getId());
        Document updateQuery = new Document("state", device.getState());
        collection.updateOne(searchQuery, new Document("$set", updateQuery));
    }
    @Override  //無快取
    public Map<Integer, String> getStateFromIoTDevices() {
        Map<Integer, String> result = new HashMap<>();
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("iotdevice");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()){
            Document document = mongoCursor.next();
            result.put(document.getDouble("_id").intValue(), document.getString("state"));
        }
        closeConnection(mongoCursor);
        return result;
    }
    @Override  //無快取
    public String getStateFromIoTDevicesById(int id) {
        //資料庫操作
        MongoDatabase db = openConnection();
        MongoCollection<Document> collection = db.getCollection("iotdevice");
        Document searchQuery = new Document("_id", id);
        FindIterable<Document> findIterable = collection.find(searchQuery);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()){
            Document document = mongoCursor.next();
            return document.getString("state");
        }
        closeConnection(mongoCursor);
        return null;
    }
    @Override  //快取
    public List<IoTDevice> getIoTDevicesByClassroomId(String classroomId) {
        List<IoTDevice> devices = iotDeviceCache.get(classroomId);
        if(devices == null) {
            List<IoTDevice> result = new ArrayList<>();
            //資料庫操作
            MongoDatabase db = openConnection();
            MongoCollection<Document> collection = db.getCollection("iotdevice");
            Document searchQuery = new Document("classroomId", classroomId);
            FindIterable<Document> findIterable = collection.find(searchQuery);
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                String type = document.getString("type");
                int id = document.getDouble("_id").intValue();
                String name = document.getString("name");
                String state = document.getString("state");
                // 使用Factory Method取得IoT裝置物件
                IoTDeviceFactory deviceFactory = IoTDeviceFactoryProducer.getFactory(type);
                IoTDevice iotDevice = deviceFactory.instantiateIoTDevice(id, name, state);
                result.add(iotDevice);
            }
            closeConnection(mongoCursor);
            devices = result;
            iotDeviceCache.put(classroomId, devices);           //儲存裝置快取
        }
        return devices;
    }
}

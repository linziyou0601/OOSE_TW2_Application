package database;

import devices.IoTDevice;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import model.Admin;
import model.Booking;
import model.Classroom;
import model.User;

import java.util.List;
import java.util.Map;

public class DBMgr {
    private DBMgrImpl dbmgrImpl;

    public DBMgr(DBMgrImpl dbmgrImpl) {
        this.dbmgrImpl = dbmgrImpl;
    }

    // =======================================================================
    // ======                         For Admin                         ======
    // =======================================================================
    public Observable<Admin> getAdminByAccount(String account) {
        Observable observable = Observable.create((ObservableOnSubscribe<Admin>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getAdminByAccount(account));
            subscriber.onComplete();
        });
        return observable;
    }

    // =======================================================================
    // ======                          For User                         ======
    // =======================================================================
    public Completable insertUser(User user) {
        Completable completable = Completable.create(subscriber -> {
            dbmgrImpl.insertUser(user);
            subscriber.onComplete();
        });
        return completable;
    }
    public User syncGetUserByAccount(String account) {
        return dbmgrImpl.getUserByAccount(account);
    }
    public Observable<User> getUserByAccount(String account) {
        Observable observable = Observable.create((ObservableOnSubscribe<User>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getUserByAccount(account));
            subscriber.onComplete();
        });
        return observable;
    }
    public Observable<Boolean> getDuplicateBooking(String userAccount, String date, int startTime, int endTime) {
        Observable observable = Observable.create((ObservableOnSubscribe<Boolean>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getDuplicateBooking(userAccount, date, startTime, endTime));
            subscriber.onComplete();
        });
        return observable;
    }

    // =======================================================================
    // ======                       For Classroom                       ======
    // =======================================================================
    public Observable<List<Classroom>> getClassrooms() {
        Observable observable = Observable.create((ObservableOnSubscribe<List<Classroom>>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getClassrooms());
            subscriber.onComplete();
        });
        return observable;
    }
    public Observable<List<Classroom>> getClassroomsByKeyword(String keyword) {
        Observable observable = Observable.create((ObservableOnSubscribe<List<Classroom>>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getClassroomsByKeyword(keyword));
            subscriber.onComplete();
        });
        return observable;
    }
    public Observable<Classroom> getClassroomById(String id) {
        Observable observable = Observable.create((ObservableOnSubscribe<Classroom>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getClassroomById(id));
            subscriber.onComplete();
        });
        return observable;
    }
    public Observable<List<Boolean>> getAvailableTime(String classroomId, String date) {
        Observable observable = Observable.create((ObservableOnSubscribe<List<Boolean>>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getAvailableTime(classroomId, date));
            subscriber.onComplete();
        });
        return observable;
    }

    // =======================================================================
    // ======                        For Booking                        ======
    // =======================================================================
    public Completable insertBooking(Booking booking) {
        Completable completable = Completable.create(subscriber -> {
            dbmgrImpl.insertBooking(booking);
            subscriber.onComplete();
        });
        return completable;
    }
    public Completable updateBooking(Booking booking) {
        Completable completable = Completable.create(subscriber -> {
            dbmgrImpl.updateBooking(booking);
            subscriber.onComplete();
        });
        return completable;
    }
    public Completable deleteBookingById(int id) {
        Completable completable = Completable.create(subscriber -> {
            dbmgrImpl.deleteBookingById(id);
            subscriber.onComplete();
        });
        return completable;
    }
    public Observable<Booking> getBookingById(int id) {
        Observable observable = Observable.create((ObservableOnSubscribe<Booking>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getBookingById(id));
            subscriber.onComplete();
        });
        return observable;
    }
    public Observable<List<Booking>> getBookings() {
        Observable observable = Observable.create((ObservableOnSubscribe<List<Booking>>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getBookings());
            subscriber.onComplete();
        });
        return observable;
    }
    public Observable<List<Booking>> getBookingsByAccount(String account) {
        Observable observable = Observable.create((ObservableOnSubscribe<List<Booking>>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getBookingsByAccount(account));
            subscriber.onComplete();
        });
        return observable;
    }

    // =======================================================================
    // ======                       For IoTDevice                       ======
    // =======================================================================
    public Completable updateIotDevice(IoTDevice device){
        Completable completable = Completable.create(subscriber -> {
            dbmgrImpl.updateIotDevice(device);
            subscriber.onComplete();
        });
        return completable;
    }
    public Observable<Map<Integer, String>> getStateFromIoTDevices() {
        Observable observable = Observable.create((ObservableOnSubscribe<Map<Integer, String>>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getStateFromIoTDevices());
            subscriber.onComplete();
        });
        return observable;
    }
    public Observable<String> getStateFromIoTDevicesById(int id) {
        Observable observable = Observable.create((ObservableOnSubscribe<String>) subscriber -> {
            subscriber.onNext(dbmgrImpl.getStateFromIoTDevicesById(id));
            subscriber.onComplete();
        });
        return observable;
    }
    public List<IoTDevice> getIoTDevicesByClassroomId(String id) { return dbmgrImpl.getIoTDevicesByClassroomId(id); }
}

package ui.AdminAllBooking;

import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import model.Admin;
import model.Booking;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import ui.AdminBookingDetail.AdminBookingDetailView;
import ui.AdminLogin.AdminLoginView;
import ui.AdminMain.AdminMainView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdminAllBookingViewModel extends ViewModel {

    private Admin currentAdmin;
    private StringProperty username = new SimpleStringProperty();
    private StringProperty periodShowType = new SimpleStringProperty("");
    private ListProperty<Booking> bookingList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private IntegerProperty cancelValid = new SimpleIntegerProperty();
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public AdminAllBookingViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
        cancelValid.set(-1);
        loadingAlert.set(false);
    }

    // =============== Getter及Setter ===============
    public StringProperty usernameProperty(){ return username; }
    public StringProperty periodShowTypeProperty(){ return periodShowType; }
    public ListProperty<Booking> bookingListProperty(){ return bookingList; }
    public IntegerProperty cancelValidProperty(){
        return cancelValid;
    }
    public void setCancelValid(int valid){
        cancelValid.set(valid);
    }
    public BooleanProperty loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentAdmin = sessionContext.get("admin");
        username.set(currentAdmin.getUsername());
        periodShowType.set("CURRENT");
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
        dbmgr.getBookings()
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    List<Booking> bookings;
                    @Override
                    public void onNext(List<Booking> result) {
                        bookings = result;
                    }
                    @Override
                    public void onComplete(){
                        loadingAlert.set(false);
                        bookingList.clear();
                        Iterator<Booking> bookingItr = bookings.iterator();
                        while(bookingItr.hasNext()) {
                            Booking booking = bookingItr.next();
                            if((periodShowType.get().equals("CURRENT") && booking.isPeriod()) || (periodShowType.get().equals("FUTURE") && booking.isFuture()) || periodShowType.get().equals("ALL")) {
                                bookingList.add(booking);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e){
                        loadingAlert.set(false);
                        bookingList.clear();
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：登出
    public void logout() {
        sessionContext.clear();        //清除Session
        ViewManager.navigateTo(AdminLoginView.class);
    }

    // 邏輯處理：顯示目前時段
    public void currentPeriod() {
        periodShowType.set("CURRENT");
    }

    // 邏輯處理：顯示目前時段
    public void futurePeriod() {
        periodShowType.set("FUTURE");
    }

    // 邏輯處理：顯示所有時段
    public void allPeriod() {
        periodShowType.set("ALL");
    }

    // 邏輯處理：執行操作預約教室
    public void doOperateBooking(int id) {
        sessionContext.set("selectedBookingId", id);
        ViewManager.popUp(AdminBookingDetailView.class);
    }

    // 邏輯處理：執行取消預約教室
    public void cancelBookingValid(int id, String password) {
        if(currentAdmin.validate(password)) {
            // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
            loadingAlert.set(true);
            dbmgr.deleteBookingById(id)
                    .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                    .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                    .subscribe(new RxJavaCompletableObserver() {
                        @Override
                        public void onComplete() {
                            loadingAlert.set(false);
                            cancelValid.set(1);
                        }
                    });
            // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
        } else {
            cancelValid.set(0);
        }
    }

    // 邏輯處理：換頁 - 教室總覽
    public void toAllClassroom() {
        ViewManager.navigateTo(AdminMainView.class);
    }
}

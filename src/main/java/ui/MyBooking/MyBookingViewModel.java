package ui.MyBooking;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import model.Booking;
import model.User;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import ui.BookingDetail.BookingDetailView;
import ui.Dialog.*;
import ui.Login.LoginView;
import ui.Main.MainView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class MyBookingViewModel extends ViewModel {

    private User currentUser;
    private StringProperty username = new SimpleStringProperty();
    private StringProperty periodShowType = new SimpleStringProperty("");
    private ListProperty<Booking> bookingList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private ObjectProperty<JFXAlert> cancelAlert = new SimpleObjectProperty<>();
    private ObjectProperty<JFXAlert> loadingAlert = new SimpleObjectProperty<>();

    public MyBookingViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty usernameProperty(){ return username; }
    public StringProperty periodShowTypeProperty(){ return periodShowType; }
    public String getPeriodShowType(){ return periodShowType.get(); }
    public ListProperty<Booking> bookingListProperty(){ return bookingList; }
    public ObjectProperty<JFXAlert> cancelAlertProperty(){
        return cancelAlert;
    }
    public ObjectProperty<JFXAlert> loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentUser = sessionContext.get("user");
        username.set(currentUser.getUsername());
        periodShowType.set("CURRENT");
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        String account = currentUser.getAccount();
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        showLoading();
        dbmgr.getBookingsByAccount(account)
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
                        stopLoading();
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
                        stopLoading();
                        bookingList.clear();
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：設定 loading Alert()
    public void showLoading() {
        IAlertBuilder alertBuilder = new LoadingAlertBuilder();
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        loadingAlert.set(alert);
    }

    // 邏輯處理：停止 loading Alert()
    public void stopLoading() {
        loadingAlert.get().close();
    }

    // 邏輯處理：登出
    public void logout() {
        sessionContext.clear();        //清除Session
        ViewManager.navigateTo(LoginView.class);
    }

    // 邏輯處理：顯示目前時段
    public void currentPeriod() {
        periodShowType.set("CURRENT");
        refresh();
    }

    // 邏輯處理：顯示目前時段
    public void futurePeriod() {
        periodShowType.set("FUTURE");
        refresh();
    }

    // 邏輯處理：顯示所有時段
    public void allPeriod() {
        periodShowType.set("ALL");
        refresh();
    }

    // 邏輯處理：操作預約教室
    public void operateBooking(int id) {
        refresh();
        sessionContext.set("selectedBookingId", id);
        ViewManager.popUp(BookingDetailView.class);
    }

    // 邏輯處理：取消預約教室
    public void cancelBooking(int id) {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new InputAlertBuilder("確認取消預約", "請輸入使用者密碼", IAlertBuilder.AlertButtonType.OK, true);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<String> result = alert.showAndWait();
        if(result.isPresent()){
            if(currentUser.validate(result.get())) {
                // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
                showLoading();
                dbmgr.deleteBookingById(id)
                        .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                        .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                        .subscribe(new RxJavaCompletableObserver() {
                            @Override
                            public void onComplete() {
                                stopLoading();
                                triggerAlert(IAlertBuilder.AlertType.SUCCESS, "成功", "已取消預約");
                            }
                        });
                // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
            } else {
                triggerAlert(IAlertBuilder.AlertType.ERROR, "失敗", "密碼錯誤");
            }
            refresh();
        }
    }

    // 邏輯處理：取消預約結果Alert
    public void triggerAlert(IAlertBuilder.AlertType type, String title, String prompt){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(type, title, prompt, IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        cancelAlert.set(alert);
    }

    // 邏輯處理：換頁 - 總覽
    public void toHome() {
        ViewManager.navigateTo(MainView.class);
    }
}

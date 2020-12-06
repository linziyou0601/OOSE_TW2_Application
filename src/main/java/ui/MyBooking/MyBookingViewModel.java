package ui.MyBooking;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import mvvm.ViewModel;
import main.SessionContext;
import mvvm.ViewManager;
import model.Booking;
import model.User;
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
    private StringProperty periodShowType = new SimpleStringProperty("CURRENT");
    private ListProperty<Booking> bookingList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private ObjectProperty<JFXAlert> cancelAlert = new SimpleObjectProperty<>();

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

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentUser = dbmgr.getUserByAccount(sessionContext.get("userAccount"));
        username.set(currentUser.getUsername());
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        bookingList.clear();
        String account = currentUser.getAccount();
        List<Booking> bookings = dbmgr.getBookingsByAccount(account);
        Iterator<Booking> bookingItr = bookings.iterator();
        while(bookingItr.hasNext()) {
            Booking booking = bookingItr.next();
            if((periodShowType.get().equals("CURRENT") && booking.isPeriod()) || (periodShowType.get().equals("FUTURE") && booking.isFuture()) || periodShowType.get().equals("ALL")) {
                bookingList.add(booking);
            }
        }
    }

    // 邏輯處理：登出
    public void logout() {
        sessionContext.clear();        //清除Session
        ViewManager.navigateTo(LoginView.class);
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

    // 邏輯處理：操作預約教室
    public void operateBooking(int id) {
        refresh();
        sessionContext.set("selectedBookingId", id);
        ViewManager.popUp(BookingDetailView.class);
    }

    // 邏輯處理：取消預約教室
    public void cancelBooking(int id) {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new PasswordAlertBuilder("確認取消預約", "請輸入使用者密碼", IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<String> result = alert.showAndWait();
        if(result.isPresent()){
            if(currentUser.validate(result.get())) {
                dbmgr.deleteBookingById(id);
                triggerAlert(IAlertBuilder.AlertType.SUCCESS, "成功", "已取消預約");
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

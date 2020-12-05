package ui.MyBooking;

import database.DBMgr;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.ViewModel;
import main.SessionContext;
import main.ViewManager;
import model.Booking;
import model.User;
import ui.BookingDetail.BookingDetailView;
import ui.Login.LoginView;
import ui.Main.MainView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyBookingViewModel extends ViewModel {

    private User currentUser;
    private StringProperty username = new SimpleStringProperty();
    private StringProperty periodShowType = new SimpleStringProperty("CURRENT");
    private ListProperty<Booking> bookingList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    public MyBookingViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty usernameProperty(){ return username; }
    public StringProperty periodShowTypeProperty(){ return periodShowType; }
    public String getPeriodShowType(){ return periodShowType.get(); }
    public ListProperty<Booking> bookingListProperty(){ return bookingList; }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentUser = sessionContext.get("user");
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
    public void operateBooking(Booking booking) {
        refresh();
        sessionContext.set("selectedBooking", booking);
        ViewManager.popUp(BookingDetailView.class);
    }

    // 邏輯處理：換頁 - 總覽
    public void toHome() {
        ViewManager.navigateTo(MainView.class);
    }
}

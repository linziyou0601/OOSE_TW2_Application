package ui.MyBooking;

import database.DBMgr;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.IViewModel;
import main.SessionContext;
import main.ViewManager;
import model.Booking;
import model.Classroom;
import model.User;
import ui.Booking.BookingView;
import ui.BookingDetail.BookingDetailView;
import ui.Login.LoginView;
import ui.Main.MainView;

import java.time.LocalDate;
import java.util.ArrayList;

public class MyBookingViewModel implements IViewModel {

    private DBMgr dbmgr;
    private StringProperty username = new SimpleStringProperty();
    private StringProperty periodShowType = new SimpleStringProperty("CURRENT");
    private ListProperty<Booking> bookingList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    public MyBookingViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
    }

    // =============== Getter及Setter ===============
    public StringProperty usernameProperty(){ return username; }
    public StringProperty periodShowTypeProperty(){ return periodShowType; }
    public String getPeriodShowType(){ return periodShowType.get(); }
    public ListProperty<Booking> bookingListProperty(){ return bookingList; }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        username.bind(Bindings.createStringBinding(() -> ((User) SessionContext.getSession().get("user")).getUsername()));    //account綁定到User的account變數上
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        String account = ((User) SessionContext.getSession().get("user")).getAccount();
        bookingList.setAll(dbmgr.getBookingsByAccount(account));
    }

    // 邏輯處理：登出
    public void logout() {
        SessionContext.getSession().clear();        //清除Session
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
        SessionContext.getSession().set("selectedBooking", booking);
        ViewManager.popUp(BookingDetailView.class);
    }

    // 邏輯處理：換頁 - 總覽
    public void toHome() {
        ViewManager.navigateTo(MainView.class);
    }
}

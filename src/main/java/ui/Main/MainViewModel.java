package ui.Main;

import database.DBMgr;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import mvvm.ViewManager;
import mvvm.ViewModel;
import model.Classroom;
import model.User;
import ui.Booking.BookingView;
import ui.Login.LoginView;
import ui.MyBooking.MyBookingView;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainViewModel extends ViewModel {

    private User currentUser;
    private StringProperty queryString = new SimpleStringProperty();
    private ObjectProperty<LocalDate> queryDate = new SimpleObjectProperty<>(LocalDate.now());
    private StringProperty username = new SimpleStringProperty();
    private ListProperty<Classroom> classroomList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private int count = 0;

    public MainViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty queryStringProperty(){ return queryString; }
    public ObjectProperty<LocalDate> queryDateProperty(){ return queryDate; }
    public StringProperty usernameProperty(){ return username; }
    public ListProperty<Classroom> classroomListProperty(){ return classroomList; }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentUser = dbmgr.getUserByAccount(sessionContext.get("userAccount"));
        username.set(currentUser.getUsername());
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        classroomList.setAll(dbmgr.getClassrooms());
    }

    // 邏輯處理：登出
    public void logout() {
        sessionContext.clear();        //清除Session
        ViewManager.navigateTo(LoginView.class);
    }

    // 邏輯處理：選擇教室
    public void selectClassroom(Classroom classroom) {
        sessionContext.set("selectedClassroomId", classroom.getId());
        sessionContext.set("selectedDate", queryDate.get().toString());
        ViewManager.popUp(BookingView.class);
    }

    // 邏輯處理：換頁 - 我的預約
    public void toMyBooking() {
        ViewManager.navigateTo(MyBookingView.class);
    }
}

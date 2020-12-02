package ui.Main;

import database.DBMgr;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import main.ViewManager;
import main.IViewModel;
import model.Classroom;
import model.User;
import ui.Booking.BookingView;
import ui.Login.LoginView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel implements IViewModel {

    private DBMgr dbmgr;
    private StringProperty queryString = new SimpleStringProperty();
    private ObjectProperty<LocalDate> queryDate = new SimpleObjectProperty<>();
    private StringProperty account = new SimpleStringProperty();
    private ListProperty<Classroom> classroomList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private int count = 0;

    public MainViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
    }

    // =============== Getter及Setter ===============
    public StringProperty queryStringProperty(){ return queryString; }
    public ObjectProperty<LocalDate> queryDateProperty(){ return queryDate; }
    public StringProperty accountProperty(){ return account; }
    public ListProperty<Classroom> classroomListProperty(){ return classroomList; }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        account.bind(Bindings.createStringBinding(() -> ((User) SessionContext.getSession().get("user")).getUsername()));    //account綁定到User的account變數上
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        classroomList.setAll(dbmgr.getClassrooms());
    }

    // 邏輯處理：登出
    public void logout() {
        SessionContext.getSession().clear();        //清除Session
        ViewManager.navigateTo(LoginView.class);
    }

    // 邏輯處理：新增教室
    public void addClassroom() {
        Classroom classroom = new Classroom("教室代碼: " + String.valueOf(count++), "討論室");
        dbmgr.insertClassroom(classroom);
        refresh();
    }

    // 邏輯處理：選擇教室
    public void selectClassroom(String id) {
        SessionContext.getSession().set("selectedClassroomId", id);
        SessionContext.getSession().set("selectedDate", queryDate.get());
        ViewManager.popUp(BookingView.class);
    }
}

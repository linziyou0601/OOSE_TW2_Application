package ui.Main;

import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import model.Classroom;
import model.User;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import ui.Booking.BookingView;
import ui.Login.LoginView;
import ui.MyBooking.MyBookingView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private User currentUser;
    private StringProperty queryString = new SimpleStringProperty("");
    private ObjectProperty<LocalDate> queryDate = new SimpleObjectProperty<>(LocalDate.now());
    private StringProperty username = new SimpleStringProperty();
    private ListProperty<Classroom> classroomList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public MainViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
        loadingAlert.set(false);
    }

    // =============== Getter及Setter ===============
    public StringProperty queryStringProperty(){ return queryString; }
    public ObjectProperty<LocalDate> queryDateProperty(){ return queryDate; }
    public StringProperty usernameProperty(){ return username; }
    public ListProperty<Classroom> classroomListProperty(){ return classroomList; }
    public BooleanProperty loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentUser = sessionContext.get("user");
        username.set(currentUser.getUsername());
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
        dbmgr.getClassroomsByKeyword(queryString.get())         //以queryString異步請求classroom的物件List
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    List<Classroom> classrooms;
                    @Override
                    public void onNext(List<Classroom> result) { classrooms = result; }     // 當 取得結果時
                    @Override
                    public void onComplete(){                                               // 當 異步請求完成時
                        loadingAlert.set(false);
                        classroomList.setAll(classrooms);
                    }
                    @Override
                    public void onError(Throwable e){                                       // 當 結果為Null或請求錯誤時
                        loadingAlert.set(false);
                        classroomList.clear();
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
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

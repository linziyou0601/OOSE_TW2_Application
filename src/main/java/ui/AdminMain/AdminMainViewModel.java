package ui.AdminMain;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import model.Admin;
import model.Classroom;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import ui.AdminBooking.AdminBookingView;
import ui.AdminLogin.AdminLoginView;
import ui.Dialog.AlertDirector;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminMainViewModel extends ViewModel {

    private Admin currentAdmin;
    private StringProperty queryString = new SimpleStringProperty("");
    private ObjectProperty<LocalDate> queryDate = new SimpleObjectProperty<>(LocalDate.now());
    private StringProperty username = new SimpleStringProperty();
    private ListProperty<Classroom> classroomList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private ObjectProperty<JFXAlert> loadingAlert = new SimpleObjectProperty<>();
    private int count = 0;

    public AdminMainViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty queryStringProperty(){ return queryString; }
    public ObjectProperty<LocalDate> queryDateProperty(){ return queryDate; }
    public StringProperty usernameProperty(){ return username; }
    public ListProperty<Classroom> classroomListProperty(){ return classroomList; }
    public ObjectProperty<JFXAlert> loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentAdmin = sessionContext.get("admin");
        username.set(currentAdmin.getUsername());
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        showLoading();
        dbmgr.getClassroomsByKeyword(queryString.get())
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    List<Classroom> classrooms;
                    @Override
                    public void onNext(List<Classroom> result) {
                        classrooms = result;
                    }
                    @Override
                    public void onComplete(){
                        stopLoading();
                        classroomList.setAll(classrooms);
                    }
                    @Override
                    public void onError(Throwable e){
                        stopLoading();
                        classroomList.clear();
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
        ViewManager.navigateTo(AdminLoginView.class);
    }

    // 邏輯處理：選擇教室
    public void selectClassroom(Classroom classroom) {
        sessionContext.set("selectedClassroomId", classroom.getId());
        sessionContext.set("selectedDate", queryDate.get().toString());
        ViewManager.popUp(AdminBookingView.class);
    }

    // 邏輯處理：換頁 - 預約總覽
    public void toAllBooking() {
        //ViewManager.navigateTo(MyBookingView.class);
    }
}

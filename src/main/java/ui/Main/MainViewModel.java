package ui.Main;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.jfoenix.controls.JFXAlert;
import com.sun.media.jfxmedia.events.PlayerEvent;
import database.DBMgr;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.SessionContext;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import model.Classroom;
import model.User;
import org.jetbrains.annotations.NotNull;
import ui.Booking.BookingView;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;
import ui.Login.LoginView;
import ui.MyBooking.MyBookingView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainViewModel extends ViewModel {

    private User currentUser;
    private StringProperty queryString = new SimpleStringProperty("");
    private ObjectProperty<LocalDate> queryDate = new SimpleObjectProperty<>(LocalDate.now());
    private StringProperty username = new SimpleStringProperty();
    private ListProperty<Classroom> classroomList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private ObjectProperty<JFXAlert> loadingAlert = new SimpleObjectProperty<>();
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
    public ObjectProperty<JFXAlert> loadingAlertProperty(){
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
        showLoading();
        dbmgr.getClassroomsByKeyword(queryString.get())
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    @Override
                    public void onNext(List<Classroom> result) {
                        classroomList.setAll(result);
                    }
                    @Override
                    public void onComplete(){
                        stopLoading();
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

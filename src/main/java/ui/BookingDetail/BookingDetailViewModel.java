package ui.BookingDetail;

import database.DBMgr;
import devices.IoTDevice;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import model.Booking;
import model.Classroom;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import mvvm.ViewModel;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BookingDetailViewModel extends ViewModel {

    private Timer timer;
    private Booking selectedBooking;
    private Classroom classroom;
    private StringProperty classroomIdLabel = new SimpleStringProperty();
    private StringProperty dateLabel = new SimpleStringProperty();
    private StringProperty timeLabel = new SimpleStringProperty();
    private StringProperty currentTimeLabel = new SimpleStringProperty();
    private StringProperty restTimeLabel = new SimpleStringProperty();
    private BooleanProperty activate = new SimpleBooleanProperty();
    private ListProperty<IoTDevice> deviceList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private BooleanProperty timeout = new SimpleBooleanProperty();
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public BookingDetailViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
        loadingAlert.set(false);
    }

    // =============== Getter及Setter ===============
    public StringProperty classroomIdLabelProperty(){ return classroomIdLabel; }
    public StringProperty dateLabelProperty(){ return dateLabel; }
    public StringProperty timeLabelProperty(){ return timeLabel; }
    public StringProperty currentTimeLabelProperty(){ return currentTimeLabel; }
    public StringProperty restTimeLabelProperty(){ return restTimeLabel; }
    public BooleanProperty activateProperty(){ return activate; }
    public boolean getActivate(){ return activate.get(); }
    public BooleanProperty timeoutProperty(){ return timeout; }
    public ListProperty<IoTDevice> deviceListProperty(){ return deviceList; }
    public BooleanProperty loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        timeout.set(false);
        loadingAlert.set(true);
        dbmgr.getBookingById(sessionContext.get("selectedBookingId"))
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    @Override
                    public void onNext(Booking result) { selectedBooking = result; }
                    @Override
                    public void onComplete(){
                        loadingAlert.set(false);
                        classroom = selectedBooking.getClassroom();
                        timer = FxTimer.runPeriodically(
                                Duration.ofMillis(1000),
                                () -> {
                                    LocalTime lt = LocalTime.now();
                                    LocalTime ltEnd = LocalTime.parse(String.format("%02d", selectedBooking.getEndTime()) + ":59:59.999");
                                    Duration duration = Duration.between(lt, ltEnd);
                                    if(lt.isAfter(ltEnd)){
                                        stopTimer();
                                        timeout.set(true);
                                    } else {
                                        currentTimeLabel.set(lt.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                                        restTimeLabel.set(String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()));
                                    }
                                }
                        );
                        refresh();
                    }
                    @Override
                    public void onError(Throwable e){
                        loadingAlert.set(false);
                        timeout.set(true);
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        // 初始化 selectedBooking 相關參數
        activate.set(selectedBooking.getActivate());
        timeLabel.set((selectedBooking.getStartTime() + ":00 - " + (selectedBooking.getEndTime()+1) + ":00"));
        dateLabel.set(selectedBooking.getDate());
        // 初始化 classroom 相關參數
        classroomIdLabel.set(classroom.getId());
        deviceList.clear();
        deviceList.setAll(classroom.getDevices());
    }

    // 邏輯處理：開始使用
    public void activateBooking() {
        selectedBooking.setActivate(true);
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
        dbmgr.updateBooking(selectedBooking)
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                .subscribe(new RxJavaCompletableObserver() {
                    @Override
                    public void onComplete() {
                        loadingAlert.set(false);
                        refresh();
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    public void stopTimer() {
        timer.stop();
    }
}

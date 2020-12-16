package ui.AdminBookingDetail;

import database.DBMgr;
import devices.IoTDevice;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import model.Booking;
import model.Classroom;
import mvvm.RxJavaObserver;
import mvvm.ViewModel;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AdminBookingDetailViewModel extends ViewModel {

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
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public AdminBookingDetailViewModel(DBMgr dbmgr) {
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
    public ListProperty<IoTDevice> deviceListProperty(){ return deviceList; }
    public BooleanProperty loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
        dbmgr.getBookingById(sessionContext.get("selectedBookingId"))       //以selected booking id異步請求目前的booking物件
                .subscribeOn(Schedulers.newThread())                        //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())                      //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    @Override
                    public void onNext(Booking result) { selectedBooking = result; }        // 當 取得結果時
                    @Override
                    public void onComplete(){                                               // 當 異步請求完成時
                        loadingAlert.set(false);
                        classroom = selectedBooking.getClassroom();
                        timer = FxTimer.runPeriodically( Duration.ofMillis(1000), () -> {
                                //取得目前時間及到期時間
                                LocalTime lt = LocalTime.now();
                                LocalTime ltEnd = LocalTime.parse(String.format("%02d", selectedBooking.getEndTime()) + ":59:59.999");
                                Duration duration = Duration.between(lt, ltEnd);
                                //比較時間
                                if(lt.isAfter(ltEnd)){
                                    //若已到期
                                    stopTimer();
                                } else {
                                    //若未到期
                                    currentTimeLabel.set(lt.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                                    restTimeLabel.set(String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()));
                                }
                            }
                        );
                        refresh();
                    }
                    @Override
                    public void onError(Throwable e){ loadingAlert.set(false); }            // 當 結果為Null或請求錯誤時
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

    public void stopTimer() {
        timer.stop();
    }
}

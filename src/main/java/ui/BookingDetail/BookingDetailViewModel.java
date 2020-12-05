package ui.BookingDetail;

import database.DBMgr;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import java.time.Duration;
import main.ViewModel;
import main.SessionContext;
import model.Booking;
import model.Classroom;
import devices.IoTDevice;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;

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
    private BooleanProperty closeStage = new SimpleBooleanProperty();
    private ListProperty<IoTDevice> deviceList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    public BookingDetailViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty classroomIdLabelProperty(){ return classroomIdLabel; }
    public StringProperty dateLabelProperty(){ return dateLabel; }
    public StringProperty timeLabelProperty(){ return timeLabel; }
    public StringProperty currentTimeLabelProperty(){ return currentTimeLabel; }
    public StringProperty restTimeLabelProperty(){ return restTimeLabel; }
    public BooleanProperty activateProperty(){ return activate; }
    public boolean getActivate(){ return activate.get(); }
    public BooleanProperty closeStageProperty(){ return closeStage; }
    public ListProperty<IoTDevice> deviceListProperty(){ return deviceList; }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        selectedBooking = sessionContext.get("selectedBooking");
        classroom = dbmgr.getClassroomById(selectedBooking.getClassroomId());
        closeStage.set(false);
        timer = FxTimer.runPeriodically(
            Duration.ofMillis(1000),
            () -> {
                LocalTime lt = LocalTime.now();
                LocalTime ltEnd = LocalTime.parse(selectedBooking.getEndTime() + ":59:59.999");
                Duration duration = Duration.between(lt, ltEnd);
                if(lt.isAfter(ltEnd)){
                    closeStage.set(true);
                    stopTimer();
                } else {
                    currentTimeLabel.set(lt.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    restTimeLabel.set(String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart()));
                }
            }
        );
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        // 初始化 selectedBooking 參數
        activate.set(selectedBooking.getActivate());
        timeLabel.set((selectedBooking.getStartTime() + ":00 - " + (selectedBooking.getEndTime()+1) + ":00"));
        dateLabel.set(selectedBooking.getDate());
        // 初始化 classroom 參數
        classroomIdLabel.set(classroom.getId());
        deviceList.setAll(classroom.getDevices());
    }

    // 邏輯處理：開始使用
    public void activateBooking() {
        selectedBooking.setActivate(true);
        dbmgr.saveBooking(selectedBooking);
        refresh();
    }

    public void stopTimer() {
        timer.stop();
    }
}

package ui.BookingDetail;

import database.DBMgr;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.IViewModel;
import main.SessionContext;
import model.Booking;
import model.Classroom;
import devices.IoTDevice;

import java.util.ArrayList;

public class BookingDetailViewModel implements IViewModel {
    private DBMgr dbmgr;
    private Classroom classroom;
    private StringProperty classroomIdLabel = new SimpleStringProperty();
    private StringProperty timeLabel = new SimpleStringProperty();
    private BooleanProperty closeStage = new SimpleBooleanProperty();
    private ListProperty<IoTDevice> deviceList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    public BookingDetailViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
    }

    // =============== Getter及Setter ===============
    public StringProperty classroomIdLabelProperty(){ return classroomIdLabel; }
    public StringProperty timeLabelProperty(){ return timeLabel; }
    public BooleanProperty closeStageProperty(){ return closeStage; }
    public ListProperty<IoTDevice> deviceListProperty(){ return deviceList; }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        Booking selectedBooking = SessionContext.getSession().get("selectedBooking");
        classroom = selectedBooking.getClassroom();
        classroomIdLabel.set(classroom.getId());
        timeLabel.set(selectedBooking.getStartTime() + ":00 - " + (selectedBooking.getEndTime()+1) + ":00");
        closeStage.set(false);
        refresh();
    }

    // 邏輯處理：刷新頁面資料
    public void refresh() {
        classroom = dbmgr.getClassroomById(classroom.getId());
        deviceList.setAll(classroom.getDevices());
    }
}

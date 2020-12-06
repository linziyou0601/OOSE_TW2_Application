package ui.Booking;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import mvvm.ViewModel;
import main.SessionContext;
import model.Booking;
import model.Classroom;
import model.User;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;

import java.util.ArrayList;
import java.util.Optional;

public class BookingViewModel extends ViewModel {

    private User currentUser;
    private String selectedDate;
    private Classroom selectedClassroom;
    private StringProperty classroomIdLabel = new SimpleStringProperty();
    private StringProperty classroomTypeLabel = new SimpleStringProperty();
    private StringProperty computerCheck = new SimpleStringProperty("incorrect");
    private StringProperty projectorCheck = new SimpleStringProperty("incorrect");
    private StringProperty blackboardCheck = new SimpleStringProperty("incorrect");
    private StringProperty air_condCheck = new SimpleStringProperty("incorrect");
    private StringProperty speakerCheck = new SimpleStringProperty("incorrect");
    private StringProperty timeStart = new SimpleStringProperty("0");
    private StringProperty timeEnd = new SimpleStringProperty("0");
    private BooleanProperty closeStage = new SimpleBooleanProperty();
    private ListProperty<Boolean> availableTime = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private ObjectProperty<JFXAlert> submitAlert = new SimpleObjectProperty<>();

    public BookingViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty classroomIdLabelProperty(){ return classroomIdLabel; }
    public StringProperty classroomTypeLabelProperty(){ return classroomTypeLabel; }
    public StringProperty computerCheckProperty(){ return computerCheck; }
    public StringProperty projectorCheckProperty(){ return projectorCheck; }
    public StringProperty blackboardCheckProperty(){ return blackboardCheck; }
    public StringProperty air_condCheckProperty(){ return air_condCheck; }
    public StringProperty speakerCheckProperty(){ return speakerCheck; }
    public StringProperty timeStartProperty(){ return timeStart; }
    public StringProperty timeEndProperty(){ return timeEnd; }
    public BooleanProperty closeStageProperty(){ return closeStage; }
    public ListProperty<Boolean> availableTimeProperty(){ return availableTime; }
    public ObjectProperty<JFXAlert> submitAlertProperty(){
        return submitAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentUser = dbmgr.getUserByAccount(sessionContext.get("userAccount"));
        selectedDate = sessionContext.get("selectedDate");
        selectedClassroom = dbmgr.getClassroomById(sessionContext.get("selectedClassroomId"));
        classroomIdLabel.set(selectedClassroom.getId());
        classroomTypeLabel.set(selectedClassroom.getType());
        computerCheck.set(selectedClassroom.hasComputer()? "available": "unavailable");
        projectorCheck.set(selectedClassroom.hasProjector()? "available": "unavailable");
        blackboardCheck.set(selectedClassroom.hasBlackboard()? "available": "unavailable");
        air_condCheck.set(selectedClassroom.hasAirCond()? "available": "unavailable");
        speakerCheck.set(selectedClassroom.hasSpeaker()? "available": "unavailable");
        availableTime.setAll(dbmgr.getAvailableTime(selectedClassroom.getId(), sessionContext.get("selectedDate")));
        closeStage.set(false);
    }

    // 邏輯處理：預約處理
    public void submit() {
        String prompt = null;

        if(Integer.parseInt(timeEnd.get()) <= Integer.parseInt(timeStart.get())){
            prompt = "結束時間必需大於開始時間";
        } /*else if(dbmgr.getDuplicateBooking(currentUser.getAccount(), selectedDate, Integer.parseInt(timeStart.get()), Integer.parseInt(timeEnd.get()))){
            prompt = "該時段已借用其他教室";
        }*/ else {
            for (int i = Integer.parseInt(timeStart.get()); i < Integer.parseInt(timeEnd.get()); i++) {
                if (!availableTime.get(i)) {
                    prompt = "時間已被借用";
                    break;
                }
            }
        }

        if(prompt != null) {
            triggerFailedAlert(prompt);
        } else {
            Booking booking = new Booking(selectedDate, Integer.parseInt(timeStart.get()), Integer.parseInt(timeEnd.get())-1, selectedClassroom, currentUser);
            dbmgr.insertBooking(booking);
            triggerSucceedAlert(booking);
        }
    }

    // 邏輯處理：觸發失敗
    public void triggerFailedAlert(String prompt){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "預約失敗", prompt, IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        submitAlert.set(alert);
    }

    // 邏輯處理：觸發成功
    public void triggerSucceedAlert(Booking booking) {
        String prompt = "你選擇的教室是: " + booking.getClassroomId() + "\n" +
                        "你選擇的日期是: " + booking.getDate() + "\n" +
                        "你選擇的時間是: " + booking.getStartTime() + ":00 到 " + (booking.getEndTime()+1) + ":00";
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.SUCCESS, "預約成功", prompt, IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<Boolean> result = alert.showAndWait();
        if (result.isPresent()) {
            sessionContext.unset("selectedClassroomId");
            sessionContext.unset("selectedDate");
            closeStage.set(true);
        }
    }

    // 邏輯處理：選擇時間
    public void selectTime(int index) {
        timeStart.set(String.valueOf(index));
        timeEnd.set(String.valueOf(index+1));
    }
}

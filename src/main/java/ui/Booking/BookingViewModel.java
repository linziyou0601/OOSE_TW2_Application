package ui.Booking;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import main.SessionContext;
import model.Booking;
import model.Classroom;
import model.User;
import org.jetbrains.annotations.NotNull;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;
import ui.Main.MainView;

import java.util.ArrayList;
import java.util.List;
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
    private ObjectProperty<JFXAlert> loadingAlert = new SimpleObjectProperty<>();

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
    public ObjectProperty<JFXAlert> loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentUser = sessionContext.get("user");
        selectedDate = sessionContext.get("selectedDate");
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        showLoading();
        dbmgr.getClassroomById(sessionContext.get("selectedClassroomId"))
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    @Override
                    public void onNext(Classroom result) {
                        selectedClassroom = result;
                    }
                    @Override
                    public void onComplete(){
                        classroomIdLabel.set(selectedClassroom.getId());
                        classroomTypeLabel.set(selectedClassroom.getType());
                        computerCheck.set(selectedClassroom.hasComputer()? "available": "unavailable");
                        projectorCheck.set(selectedClassroom.hasProjector()? "available": "unavailable");
                        blackboardCheck.set(selectedClassroom.hasBlackboard()? "available": "unavailable");
                        air_condCheck.set(selectedClassroom.hasAirCond()? "available": "unavailable");
                        speakerCheck.set(selectedClassroom.hasSpeaker()? "available": "unavailable");

                        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
                        dbmgr.getAvailableTime(selectedClassroom.getId(), sessionContext.get("selectedDate"))
                                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                                .subscribe(new RxJavaObserver<>(){
                                    @Override
                                    public void onNext(List<Boolean> result) {
                                        availableTime.setAll(result);
                                    }
                                    @Override
                                    public void onComplete(){
                                        stopLoading();
                                    }
                                });
                        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====

        closeStage.set(false);
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

    // 邏輯處理：預約處理
    public void submit() {
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        showLoading();
        dbmgr.getDuplicateBooking(currentUser.getAccount(), selectedDate, Integer.parseInt(timeStart.get()), Integer.parseInt(timeEnd.get()))
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                .subscribe(new RxJavaObserver<>() {
                    boolean isDuplicate;

                    @Override
                    public void onNext(Boolean result) {
                        isDuplicate = result;
                    }
                    @Override
                    public void onComplete() {
                        String prompt = null;
                        if(Integer.parseInt(timeEnd.get()) <= Integer.parseInt(timeStart.get())){
                            prompt = "結束時間必需大於開始時間";
                        } else if(isDuplicate){
                            prompt = "該時段已借用其他教室";
                        } else {
                            for (int i = Integer.parseInt(timeStart.get()); i < Integer.parseInt(timeEnd.get()); i++) {
                                if (!availableTime.get(i)) {
                                    prompt = "時間已被借用";
                                    break;
                                }
                            }
                        }

                        if(prompt != null) {
                            stopLoading();
                            triggerFailedAlert(prompt);
                        } else {
                            Booking booking = new Booking(selectedDate, Integer.parseInt(timeStart.get()), Integer.parseInt(timeEnd.get())-1, selectedClassroom, currentUser);
                            // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
                            dbmgr.insertBooking(booking)
                                    .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                                    .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                                    .subscribe(new RxJavaCompletableObserver() {
                                        @Override
                                        public void onComplete() {
                                            stopLoading();
                                            triggerSucceedAlert(booking);
                                        }
                                    });
                            // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
                        }
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
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

package ui.Booking;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionContext;
import model.Booking;
import model.Classroom;
import model.User;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import mvvm.ViewModel;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingViewModel extends ViewModel {

    private User currentUser;
    private String selectedDate;
    private Classroom selectedClassroom;
    private Booking createdBooking;
    private StringProperty classroomIdLabel = new SimpleStringProperty();
    private StringProperty classroomTypeLabel = new SimpleStringProperty();
    private StringProperty computerCheck = new SimpleStringProperty("incorrect");
    private StringProperty projectorCheck = new SimpleStringProperty("incorrect");
    private StringProperty blackboardCheck = new SimpleStringProperty("incorrect");
    private StringProperty air_condCheck = new SimpleStringProperty("incorrect");
    private StringProperty speakerCheck = new SimpleStringProperty("incorrect");
    private StringProperty timeStart = new SimpleStringProperty("0");
    private StringProperty timeEnd = new SimpleStringProperty("0");
    private ListProperty<Boolean> availableTime = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private StringProperty bookingPrompt = new SimpleStringProperty();
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public BookingViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
        loadingAlert.set(false);
    }

    // =============== Getter及Setter ===============
    public Booking getCreatedBooking() { return createdBooking; }
    public StringProperty classroomIdLabelProperty(){ return classroomIdLabel; }
    public StringProperty classroomTypeLabelProperty(){ return classroomTypeLabel; }
    public StringProperty computerCheckProperty(){ return computerCheck; }
    public StringProperty projectorCheckProperty(){ return projectorCheck; }
    public StringProperty blackboardCheckProperty(){ return blackboardCheck; }
    public StringProperty air_condCheckProperty(){ return air_condCheck; }
    public StringProperty speakerCheckProperty(){ return speakerCheck; }
    public StringProperty timeStartProperty(){ return timeStart; }
    public StringProperty timeEndProperty(){ return timeEnd; }
    public ListProperty<Boolean> availableTimeProperty(){ return availableTime; }
    public StringProperty bookingPromptProperty(){
        return bookingPrompt;
    }
    public void setBookingPrompt(String prompt){
        bookingPrompt.set(prompt);
    }
    public BooleanProperty loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登入後參數對session綁定
    public void init() {
        currentUser = sessionContext.get("user");
        selectedDate = sessionContext.get("selectedDate");
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
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
                                    public void onComplete(){ loadingAlert.set(false); }
                                    @Override
                                    public void onError(Throwable e){ loadingAlert.set(false); }
                                });
                        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
                    }
                    @Override
                    public void onError(Throwable e){ loadingAlert.set(false); }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：預約處理
    public void submit() {
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
        dbmgr.getDuplicateBooking(currentUser.getAccount(), selectedDate, Integer.parseInt(timeStart.get()), Integer.parseInt(timeEnd.get())-1)
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
                        loadingAlert.set(false);
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
                            bookingPrompt.set(prompt);
                        } else {
                            createdBooking = new Booking(selectedDate, Integer.parseInt(timeStart.get()), Integer.parseInt(timeEnd.get())-1, selectedClassroom, currentUser);
                            // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
                            loadingAlert.set(true);
                            dbmgr.insertBooking(createdBooking)
                                    .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                                    .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                                    .subscribe(new RxJavaCompletableObserver() {
                                        @Override
                                        public void onComplete() {
                                            loadingAlert.set(false);
                                            bookingPrompt.set("成功");
                                        }
                                    });
                            // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
                        }
                    }
                    @Override
                    public void onError(Throwable e){
                        loadingAlert.set(false);
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：選擇時間
    public void selectTime(int index) {
        timeStart.set(String.valueOf(index));
        timeEnd.set(String.valueOf(index+1));
    }

    // 邏輯處理：執行預約成功後邏輯
    public void doPostSubmit() {
        sessionContext.unset("selectedClassroomId");
        sessionContext.unset("selectedDate");
    }
}

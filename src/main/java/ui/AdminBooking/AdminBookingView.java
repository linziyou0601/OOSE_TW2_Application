package ui.AdminBooking;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Booking;
import mvvm.View;
import mvvm.ViewModelProviders;
import ui.Dialog.*;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Optional;

public class AdminBookingView implements View {

    @FXML
    private Label classroomIdLabel; //教室名稱標籤

    @FXML
    private Label classroomTypeLabel;   //教室類型標籤

    @FXML
    private ArrayList<JFXButton> timeBtnList; //時間選擇鈕陣列

    @FXML
    private ImageView computerCheck;    //有無電腦

    @FXML
    private ImageView projectorCheck;   //有無投影機

    @FXML
    private ImageView blackboardCheck;  //有無黑板

    @FXML
    private ImageView air_condCheck;    //有無冷氣

    @FXML
    private ImageView speakerCheck;     //有無擴大機

    @FXML
    private JFXTextField timeStartInput;    //借用起始時間

    @FXML
    private JFXTextField timeEndInput;      //借用終止時間

    @FXML
    private JFXButton closeStageBtn;

    private AdminBookingViewModel adminBookingViewModel;

    private JFXAlert loadingAlert;

    @Override
    public void initialize() {
        // 指定 ViewModel
        adminBookingViewModel = ViewModelProviders.getInstance().get(AdminBookingViewModel.class);

        // 雙向綁定View資料和ViewModel資料
        ChangeListener<String> forceTimeSelectListener = ((observable, oldValue, newValue) -> {
            if (!newValue.matches("^(2[01234]|[10]?\\d)$")) ((StringProperty) observable).set(oldValue);
        });
        classroomIdLabel.textProperty().bindBidirectional(adminBookingViewModel.classroomIdLabelProperty());
        classroomTypeLabel.textProperty().bindBidirectional(adminBookingViewModel.classroomTypeLabelProperty());
        timeStartInput.textProperty().bindBidirectional(adminBookingViewModel.timeStartProperty());
        timeEndInput.textProperty().bindBidirectional(adminBookingViewModel.timeEndProperty());
        timeStartInput.textProperty().addListener(forceTimeSelectListener);
        timeEndInput.textProperty().addListener(forceTimeSelectListener);

        // 裝置列表
        adminBookingViewModel.computerCheckProperty().addListener((observable, oldValue, filename) -> {
            computerCheck.setImage(new Image("/images/"+ filename +".png"));
        });
        adminBookingViewModel.projectorCheckProperty().addListener((observable, oldValue, filename) -> {
            projectorCheck.setImage(new Image("/images/"+ filename +".png"));
        });
        adminBookingViewModel.blackboardCheckProperty().addListener((observable, oldValue, filename) -> {
            blackboardCheck.setImage(new Image("/images/"+ filename +".png"));
        });
        adminBookingViewModel.air_condCheckProperty().addListener((observable, oldValue, filename) -> {
            air_condCheck.setImage(new Image("/images/"+ filename +".png"));
        });
        adminBookingViewModel.speakerCheckProperty().addListener((observable, oldValue, filename) -> {
            speakerCheck.setImage(new Image("/images/"+ filename +".png"));
        });

        // 可用時間
        adminBookingViewModel.availableTimeProperty().addListener((observable, oldValue, availablesTimeList) -> {
            ListIterator<Boolean> availableIter = availablesTimeList.listIterator();
            while(availableIter.hasNext()){
                int index = availableIter.nextIndex();
                boolean available = availableIter.next();
                JFXButton timeBtn = timeBtnList.get(index);
                if(available) {
                    timeBtn.setStyle("-fx-background-color: #4DB6AC");
                    timeBtn.setDisable(false);
                } else {
                    timeBtn.setStyle("-fx-background-color: #E6E6E6");
                    timeBtn.setDisable(true);
                }
            }
        });

        // 綁定時間按鈕
        ListIterator<JFXButton> timeBtnIter = timeBtnList.listIterator();
        while(timeBtnIter.hasNext()) {
            int index = timeBtnIter.nextIndex();
            JFXButton timeBtn = timeBtnIter.next();
            timeBtn.setOnAction(e -> adminBookingViewModel.selectTime(index));
        }

        // 綁定 Loading Alert 變數
        adminBookingViewModel.loadingAlertProperty().addListener((observable, oldStatus, newStatus) -> {
            if(newStatus) showLoading();
            else stopLoading();
        });

        // 綁定 Booking Prompt 變數
        adminBookingViewModel.bookingPromptProperty().addListener((observable, oldPrompt, newPrompt) -> {
            switch(newPrompt){
                case "":
                    break;
                case "成功":
                    showBookingSucceedAlert(adminBookingViewModel.getCreatedBooking());
                    break;
                default:
                    showBookingFailedAlert(newPrompt);
                    break;
            }
            adminBookingViewModel.setBookingPrompt("");
        });

        // 初始化 Loading Alert（要在主UI線程執行後執行）
        Platform.runLater(() -> {
            IAlertBuilder alertBuilder = new LoadingAlertBuilder();
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            loadingAlert = alertBuilder.getAlert();

            // 初始化ViewModel
            adminBookingViewModel.init();
        });
    }

    //預約鈕
    public void submitBtnClick() {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new InputAlertBuilder("代為預約", "請輸入使用者名稱", IAlertBuilder.AlertButtonType.OK, false);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<String> result = alert.showAndWait();
        if(result.isPresent()){
            adminBookingViewModel.submitBookingValid(result.get());
        }
    }

    //顯示 Loading Alert
    public void showLoading() {
        loadingAlert.show();
    }

    //停止 Loading Alert
    public void stopLoading() {
        loadingAlert.close();
    }

    //顯示失敗 Alert
    public void showBookingFailedAlert(String prompt){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "預約失敗", prompt, IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        alert.show();
    }

    //顯示成功 Alert
    public void showBookingSucceedAlert(Booking booking) {
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
            adminBookingViewModel.doPostSubmit();
            closeStageBtn.fire();
        }
    }
}
package ui.AdminBooking;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mvvm.View;
import mvvm.ViewModelProviders;

import java.util.ArrayList;
import java.util.ListIterator;

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
    private JFXButton submitBtn;    //預約按鈕

    @FXML
    private JFXButton closeStageBtn;

    private AdminBookingViewModel adminBookingViewModel;

    @Override
    public void initialize() {
        adminBookingViewModel = ViewModelProviders.getInstance().get(AdminBookingViewModel.class);

        // 關閉popUp訊號
        adminBookingViewModel.closeStageProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) closeStageBtn.fire();
        });

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

        // 綁定 submitAlert 變數
        adminBookingViewModel.submitAlertProperty().addListener((observable, oldAlert, newAlert) -> newAlert.show());

        // 綁定 Loading Alert 變數
        adminBookingViewModel.loadingAlertProperty().addListener((observable, oldAlert, newAlert) -> Platform.runLater(() -> newAlert.show()));

        adminBookingViewModel.init();
    }

    //預約鈕
    public void submitBtnClick() {
        adminBookingViewModel.submit();
    }
}
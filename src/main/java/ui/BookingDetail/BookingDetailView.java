package ui.BookingDetail;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import mvvm.View;
import mvvm.ViewModelProviders;
import devices.IoTDevice;
import observer.and.adapter.DeviceIconObserver;
import observer.and.adapter.IObservable;
import observer.and.adapter.PowerBtnObserver;

import java.io.IOException;
import java.util.ListIterator;

public class BookingDetailView implements View {

    @FXML
    private Label classroomIdLabel; //教室名稱標籤

    @FXML
    private Label dateLabel;   //預約日期標籤
    @FXML
    private Label timeLabel;   //預約時間標籤
    @FXML
    private Label currentTimeLabel;   //目前時間標籤
    @FXML
    private Label restTimeLabel;   //目前時間標籤
    @FXML
    private JFXButton activateBtn;  //啟用按鈕

    @FXML
    private JFXMasonryPane deviceListPane;   //裝置清單

    @FXML
    private JFXButton closeStageBtn;

    private BookingDetailViewModel bookingDetailViewModel;

    @Override
    public void initialize() {
        bookingDetailViewModel = ViewModelProviders.getInstance().get(BookingDetailViewModel.class);

        // 預約鈕
        //submitBtn.setOnAction(e -> bookingDetailViewModel.submit());

        // 啟用鈕
        activateBtn.setOnAction(e -> bookingDetailViewModel.activateBooking());
        activateBtn.disableProperty().bindBidirectional(bookingDetailViewModel.activateProperty());

        // 關閉popUp訊號
        bookingDetailViewModel.closeStageProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) closeStageBtn.fire();
        });

        // 雙向綁定View資料和ViewModel資料
        classroomIdLabel.textProperty().bindBidirectional(bookingDetailViewModel.classroomIdLabelProperty());
        dateLabel.textProperty().bindBidirectional(bookingDetailViewModel.dateLabelProperty());
        timeLabel.textProperty().bindBidirectional(bookingDetailViewModel.timeLabelProperty());
        currentTimeLabel.textProperty().bindBidirectional(bookingDetailViewModel.currentTimeLabelProperty());
        restTimeLabel.textProperty().bindBidirectional(bookingDetailViewModel.restTimeLabelProperty());

        // 裝置列表
        deviceListPane.disableProperty().bind(Bindings.createBooleanBinding(() -> !bookingDetailViewModel.getActivate(), bookingDetailViewModel.activateProperty()));
        bookingDetailViewModel.deviceListProperty().addListener((observable, oldValue, deviceList) -> {
            deviceListPane.getChildren().clear();
            ListIterator<IoTDevice> deviceIter = deviceList.listIterator();
            while(deviceIter.hasNext()){
                IoTDevice device = deviceIter.next();
                try {
                    // 取得 classroomCard 佈局元件
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/drawable/deviceCard.fxml"));
                    Parent rootNode = loader.load();
                    JFXButton powerBtn = (JFXButton) rootNode.lookup("#powerBtn");
                    ImageView deviceImage = (ImageView) rootNode.lookup("#deviceImage");
                    Label deviceNameLabel = (Label) rootNode.lookup("#deviceNameLabel");
                    // 將資料設到元件上
                    DeviceIconObserver deviceIconObserver = new DeviceIconObserver(deviceImage, deviceNameLabel);
                    PowerBtnObserver powerBtnObserver = new PowerBtnObserver(powerBtn);
                    ((IObservable)device).addObserve(deviceIconObserver);
                    ((IObservable)device).addObserve(powerBtnObserver);
                    powerBtn.setOnAction(e -> device.switchState());
                    deviceListPane.getChildren().add(rootNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 綁定 Loading Alert 變數
        bookingDetailViewModel.loadingAlertProperty().addListener((observable, oldAlert, newAlert) -> Platform.runLater(() -> newAlert.show()));

        bookingDetailViewModel.init();
    }
}
package ui.BookingDetail;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import main.ViewModelProviders;
import devices.IoTDevice;
import observer.and.adapter.DeviceIconObserver;
import observer.and.adapter.Observable;
import observer.and.adapter.PowerBtnObserver;

import java.io.IOException;
import java.util.ListIterator;

public class BookingDetailView {

    @FXML
    private Label classroomIdLabel; //教室名稱標籤

    @FXML
    private Label timeLabel;   //預約時間標籤

    @FXML
    private JFXMasonryPane deviceListPane;   //預約清單

    @FXML
    private JFXButton closeStageBtn;

    private BookingDetailViewModel bookingDetailViewModel;

    public void initialize() {
        bookingDetailViewModel = ViewModelProviders.getInstance().get(BookingDetailViewModel.class);

        // 預約鈕
        //submitBtn.setOnAction(e -> bookingDetailViewModel.submit());

        // 關閉popUp訊號
        bookingDetailViewModel.closeStageProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) closeStageBtn.fire();
        });

        // 雙向View資料和ViewModel資料
        classroomIdLabel.textProperty().bindBidirectional(bookingDetailViewModel.classroomIdLabelProperty());
        timeLabel.textProperty().bindBidirectional(bookingDetailViewModel.timeLabelProperty());

        // 裝置列表
        bookingDetailViewModel.deviceListProperty().addListener((observable, oldValue, deviceList) -> {
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
                    ((Observable)device).addObserve(deviceIconObserver);
                    ((Observable)device).addObserve(powerBtnObserver);
                    powerBtn.setOnAction(e -> device.switchState());
                    deviceListPane.getChildren().add(rootNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        bookingDetailViewModel.init();
    }
}
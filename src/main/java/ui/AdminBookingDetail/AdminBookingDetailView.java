package ui.AdminBookingDetail;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import devices.IoTDevice;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import mvvm.View;
import mvvm.ViewModelProviders;
import observer.and.adapter.DeviceIconObserver;
import observer.and.adapter.IObservable;
import observer.and.adapter.PowerBtnObserver;
import ui.Dialog.AlertDirector;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;

public class AdminBookingDetailView implements View {

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
    private Label activateLabel;  //啟用狀態標籤

    @FXML
    private JFXMasonryPane deviceListPane;   //裝置清單

    @FXML
    private JFXButton closeStageBtn;

    private AdminBookingDetailViewModel adminBookingDetailViewModel;

    private JFXAlert loadingAlert;

    @Override
    public void initialize() {
        // 指定 ViewModel
        adminBookingDetailViewModel = ViewModelProviders.getInstance().get(AdminBookingDetailViewModel.class);

        // 啟用鈕
        activateLabel.textProperty().bind(Bindings.createStringBinding(() -> adminBookingDetailViewModel.getActivate()? "已開始使用": "未使用", adminBookingDetailViewModel.activateProperty()));

        // 雙向綁定View資料和ViewModel資料
        classroomIdLabel.textProperty().bindBidirectional(adminBookingDetailViewModel.classroomIdLabelProperty());
        dateLabel.textProperty().bindBidirectional(adminBookingDetailViewModel.dateLabelProperty());
        timeLabel.textProperty().bindBidirectional(adminBookingDetailViewModel.timeLabelProperty());
        currentTimeLabel.textProperty().bindBidirectional(adminBookingDetailViewModel.currentTimeLabelProperty());
        restTimeLabel.textProperty().bindBidirectional(adminBookingDetailViewModel.restTimeLabelProperty());

        // 裝置列表
        adminBookingDetailViewModel.deviceListProperty().addListener((observable, oldValue, deviceList) -> {
            setDeviceListPane(deviceList);
        });

        // 綁定 Loading Alert 變數
        adminBookingDetailViewModel.loadingAlertProperty().addListener((observable, oldStatus, newStatus) -> {
            if(newStatus) showLoading();
            else stopLoading();
        });

        // 初始化 Loading Alert（要在主UI線程執行後執行）
        Platform.runLater(() -> {
            IAlertBuilder alertBuilder = new LoadingAlertBuilder();
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            loadingAlert = alertBuilder.getAlert();

            // 初始化ViewModel
            adminBookingDetailViewModel.init();
        });
    }

    //將裝置設定到教室上
    public void setDeviceListPane(List<IoTDevice> deviceList) {
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
    }

    //顯示 Loading Alert
    public void showLoading() {
        loadingAlert.show();
    }

    //停止 Loading Alert
    public void stopLoading() {
        loadingAlert.close();
    }
}
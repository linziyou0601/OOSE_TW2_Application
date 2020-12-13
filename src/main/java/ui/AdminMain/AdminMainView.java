package ui.AdminMain;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import devices.IoTDevice;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Admin;
import model.Classroom;
import mvvm.RxJavaCompletableObserver;
import mvvm.View;
import mvvm.ViewModelProviders;
import observer.and.adapter.DeviceIconObserver;
import observer.and.adapter.IObservable;
import observer.and.adapter.PowerBtnObserver;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AdminMainView implements View {

    @FXML
    private JFXTextField queryInput;    //搜尋欄位
    @FXML
    private JFXButton searchBtn;    //搜尋按鈕
    @FXML
    private DatePicker datepicker;  //日期選擇

    @FXML
    private Label usernameLabel;     //顯示目前帳號
    @FXML
    private JFXButton toAllClassroomBtn;    //按鈕前往：教室總覽
    @FXML
    private JFXButton toAllBookingBtn; //按鈕前往：預約總覽
    @FXML
    private JFXButton logoutBtn;    //登出按鈕

    @FXML
    private VBox classroomListBox;   //教室清單

    private AdminMainViewModel adminMainViewModel;

    @Override
    public void initialize() {
        adminMainViewModel = ViewModelProviders.getInstance().get(AdminMainViewModel.class);

        // 雙向綁定View資料和ViewModel資料
        queryInput.textProperty().bindBidirectional(adminMainViewModel.queryStringProperty());
        datepicker.valueProperty().bindBidirectional(adminMainViewModel.queryDateProperty());
        usernameLabel.textProperty().bindBidirectional(adminMainViewModel.usernameProperty());

        // 教室清單
        adminMainViewModel.classroomListProperty().addListener((observable, oldValue, classroomList) -> refreshClassroomListPane(classroomList));

        // 綁定 Loading Alert 變數
        adminMainViewModel.loadingAlertProperty().addListener((observable, oldAlert, newAlert) -> Platform.runLater(() -> newAlert.show()));

        adminMainViewModel.init();
    }

    //我的預約鈕
    public void toAllBookingBtnClick() {
        adminMainViewModel.toAllBooking();
    }

    //登出鈕
    public void logoutBtnClick() {
        adminMainViewModel.logout();
    }

    //搜尋鈕
    public void searchBtnClick() {
        adminMainViewModel.refresh();
    }

    //重新整理教室列表
    public void refreshClassroomListPane(List<Classroom> classroomList) {
        classroomListBox.getChildren().clear();
        Iterator<Classroom> classroomItr = classroomList.iterator();
        while(classroomItr.hasNext()) {
            Classroom classroom = classroomItr.next();
            try {
                // 取得 classroomListCard 佈局元件
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/drawable/classroomListCard.fxml"));
                Parent rootNode = loader.load();
                Label classroomIdLabel = (Label) rootNode.lookup("#classroomIdLabel");
                Label classroomTypeLabel = (Label) rootNode.lookup("#classroomTypeLabel");
                Label bookingTag = (Label) rootNode.lookup("#bookingTag");
                ScrollPane deviceScrollPane = (ScrollPane) rootNode.lookup("#deviceScrollPane");
                HBox deviceListBox = (HBox) deviceScrollPane.getContent().lookup("#deviceListBox");
                // 將 classroom 資料設到元件上
                classroomIdLabel.setText(classroom.getId());
                classroomTypeLabel.setText(classroom.getType());
                bookingTag.setOnMouseClicked(e -> adminMainViewModel.selectClassroom(classroom));

                setDeviceListBox(deviceListBox, classroom.getDevices())
                        .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                        .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                        .subscribe(new RxJavaCompletableObserver() {
                            @Override
                            public void onComplete() { classroomListBox.getChildren().add(rootNode); }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //將裝置設定到教室列表上
    public Completable setDeviceListBox(HBox deviceListBox, List<IoTDevice> deviceList) {
        Completable completable = Completable.create(subscriber -> {
            ListIterator<IoTDevice> deviceIter = deviceList.listIterator();
            while(deviceIter.hasNext()){
                IoTDevice device = deviceIter.next();
                try {
                    // 取得 deviceCard 佈局元件
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/drawable/deviceCard.fxml"));
                    Parent root = loader.load();
                    JFXButton powerBtn = (JFXButton) root.lookup("#powerBtn");
                    ImageView deviceImage = (ImageView) root.lookup("#deviceImage");
                    Label deviceNameLabel = (Label) root.lookup("#deviceNameLabel");
                    // 將資料設到元件上
                    DeviceIconObserver deviceIconObserver = new DeviceIconObserver(deviceImage, deviceNameLabel);
                    PowerBtnObserver powerBtnObserver = new PowerBtnObserver(powerBtn);
                    ((IObservable)device).addObserve(deviceIconObserver);
                    ((IObservable)device).addObserve(powerBtnObserver);
                    powerBtn.setOnAction(e -> device.switchState());
                    deviceListBox.getChildren().add(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            subscriber.onComplete();
        });
        return completable;
    }
}
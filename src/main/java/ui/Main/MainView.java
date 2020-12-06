package ui.Main;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import mvvm.View;
import mvvm.ViewModelProviders;
import model.Classroom;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;

import java.io.IOException;
import java.util.Iterator;

public class MainView implements View {

    @FXML
    private JFXTextField queryInput;    //搜尋欄位
    @FXML
    private JFXButton searchBtn;    //搜尋按鈕
    @FXML
    private DatePicker datepicker;  //日期選擇

    @FXML
    private Label usernameLabel;     //顯示目前帳號
    @FXML
    private JFXButton toHomeBtn;    //按鈕前往：總覽
    @FXML
    private JFXButton toBookingBtn; //按鈕前往：預約
    @FXML
    private JFXButton toSubscribeBtn; //按鈕前往：訂閱
    @FXML
    private JFXButton logoutBtn;    //登出按鈕

    @FXML
    private JFXMasonryPane classroomListPane;   //教室清單

    private MainViewModel mainViewModel;

    @Override
    public void initialize() {
        mainViewModel = ViewModelProviders.getInstance().get(MainViewModel.class);

        // 換頁鈕
        toHomeBtn.setOnAction(e -> {});
        toBookingBtn.setOnAction(e -> mainViewModel.toMyBooking());
        toSubscribeBtn.setOnAction(e -> {});

        // 登出鈕
        logoutBtn.setOnAction(e -> mainViewModel.logout());

        // 搜尋鈕
        searchBtn.setOnAction(e -> {
            mainViewModel.refresh();
        });

        // 雙向綁定View資料和ViewModel資料
        queryInput.textProperty().bindBidirectional(mainViewModel.queryStringProperty());
        datepicker.valueProperty().bindBidirectional(mainViewModel.queryDateProperty());
        usernameLabel.textProperty().bindBidirectional(mainViewModel.usernameProperty());

        // 教室清單
        mainViewModel.classroomListProperty().addListener((observable, oldValue, classroomList) -> {
            classroomListPane.getChildren().clear();
            Iterator<Classroom> classroomItr = classroomList.iterator();
            while(classroomItr.hasNext()) {
                Classroom classroom = classroomItr.next();
                try {
                    // 取得 classroomCard 佈局元件
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/drawable/classroomCard.fxml"));
                    Parent rootNode = loader.load();
                    Label classroomIdLabel = (Label) rootNode.lookup("#classroomIdLabel");
                    Label classroomTypeLabel = (Label) rootNode.lookup("#classroomTypeLabel");
                    // 將資料設到元件上
                    classroomIdLabel.setText(classroom.getId());
                    classroomTypeLabel.setText(classroom.getType());
                    rootNode.setOnMouseClicked(e -> mainViewModel.selectClassroom(classroom));
                    classroomListPane.getChildren().add(rootNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 綁定 Loading Alert 變數
        mainViewModel.loadingAlertProperty().addListener((observable, oldAlert, newAlert) -> Platform.runLater(() -> newAlert.show()));

        mainViewModel.init();
    }
}
package ui.Main;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import model.Classroom;
import mvvm.View;
import mvvm.ViewModelProviders;
import ui.Dialog.AlertDirector;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MainView implements View {

    @FXML
    private JFXTextField queryInput;    //搜尋欄位

    @FXML
    private DatePicker datepicker;  //日期選擇

    @FXML
    private Label usernameLabel;     //顯示目前帳號

    @FXML
    private JFXMasonryPane classroomListPane;   //教室清單

    private MainViewModel mainViewModel;

    private JFXAlert loadingAlert;

    @Override
    public void initialize() {
        // 指定 ViewModel
        mainViewModel = ViewModelProviders.getInstance().get(MainViewModel.class);

        // 雙向綁定View資料和ViewModel資料
        queryInput.textProperty().bindBidirectional(mainViewModel.queryStringProperty());
        datepicker.valueProperty().bindBidirectional(mainViewModel.queryDateProperty());
        usernameLabel.textProperty().bindBidirectional(mainViewModel.usernameProperty());

        // 綁定 Loading Alert 變數
        mainViewModel.loadingAlertProperty().addListener((observable, oldStatus, newStatus) -> {
            if(newStatus) showLoading();
            else stopLoading();
        });

        // 教室清單
        mainViewModel.classroomListProperty().addListener((observable, oldValue, classroomList) -> refreshClassroomListPane(classroomList));

        // 初始化 Loading Alert（要在主UI線程執行後執行）
        Platform.runLater(() -> {
            IAlertBuilder alertBuilder = new LoadingAlertBuilder();
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            loadingAlert = alertBuilder.getAlert();

            // 初始化ViewModel
            mainViewModel.init();
        });
    }

    //我的預約鈕
    public void toBookingBtnClick() {
        mainViewModel.toMyBooking();
    }

    //登出鈕
    public void logoutBtnClick() {
        mainViewModel.logout();
    }

    //搜尋鈕
    public void searchBtnClick() {
        loadingAlert.show();
        mainViewModel.refresh();
    }

    //重新整理教室列表
    public void refreshClassroomListPane(List<Classroom> classroomList) {
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
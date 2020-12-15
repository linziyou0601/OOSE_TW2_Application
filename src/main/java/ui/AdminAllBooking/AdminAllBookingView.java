package ui.AdminAllBooking;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import model.Booking;
import mvvm.View;
import mvvm.ViewModelProviders;
import ui.Dialog.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class AdminAllBookingView implements View {

    @FXML
    private Label usernameLabel;     //顯示目前帳號

    @FXML
    private JFXButton currentBookingBtn;    //目前時段按鈕
    @FXML
    private JFXButton futureBookingBtn;    //未來時段按鈕
    @FXML
    private JFXButton allBookingBtn;    //顯示全部按鈕
    @FXML
    private JFXMasonryPane bookingListPane;   //預約清單

    private AdminAllBookingViewModel adminAllBookingViewModel;

    private JFXAlert loadingAlert;

    @Override
    public void initialize() {
        // 指定 ViewModel
        adminAllBookingViewModel = ViewModelProviders.getInstance().get(AdminAllBookingViewModel.class);

        // 時段切換鈕
        adminAllBookingViewModel.periodShowTypeProperty().addListener((observable, oldValue, periodShowType) -> {
            currentBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("CURRENT")? "#EAEAEA": "transparent"));
            futureBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("FUTURE")? "#EAEAEA": "transparent"));
            allBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("ALL")? "#EAEAEA": "transparent"));
        });

        // 雙向綁定View資料和ViewModel資料
        usernameLabel.textProperty().bindBidirectional(adminAllBookingViewModel.usernameProperty());

        // 預約清單
        adminAllBookingViewModel.bookingListProperty().addListener((observable, oldValue, bookingList) -> refreshBookingListPane(bookingList));

        // 綁定 Loading Alert 變數
        adminAllBookingViewModel.loadingAlertProperty().addListener((observable, oldStatus, newStatus) -> {
            if(newStatus) showLoading();
            else stopLoading();
        });

        // 綁定 Cancel Valid 變數
        adminAllBookingViewModel.cancelValidProperty().addListener((observable, oldValid, newValid) -> {
            switch((int)newValid){
                case 0:
                    showCancelFailedAlert();
                    break;
                case 1:
                    showCancelSucceedAlert();
                    adminAllBookingViewModel.refresh();
                    break;
            }
            adminAllBookingViewModel.setCancelValid(-1);
        });

        // 初始化 Loading Alert（要在主UI線程執行後執行）
        Platform.runLater(() -> {
            IAlertBuilder alertBuilder = new LoadingAlertBuilder();
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            loadingAlert = alertBuilder.getAlert();

            // 初始化ViewModel
            adminAllBookingViewModel.init();
        });
    }

    //我的預約鈕
    public void toAllClassroomBtnClick() {
        adminAllBookingViewModel.toAllClassroom();
    }

    //登出鈕
    public void logoutBtnClick() {
        adminAllBookingViewModel.logout();
    }

    //目前時段鈕
    public void currentBookingBtnClick() {
        adminAllBookingViewModel.currentPeriod();
        adminAllBookingViewModel.refresh();
    }

    //未來時段鈕
    public void futureBookingBtnClick() {
        adminAllBookingViewModel.futurePeriod();
        adminAllBookingViewModel.refresh();
    }

    //所有時段鈕
    public void allBookingBtnClick() {
        adminAllBookingViewModel.allPeriod();
        adminAllBookingViewModel.refresh();
    }

    //重新整理預約列表
    public void refreshBookingListPane(List<Booking> bookingList) {
        bookingListPane.getChildren().clear();
        Iterator<Booking> bookingItr = bookingList.iterator();
        while(bookingItr.hasNext()) {
            Booking booking = bookingItr.next();
            try {
                // 取得 bookingCard 佈局元件
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/drawable/adminBookingCard.fxml"));
                Parent rootNode = loader.load();
                Label bookingUsernameLabel = (Label) rootNode.lookup("#bookingUsernameLabel");
                Label datetimeLabel = (Label) rootNode.lookup("#datetimeLabel");
                Label classroomIdLabel = (Label) rootNode.lookup("#classroomIdLabel");
                JFXButton operateBtn = (JFXButton) rootNode.lookup("#operateBtn");
                JFXButton cancelBtn = (JFXButton) rootNode.lookup("#cancelBtn");
                // 將資料設到元件上
                bookingUsernameLabel.setText(booking.getUserUsername());
                datetimeLabel.setText(booking.getDate() + ", " + booking.getStartTime() + ":00 - " + (booking.getEndTime() + 1) + ":00");
                classroomIdLabel.setText(booking.getClassroomId());
                operateBtn.setDisable(!booking.isPeriod());
                cancelBtn.setDisable(!booking.isFuture());
                operateBtn.setOnAction(e -> operateBooking(booking.getId()));
                cancelBtn.setOnAction(e -> cancelBooking(booking.getId()));
                bookingListPane.getChildren().add(rootNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //操作預約教室
    public void operateBooking(int id) {
        adminAllBookingViewModel.refresh();
        adminAllBookingViewModel.doOperateBooking(id);
    }

    //取消預約教室
    public void cancelBooking(int id) {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new InputAlertBuilder("確認取消預約", "請輸入管理員密碼", IAlertBuilder.AlertButtonType.OK, true);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<String> result = alert.showAndWait();
        if(result.isPresent()){
            adminAllBookingViewModel.cancelBookingValid(id, result.get());
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
    public void showCancelFailedAlert(){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "失敗", "密碼錯誤", IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        alert.show();
    }

    //顯示成功 Alert
    public void showCancelSucceedAlert() {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.SUCCESS, "成功", "已取消預約", IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        alert.show();
    }
}
package ui.MyBooking;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import model.Booking;
import mvvm.RxJavaCompletableObserver;
import mvvm.View;
import mvvm.ViewManager;
import mvvm.ViewModelProviders;
import ui.BookingDetail.BookingDetailView;
import ui.Dialog.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class MyBookingView implements View {

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

    private MyBookingViewModel myBookingViewModel;

    private JFXAlert loadingAlert;

    @Override
    public void initialize() {
        // 指定 ViewModel
        myBookingViewModel = ViewModelProviders.getInstance().get(MyBookingViewModel.class);

        // 時段切換鈕
        myBookingViewModel.periodShowTypeProperty().addListener((observable, oldValue, periodShowType) -> {
            currentBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("CURRENT")? "#EAEAEA": "transparent"));
            futureBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("FUTURE")? "#EAEAEA": "transparent"));
            allBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("ALL")? "#EAEAEA": "transparent"));
        });

        // 雙向綁定View資料和ViewModel資料
        usernameLabel.textProperty().bindBidirectional(myBookingViewModel.usernameProperty());

        // 預約清單
        myBookingViewModel.bookingListProperty().addListener((observable, oldValue, bookingList) -> refreshBookingListPane(bookingList));

        // 綁定 Loading Alert 變數
        myBookingViewModel.loadingAlertProperty().addListener((observable, oldStatus, newStatus) -> {
            if(newStatus) showLoading();
            else stopLoading();
        });

        // 綁定 Cancel Valid 變數
        myBookingViewModel.cancelValidProperty().addListener((observable, oldValid, newValid) -> {
            switch((int)newValid){
                case 0:
                    showCancelFailedAlert();
                    break;
                case 1:
                    showCancelSucceedAlert();
                    myBookingViewModel.refresh();
                    break;
            }
            myBookingViewModel.setCancelValid(-1);
        });

        // 初始化 Loading Alert（要在主UI線程執行後執行）
        Platform.runLater(() -> {
            IAlertBuilder alertBuilder = new LoadingAlertBuilder();
            AlertDirector alertDirector = new AlertDirector(alertBuilder);
            alertDirector.build();
            loadingAlert = alertBuilder.getAlert();

            // 初始化ViewModel
            myBookingViewModel.init();
        });

    }

    //我的預約鈕
    public void toHomeBtnClick() {
        myBookingViewModel.toHome();
    }

    //登出鈕
    public void logoutBtnClick() {
        myBookingViewModel.logout();
    }

    //目前時段鈕
    public void currentBookingBtnClick() {
        myBookingViewModel.currentPeriod();
        myBookingViewModel.refresh();
    }

    //未來時段鈕
    public void futureBookingBtnClick() {
        myBookingViewModel.futurePeriod();
        myBookingViewModel.refresh();
    }

    //所有時段鈕
    public void allBookingBtnClick() {
        myBookingViewModel.allPeriod();
        myBookingViewModel.refresh();
    }

    //重新整理預約列表
    public void refreshBookingListPane(List<Booking> bookingList) {
        bookingListPane.getChildren().clear();
        Iterator<Booking> bookingItr = bookingList.iterator();
        while(bookingItr.hasNext()) {
            Booking booking = bookingItr.next();
            try {
                // 取得 bookingCard 佈局元件
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/drawable/bookingCard.fxml"));
                Parent rootNode = loader.load();
                Label datetimeLabel = (Label) rootNode.lookup("#datetimeLabel");
                Label classroomIdLabel = (Label) rootNode.lookup("#classroomIdLabel");
                JFXButton operateBtn = (JFXButton) rootNode.lookup("#operateBtn");
                JFXButton cancelBtn = (JFXButton) rootNode.lookup("#cancelBtn");
                // 將資料設到元件上
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
        myBookingViewModel.refresh();
        myBookingViewModel.doOperateBooking(id);
    }

    //取消預約教室
    public void cancelBooking(int id) {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new InputAlertBuilder("確認取消預約", "請輸入使用者密碼", IAlertBuilder.AlertButtonType.OK, true);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<String> result = alert.showAndWait();
        if(result.isPresent()){
            myBookingViewModel.cancelBookingValid(id, result.get());
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
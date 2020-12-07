package ui.MyBooking;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import model.Classroom;
import mvvm.View;
import mvvm.ViewModelProviders;
import model.Booking;
import ui.Dialog.AlertDirector;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MyBookingView implements View {

    @FXML
    private Label usernameLabel;     //顯示目前帳號
    @FXML
    private JFXButton toHomeBtn;    //按鈕前往：總覽
    @FXML
    private JFXButton toBookingBtn; //按鈕前往：預約
    @FXML
    private JFXButton logoutBtn;    //登出按鈕

    @FXML
    private JFXButton currentBookingBtn;    //目前時段按鈕
    @FXML
    private JFXButton futureBookingBtn;    //未來時段按鈕
    @FXML
    private JFXButton allBookingBtn;    //顯示全部按鈕
    @FXML
    private JFXMasonryPane bookingListPane;   //預約清單

    private MyBookingViewModel myBookingViewModel;

    @Override
    public void initialize() {
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
        myBookingViewModel.bookingListProperty().addListener((observable, oldValue, bookingList) -> {
            refreshBookingListPane(bookingList);
        });

        // 綁定 alert 變數
        myBookingViewModel.cancelAlertProperty().addListener((observable, oldAlert, newAlert) -> newAlert.show());

        // 綁定 Loading Alert 變數
        myBookingViewModel.loadingAlertProperty().addListener((observable, oldAlert, newAlert) -> Platform.runLater(() -> newAlert.show()));

        myBookingViewModel.init();
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
    }

    //未來時段鈕
    public void futureBookingBtnClick() {
        myBookingViewModel.futurePeriod();
    }

    //所有時段鈕
    public void allBookingBtnClick() {
        myBookingViewModel.allPeriod();
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
                operateBtn.setOnAction(e -> myBookingViewModel.operateBooking(booking.getId()));
                cancelBtn.setOnAction(e -> myBookingViewModel.cancelBooking(booking.getId()));
                bookingListPane.getChildren().add(rootNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
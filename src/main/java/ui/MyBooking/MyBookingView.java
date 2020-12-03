package ui.MyBooking;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.ViewModelProviders;
import model.Booking;

import java.io.IOException;

public class MyBookingView {

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
    private JFXButton currentBookingBtn;    //目前時段按鈕
    @FXML
    private JFXButton futureBookingBtn;    //未來時段按鈕
    @FXML
    private JFXButton allBookingBtn;    //顯示全部按鈕
    @FXML
    private JFXMasonryPane bookingListPane;   //預約清單

    private MyBookingViewModel myBookingViewModel;

    public void initialize() {
        myBookingViewModel = ViewModelProviders.getInstance().get(MyBookingViewModel.class);

        // 換頁鈕
        toHomeBtn.setOnAction(e -> myBookingViewModel.toHome());
        toBookingBtn.setOnAction(e -> {});
        toSubscribeBtn.setOnAction(e -> {});

        // 登出鈕
        logoutBtn.setOnAction(e -> myBookingViewModel.logout());

        // 時段切換鈕
        currentBookingBtn.setOnAction(e -> myBookingViewModel.currentPeriod());
        futureBookingBtn.setOnAction(e -> myBookingViewModel.futurePeriod());
        allBookingBtn.setOnAction(e -> myBookingViewModel.allPeriod());
        myBookingViewModel.periodShowTypeProperty().addListener((observable, oldValue, periodShowType) -> {
            currentBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("CURRENT")? "#EAEAEA": "transparent"));
            futureBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("CURRENT")? "#EAEAEA": "transparent"));
            allBookingBtn.setStyle("-fx-background-color: " + (periodShowType.equals("ALL")? "#EAEAEA": "transparent"));
            myBookingViewModel.refresh();
        });

        // 雙向View資料和ViewModel資料
        usernameLabel.textProperty().bindBidirectional(myBookingViewModel.usernameProperty());

        // 教室清單
        myBookingViewModel.bookingListProperty().addListener((observable, oldValue, bookingList) -> {
            bookingListPane.getChildren().clear();
            String periodShowType = myBookingViewModel.getPeriodShowType();
            for(Booking booking: bookingList){
                if((periodShowType.equals("CURRENT") && booking.isPeriod()) || (periodShowType.equals("FUTURE") && booking.isFuture()) || periodShowType.equals("ALL")) {
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
                        //rootNode.setOnMouseClicked(e -> myBookingViewModel.(booking.getId()));
                        bookingListPane.getChildren().add(rootNode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        myBookingViewModel.init();
    }
}
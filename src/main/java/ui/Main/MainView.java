package ui.Main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import main.ViewModelProviders;
import model.Classroom;

import java.io.IOException;

public class MainView {

    @FXML
    private JFXButton test_addClassroomBtn; //測試鈕：新增教室物件

    @FXML
    private JFXTextField queryInput;    //搜尋欄位
    @FXML
    private JFXButton searchBtn;    //搜尋按鈕
    @FXML
    private DatePicker datepicker;  //日期選擇

    @FXML
    private Label accountLabel;     //顯示目前帳號
    @FXML
    private JFXButton toHomeBtn;    //按鈕前往：總覽
    @FXML
    private JFXButton toBookingBtn; //按鈕前往：預約
    @FXML
    private JFXButton toSubscribeBtn; //按鈕前往：訂閱
    @FXML
    private JFXButton logoutBtn;    //登出按鈕

    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private JFXMasonryPane classroomListPane;   //教室清單

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }
    @FXML
    public void maximumApplication(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.maximizedProperty().get());
    }
    @FXML
    public void minimumApplication(ActionEvent event) {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private MainViewModel mainViewModel;

    public void initialize() {
        mainViewModel = ViewModelProviders.getInstance().get(MainViewModel.class);
        mainViewModel.init();

        // 測試鈕
        test_addClassroomBtn.setOnAction(e ->  mainViewModel.addClassroom() );

        // 換頁鈕
        toHomeBtn.setOnAction(e -> {});
        toBookingBtn.setOnAction(e -> {});
        toSubscribeBtn.setOnAction(e -> {});

        // 登出鈕
        logoutBtn.setOnAction(e -> mainViewModel.logout());

        // 搜尋鈕
        searchBtn.setOnAction(e -> {
            System.out.println(mainViewModel.queryDateProperty().get());
        });

        // 雙向View資料和ViewModel資料
        queryInput.textProperty().bindBidirectional(mainViewModel.queryStringProperty());
        datepicker.valueProperty().bindBidirectional(mainViewModel.queryDateProperty());
        accountLabel.textProperty().bindBidirectional(mainViewModel.accountProperty());

        // 教室清單
        mainViewModel.classroomListProperty().addListener((observable, oldValue, classroomList) -> {
            classroomListPane.getChildren().clear();
            for(Classroom classroom: classroomList){
                try {
                    FXMLLoader loader = new FXMLLoader();
                    Parent rootNode = loader.load(this.getClass().getResource("/drawable/classroomCard.fxml").openStream());
                    Label classroomIdLabel = (Label) rootNode.lookup("#classroomIdLabel");
                    classroomIdLabel.setText(classroom.getId());
                    classroomListPane.getChildren().add(rootNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
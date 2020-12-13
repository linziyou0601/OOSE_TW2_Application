package main;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mvvm.ViewManager;
import ui.AdminAllBooking.AdminAllBookingView;
import ui.AdminBooking.AdminBookingView;
import ui.AdminBookingDetail.AdminBookingDetailView;
import ui.AdminLogin.AdminLoginView;
import ui.AdminMain.AdminMainView;
import ui.Booking.BookingView;
import ui.BookingDetail.BookingDetailView;
import ui.Login.LoginView;
import ui.Main.MainView;
import ui.MyBooking.MyBookingView;
import ui.Register.RegisterView;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        /* 將所有View放入Map中 */
        ViewManager.addView(RegisterView.class);
        ViewManager.addView(LoginView.class);
        ViewManager.addView(MainView.class);
        ViewManager.addView(BookingView.class);
        ViewManager.addView(MyBookingView.class);
        ViewManager.addView(BookingDetailView.class);
        ViewManager.addView(AdminLoginView.class);
        ViewManager.addView(AdminMainView.class);
        ViewManager.addView(AdminBookingView.class);
        ViewManager.addView(AdminAllBookingView.class);
        ViewManager.addView(AdminBookingDetailView.class);
        /* 設定用啟始頁面的View並顯示畫面 */
        ViewManager.initStage(primaryStage, LoginView.class);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(true);
        primaryStage.setTitle("Login");
        primaryStage.show();

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
    }
}

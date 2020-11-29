package ui.Main;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.MainApplication;
import main.SessionService;
import main.ViewManager;
import main.ViewModel;
import model.User;
import ui.Login.LoginView;

public class MainViewModel implements ViewModel {

    private SessionService sessionService;
    private StringProperty account = new SimpleStringProperty();

    public MainViewModel(SessionService sessionService) {
        this.sessionService = sessionService;
        account.bind(Bindings.createStringBinding(() -> sessionService.getUser().getAccount()));    //account綁定到User的account變數上
    }

    // =============== Getter及Setter ===============
    public StringProperty accountProperty(){ return account; }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登出
    public void logout(){
        sessionService.clear();
        ViewManager.navigateTo(LoginView.class);
    }
}

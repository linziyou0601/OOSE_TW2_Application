package ui.Login;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.SessionContext;
import mvvm.ViewManager;
import mvvm.ViewModel;
import model.User;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Main.MainView;
import ui.Register.RegisterView;

public class LoginViewModel extends ViewModel {

    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<JFXAlert> loginAlert = new SimpleObjectProperty<>();

    public LoginViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty accountProperty(){
        return account;
    }
    public StringProperty passwordProperty(){
        return password;
    }
    public ObjectProperty<JFXAlert> loginAlertProperty(){
        return loginAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：清除所有輸入資料
    public void clearInput() {
        account.set("");
        password.set("");
    }

    // 邏輯處理：前往註冊頁面
    public void signUp() {
        ViewManager.navigateTo(RegisterView.class);
    }

    // 邏輯處理：驗證登入資料
    public void login(){
        User user = dbmgr.getUserByAccount(account.get());
        String prompt = null;

        // 驗證登入資料
        if(user == null) {
            prompt = "帳號錯誤";
        } else if(!user.validate(password.get())) {
            prompt = "密碼錯誤";
        }

        // 執行登入邏輯
        if(prompt!=null) {
            triggerFailedAlert(prompt);
        } else {
            sessionContext.set("userAccount", user.getAccount());
            clearInput();
            ViewManager.navigateTo(MainView.class);
        }
    }

    // 邏輯處理：觸發失敗
    public void triggerFailedAlert(String prompt){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "錯誤", prompt, IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        loginAlert.set(alert);
    }
}

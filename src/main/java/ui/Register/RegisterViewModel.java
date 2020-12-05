package ui.Register;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import javafx.beans.property.*;
import main.ViewModel;
import main.SessionContext;
import main.ViewManager;
import model.User;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Login.LoginView;

import java.util.Optional;

public class RegisterViewModel extends ViewModel {

    private StringProperty username = new SimpleStringProperty();
    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty passwordConfirm = new SimpleStringProperty();
    private ObjectProperty<JFXAlert> registerAlert = new SimpleObjectProperty<>();

    public RegisterViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
    }

    // =============== Getter及Setter ===============
    public StringProperty usernameProperty(){
        return username;
    }
    public StringProperty accountProperty(){
        return account;
    }
    public StringProperty passwordProperty(){ return password; }
    public StringProperty passwordConfirmProperty(){
        return passwordConfirm;
    }
    public ObjectProperty<JFXAlert> registerAlertProperty(){
        return registerAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：清除所有輸入資料
    public void clearInput() {
        username.set("");
        account.set("");
        password.set("");
        passwordConfirm.set("");
    }

    // 邏輯處理：前往登入頁面
    public void signIn() {
        ViewManager.navigateTo(LoginView.class);
    }

    // 邏輯處理：驗證註冊資料
    public void submit(){
        User existUser = dbmgr.getUserByAccount(account.get());
        String prompt = null;

        // 驗證註冊資料
        if(existUser!=null) prompt = "帳號已被使用";
        else if(account.get()==null || account.get().equals("")) prompt = "帳號未輸入";
        else if(password.get()==null || password.get().equals("")) prompt = "密碼未輸入";
        else if(!password.get().equals(passwordConfirm.get())) prompt = "密碼不一致";
        else if(username.get()==null || username.get().equals("")) prompt = "使用者名稱未輸入";

        // 執行註冊邏輯
        if(prompt!=null) {
            triggerFailedAlert(prompt);
        } else {
            String password_hash = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.get().toCharArray());
            dbmgr.saveUser(new User(account.get(), password_hash, username.get()));
            clearInput();
            triggerSucceedAlert();
        }
    }

    // 邏輯處理：觸發失敗
    public void triggerFailedAlert(String prompt){
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.ERROR, "錯誤", prompt, IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        registerAlert.set(alert);
    }

    // 邏輯處理：觸發成功
    public void triggerSucceedAlert() {
        // Builder Pattern：建立BasicAlert
        IAlertBuilder alertBuilder = new BasicAlertBuilder(IAlertBuilder.AlertType.SUCCESS, "成功註冊", "導向登入畫面", IAlertBuilder.AlertButtonType.OK);
        AlertDirector alertDirector = new AlertDirector(alertBuilder);
        alertDirector.build();
        JFXAlert alert = alertBuilder.getAlert();
        // Show and wait for selection
        Optional<Boolean> result = alert.showAndWait();
        if(result.isPresent()){
            ViewManager.navigateTo(LoginView.class);
        }
    }
}

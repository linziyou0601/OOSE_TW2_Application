package ui.AdminLogin;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.*;
import main.SessionContext;
import model.Admin;
import model.User;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import ui.AdminMain.AdminMainView;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;
import ui.Login.LoginView;
import ui.Main.MainView;

public class AdminLoginViewModel extends ViewModel {

    private Admin currentAdmin;
    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private IntegerProperty loginValid = new SimpleIntegerProperty();
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public AdminLoginViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
        loginValid.set(-1);
        loadingAlert.set(false);
    }

    // =============== Getter及Setter ===============
    public StringProperty accountProperty(){
        return account;
    }
    public StringProperty passwordProperty(){
        return password;
    }
    public IntegerProperty loginValidProperty(){
        return loginValid;
    }
    public void setLoginValid(int valid){
        loginValid.set(valid);
    }
    public BooleanProperty loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：清除所有輸入資料
    public void clearInput() {
        account.set("");
        password.set("");
    }

    // 邏輯處理：前往使用者登入頁面
    public void toUserLogin() {
        ViewManager.navigateTo(LoginView.class);
    }

    // 邏輯處理：驗證登入資料
    public void loginValid(){
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
        dbmgr.getAdminByAccount(account.get())
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    @Override
                    public void onNext(Admin result) {
                        currentAdmin = result;
                    }
                    @Override
                    public void onComplete(){
                        // 驗證登入資料
                        loadingAlert.set(false);
                        if(!currentAdmin.validate(password.get())) loginValid.set(0);
                        else loginValid.set(1);
                    }
                    @Override
                    public void onError(Throwable e){
                        //找不到使用者
                        loadingAlert.set(false);
                        loginValid.set(0);
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：執行登入換頁邏輯
    public void doLogin() {
        sessionContext.set("admin", currentAdmin);
        clearInput();
        ViewManager.navigateTo(AdminMainView.class);
    }
}

package ui.Login;

import com.jfoenix.controls.JFXAlert;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.*;
import main.SessionContext;
import model.User;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import org.jetbrains.annotations.NotNull;
import org.reactfx.util.FxTimer;
import ui.AdminLogin.AdminLoginView;
import ui.Dialog.AlertDirector;
import ui.Dialog.BasicAlertBuilder;
import ui.Dialog.IAlertBuilder;
import ui.Dialog.LoadingAlertBuilder;
import ui.Main.MainView;
import ui.Register.RegisterView;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class LoginViewModel extends ViewModel {

    private User currentUser;
    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private IntegerProperty loginValid = new SimpleIntegerProperty();
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public LoginViewModel(DBMgr dbmgr) {
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
    // 邏輯處理：前往管理者登入頁面
    public void toAdminLogin() {
        ViewManager.navigateTo(AdminLoginView.class);
    }

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
    public void loginValid(){
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);

        // =============== 單線程直接跑 [Unit Test 可以通過] ===============
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            currentUser = dbmgr.syncGetUserByAccount(account.get());
        });

        try { future.get(); }
        catch (Exception e) { e.printStackTrace(); }

        if(currentUser!=null){
            // 驗證登入資料
            loadingAlert.set(false);
            if(!currentUser.validate(password.get())) loginValid.set(0);
            else loginValid.set(1);
        } else {
            //找不到使用者
            loadingAlert.set(false);
            loginValid.set(0);
        }

        // =============== 多線程抓資料，Platform.runLater的UI線程更新畫面 [Unit Test 不通過] [原因 runLater是static] ===============
        /*new Thread(() -> {
            currentUser = dbmgr.syncGetUserByAccount(account.get());
            Platform.runLater(() -> {
                if(currentUser!=null){
                    // 驗證登入資料
                    loadingAlert.set(false);
                    if(!currentUser.validate(password.get())) loginValid.set(0);
                    else loginValid.set(1);
                } else {
                    //找不到使用者
                    loadingAlert.set(false);
                    loginValid.set(0);
                }
            });
        }).start();*/

        // =============== RxJava反Observer做法 [Unit Test 有條件通過] [原因 subscriber要另外寫在JUnit裡測模擬資料] ===============
        /*dbmgr.getUserByAccount(account.get())
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<User>() {
                    @Override
                    public void onNext(User result) {
                        currentUser = result;
                    }
                    @Override
                    public void onComplete(){
                        // 驗證登入資料
                        loadingAlert.set(false);
                        if(!currentUser.validate(password.get())) loginValid.set(0);
                        else loginValid.set(1);
                    }
                    @Override
                    public void onError(Throwable e){
                        //找不到使用者
                        loadingAlert.set(false);
                        loginValid.set(0);
                    }
                });*/
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：執行登入換頁邏輯
    public void doLogin() {
        sessionContext.set("user", currentUser);
        clearInput();
        ViewManager.navigateTo(MainView.class);
    }
}

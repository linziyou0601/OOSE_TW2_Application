package ui.Register;

import at.favre.lib.crypto.bcrypt.BCrypt;
import database.DBMgr;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.SessionContext;
import model.User;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import mvvm.ViewManager;
import mvvm.ViewModel;
import ui.Login.LoginView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterViewModel extends ViewModel {

    private StringProperty username = new SimpleStringProperty();
    private StringProperty account = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty passwordConfirm = new SimpleStringProperty();
    private StringProperty registerPrompt = new SimpleStringProperty();
    private BooleanProperty loadingAlert = new SimpleBooleanProperty();

    public RegisterViewModel(DBMgr dbmgr) {
        this.dbmgr = dbmgr;
        this.sessionContext = SessionContext.getInstance();
        loadingAlert.set(false);
    }

    // =============== Getter及Setter ===============
    public StringProperty usernameProperty(){
        return username;
    }
    public StringProperty accountProperty(){
        return account;
    }
    public StringProperty emailProperty(){
        return email;
    }
    public StringProperty passwordProperty(){ return password; }
    public StringProperty passwordConfirmProperty(){
        return passwordConfirm;
    }
    public StringProperty registerPromptProperty(){
        return registerPrompt;
    }
    public void setRegisterPrompt(String prompt){
        registerPrompt.set(prompt);
    }
    public String getRegisterPrompt(){
        return registerPrompt.get();
    }
    public BooleanProperty loadingAlertProperty(){
        return loadingAlert;
    }

    // =============== 邏輯處理 ===============
    // 邏輯處理：清除所有輸入資料
    public void clearInput() {
        username.set("");
        account.set("");
        email.set("");
        password.set("");
        passwordConfirm.set("");
    }

    // 邏輯處理：帳號格式是否正確
    public boolean isAccountValid(String account) {
        return (account!=null && !account.equals(""));
    }

    // 邏輯處理：使用者名稱格式是否正確
    public boolean isUsernameValid(String username) {
        return (username!=null && !username.equals(""));
    }

    // 邏輯處理：信箱格式是否正確
    public boolean isEmailValid(String email) {
        //RCF 5322 Email標準
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("(?:[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return (email!=null && !email.equals("")) && matcher.matches();
    }

    // 邏輯處理：密碼是否有輸入及是否一致
    public boolean isPasswordValid(String password, String passwordConfirm) {
        return (password!=null && !password.equals("")) && password.equals(passwordConfirm);
    }

    // 邏輯處理：驗證註冊資料
    public void registerValid(){
        // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
        loadingAlert.set(true);
        dbmgr.getUserByAccount(account.get())                   //以account異步請求user物件
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行
                .subscribe(new RxJavaObserver<>(){
                    String prompt = null;
                    User existUser;
                    @Override
                    public void onNext(User result) { existUser = result; }         // 當 取得結果時
                    @Override
                    public void onComplete(){                                       // 當 異步請求完成時
                        // User有資料
                        loadingAlert.set(false);
                        registerPrompt.set("帳號已被使用");
                    }
                    @Override
                    public void onError(Throwable e){                               // 當 結果為Null或請求錯誤時
                        // User沒資料
                        // 驗證註冊資料
                        if(!isAccountValid(account.get())) prompt = "帳號格式有誤";
                        else if(!isUsernameValid(username.get())) prompt = "使用者名稱未輸入";
                        else if(!isEmailValid(email.get())) prompt = "電子信箱格式有誤";
                        else if(!isPasswordValid(password.get(), passwordConfirm.get())) prompt = "密碼不一致或或未輸入";

                        // 執行註冊邏輯
                        if(prompt!=null) {
                            loadingAlert.set(false);
                            registerPrompt.set(prompt);
                        } else {
                            String password_hash = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(10, password.get().toCharArray());
                            // ===== ↓ 在新執行緒中執行DB請求 ↓ =====
                            dbmgr.insertUser(new User(account.get(), password_hash, username.get(), email.get()))   //以異步請求insert一個user物件到資料庫
                                    .subscribeOn(Schedulers.newThread())                                            //請求在新執行緒中執行
                                    .observeOn(JavaFxScheduler.platform())                                          //最後在主執行緒中執行
                                    .subscribe(new RxJavaCompletableObserver() {
                                        @Override
                                        public void onComplete() {                                                  // 當 異步請求完成時
                                            loadingAlert.set(false);
                                            registerPrompt.set("成功");
                                        }
                                    });
                            // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
                        }
                    }
                });
        // ===== ↑ 在新執行緒中執行DB請求 ↑ =====
    }

    // 邏輯處理：執行註冊換頁邏輯
    public void doRegister() {
        clearInput();
        ViewManager.navigateTo(LoginView.class);
    }

    // 邏輯處理：前往登入頁面
    public void signIn() {
        ViewManager.navigateTo(LoginView.class);
    }
}

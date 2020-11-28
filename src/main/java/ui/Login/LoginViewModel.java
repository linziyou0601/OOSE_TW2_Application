package ui.Login;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;

public class LoginViewModel {

    private StringProperty account = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private HashMap<String, String> userData = new HashMap<>();

    public LoginViewModel() {
        userData.put("A001", "pwd123");
        userData.put("A002", "pwd123");
        userData.put("A003", "pwd123");
    }

    // accountInput & passwordInput
    public StringProperty accountProperty(){
        return account;
    }
    public String getAccount() { return account.get(); }
    public StringProperty passwordProperty(){
        return password;
    }
    public String getPassword() { return password.get(); }
    public void clearInput() {
        account.set("");
        password.set("");
    }

    // 邏輯處理：驗證使用者登入資料
    public Observable<String> login(){
        Observable observable = Observable.create((ObservableOnSubscribe<String>) subscriber -> {
            String userPwd = userData.get(getAccount());
            if(userPwd == null) {
                subscriber.onError(new Exception("帳號錯誤"));
            } else{
                if(!userPwd.equals(getPassword())){
                    subscriber.onError(new Exception("密碼錯誤"));
                } else {
                    subscriber.onNext(getAccount());
                    subscriber.onComplete();
                }
            }
        });
        return observable;
    }
}

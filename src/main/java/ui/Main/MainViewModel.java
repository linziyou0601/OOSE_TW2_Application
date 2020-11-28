package ui.Main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.ViewModel;

public class MainViewModel implements ViewModel {

    private StringProperty account = new SimpleStringProperty();

    // Account
    public StringProperty accountProperty(){
        return account;
    }
    public String getAccount(){
        return account.get();
    }
    public void setAccount(String text){
        account.set(text);
    }
}

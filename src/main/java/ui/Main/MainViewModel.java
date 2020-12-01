package ui.Main;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import main.SessionService;
import main.ViewManager;
import main.IViewModel;
import model.Classroom;
import ui.Login.LoginView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel implements IViewModel {

    private SessionService sessionService;
    private StringProperty queryString = new SimpleStringProperty();
    private ObjectProperty<LocalDate> queryDate = new SimpleObjectProperty<>();
    private StringProperty account = new SimpleStringProperty();
    private ListProperty<Classroom> classroomList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private int count = 0;

    public MainViewModel(SessionService sessionService) {
        this.sessionService = sessionService;
        account.bind(Bindings.createStringBinding(() -> sessionService.getUser().getAccount()));    //account綁定到User的account變數上
    }

    // =============== Getter及Setter ===============
    public StringProperty queryStringProperty(){ return queryString; }
    public ObjectProperty<LocalDate> queryDateProperty(){ return queryDate; }
    public StringProperty accountProperty(){ return account; }
    public ListProperty<Classroom> classroomListProperty(){ return classroomList; }
    public List<Classroom> getClassroomList(){ return classroomList.get(); }

    // =============== 邏輯處理 ===============
    // 邏輯處理：登出
    public void logout(){
        sessionService.clear();
        ViewManager.navigateTo(LoginView.class);
    }

    public void addClassroom(){
        classroomList.add(new Classroom("教室代碼: " + String.valueOf(count++)));
    }
}

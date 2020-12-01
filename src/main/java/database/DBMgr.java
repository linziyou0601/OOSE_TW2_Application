package database;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class DBMgr {
    private List<User> userStorage = new ArrayList<>();

    public DBMgr() {

    }

    public void insertUser(User user) {
        userStorage.add(user);
    }

    public List<User> getUsers() {
        return userStorage;
    }

    public User getUserByAccount(String account) {
        for(User user: userStorage){
            if(user.getAccount().equals(account))
                return user;
        }
        return null;
    }
}

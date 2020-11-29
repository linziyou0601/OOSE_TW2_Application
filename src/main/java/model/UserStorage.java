package model;

import java.util.HashMap;

public class UserStorage {
    private HashMap<String, User> userHashMap = new HashMap<>();

    public void add(User user) {
        userHashMap.put(user.getAccount(), user);
    }

    public User find(String account) {
        return userHashMap.get(account);
    }
}

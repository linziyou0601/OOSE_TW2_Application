package main;

import model.User;

import java.util.HashMap;

public class SessionContext {

    private static final SessionContext sessionContext = new SessionContext();
    private HashMap<String, Object> session = new HashMap<>();
    private User user;

    private SessionContext(){}

    public static SessionContext getInstance(){
        return sessionContext;
    }

    public void set(String key, Object value) {
        session.put(key, value);
    }

    public void unset(String key) {
        session.remove(key);
    }

    public <T> T get(String key) {
        return (T) session.get(key);
    }

    public void clear() {
        session = new HashMap<>();
    }
}

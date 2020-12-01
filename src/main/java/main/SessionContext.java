package main;

import model.User;

import java.util.HashMap;

public class SessionContext {

    private static final SessionContext sessionContext = new SessionContext();
    private HashMap<String, Object> session = new HashMap<>();
    private User user;

    private SessionContext(){}

    public static SessionContext getSession(){
        return sessionContext;
    }

    public void set(String key, Object value) {
        session.put(key, value);
    }

    public <T> T get(String key) {
        return (T) session.get(key);
    }

    public void unset() {
        session = new HashMap<>();
    }
}
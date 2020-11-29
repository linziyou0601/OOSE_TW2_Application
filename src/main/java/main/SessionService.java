package main;

import model.User;

public class SessionService {
    private User user;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public void clear() {
        user = null;
    }
}

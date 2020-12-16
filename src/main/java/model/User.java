package model;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class User {
    private String account;
    private String password;
    private String username;
    private String email;
    private int point;
    private String position;

    public User() {}

    public User(String account, String password, String username, String email) {
        this.account = account;
        this.password = password;
        this.username = username;
        this.email = email;
        this.point = 100;
        if(account.substring(0, 1).equals("B") || account.substring(0, 1).equals("A") || account.substring(0, 1).equals("M")) this.position = "Student";
        else this.position = "Teacher";
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getPoint() {
        return point;
    }

    public String getPosition() {
        return position;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    // 待資料庫補齊需要改
    public boolean validate(String password) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), this.password);
        return result.verified;
    }
}

package model;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Admin {
    private String account;
    private String password;
    private String username;

    public Admin() {}

    public Admin(String account, String password, String username) {
        this.account = account;
        this.password = password;
        this.username = username;
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

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // 待資料庫補齊需要改
    public boolean validate(String password) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), this.password);
        return result.verified;
    }
}

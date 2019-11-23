package com.moon.ailatrieuphu.model;

import java.io.Serializable;

public class User implements Serializable {
    private int idUser;
    private String email;
    private String nickname;
    private String password;
    private boolean adminRole;

    public User() {
    }

    public User(int idUser, String email, String nickname, String password, boolean adminRole) {
        this.idUser = idUser;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.adminRole = adminRole;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdminRole() {
        return adminRole;
    }

    public void setAdminRole(boolean adminRole) {
        this.adminRole = adminRole;
    }
}

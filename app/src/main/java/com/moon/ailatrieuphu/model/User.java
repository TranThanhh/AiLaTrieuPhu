package com.moon.ailatrieuphu.model;

import java.io.Serializable;

public class User implements Serializable {
    private int idUser;
    private String email;
    private String nickname;
    private String password;
    private String createTime;
    private String updateTime;
    private int diemCao;
    private int roleLevel;
    private boolean deleted;

    public User() {
    }

    public User(int idUser, String email, String nickname, String password, String createTime, String updateTime, int diemCao, int roleLevel, boolean deleted) {
        this.idUser = idUser;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.diemCao = diemCao;
        this.roleLevel = roleLevel;
        this.deleted = deleted;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getDiemCao() {
        return diemCao;
    }

    public void setDiemCao(int diemCao) {
        this.diemCao = diemCao;
    }

    public int getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(int roleLevel) {
        this.roleLevel = roleLevel;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

package com.moon.ailatrieuphu.model;

import java.io.Serializable;

public class Diem implements Serializable {
    private int idUser;
    private int diemCao;

    public Diem() {
    }

    public Diem(int idUser, int diemCao) {
        this.idUser = idUser;
        this.diemCao = diemCao;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getDiemCao() {
        return diemCao;
    }

    public void setDiemCao(int diemCao) {
        this.diemCao = diemCao;
    }
}

package com.moon.ailatrieuphu.model;

import java.io.Serializable;

public class CauHoi implements Serializable {
    private int idCauHoi;
    private String noiDung;
    private String cauA;
    private String cauB;
    private String cauC;
    private String cauD;
    private int idLoaiCH;
    private int idUser;
    private String createdTime;
    private String updateTime;
    private String dapAnDung;

    public CauHoi() {
    }

    public CauHoi(int idCauHoi, String noiDung, String cauA, String cauB, String cauC, String cauD, int idLoaiCH) {
        this.idCauHoi = idCauHoi;
        this.noiDung = noiDung;
        this.cauA = cauA;
        this.cauB = cauB;
        this.cauC = cauC;
        this.cauD = cauD;
        this.idLoaiCH = idLoaiCH;
    }

    public CauHoi(int idCauHoi, String noiDung, String cauA, String cauB, String cauC, String cauD, int idLoaiCH, int idUser, String createdTime, String updateTime) {
        this.idCauHoi = idCauHoi;
        this.noiDung = noiDung;
        this.cauA = cauA;
        this.cauB = cauB;
        this.cauC = cauC;
        this.cauD = cauD;
        this.idLoaiCH = idLoaiCH;
        this.idUser = idUser;
        this.createdTime = createdTime;
        this.updateTime = updateTime;
    }

    public CauHoi(int idCauHoi, String noiDung, String cauA, String cauB, String cauC, String cauD, int idLoaiCH, int idUser, String createdTime, String updateTime, String dapAnDung) {
        this.idCauHoi = idCauHoi;
        this.noiDung = noiDung;
        this.cauA = cauA;
        this.cauB = cauB;
        this.cauC = cauC;
        this.cauD = cauD;
        this.idLoaiCH = idLoaiCH;
        this.idUser = idUser;
        this.createdTime = createdTime;
        this.updateTime = updateTime;
        this.dapAnDung = dapAnDung;
    }

    public int getIdCauHoi() {
        return idCauHoi;
    }

    public void setIdCauHoi(int idCauHoi) {
        this.idCauHoi = idCauHoi;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getCauA() {
        return cauA;
    }

    public void setCauA(String cauA) {
        this.cauA = cauA;
    }

    public String getCauB() {
        return cauB;
    }

    public void setCauB(String cauB) {
        this.cauB = cauB;
    }

    public String getCauC() {
        return cauC;
    }

    public void setCauC(String cauC) {
        this.cauC = cauC;
    }

    public String getCauD() {
        return cauD;
    }

    public void setCauD(String cauD) {
        this.cauD = cauD;
    }

    public int getIdLoaiCH() {
        return idLoaiCH;
    }

    public void setIdLoaiCH(int idLoaiCH) {
        this.idLoaiCH = idLoaiCH;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDapAnDung() {
        return dapAnDung;
    }

    public void setDapAnDung(String dapAnDung) {
        this.dapAnDung = dapAnDung;
    }
}

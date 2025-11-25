/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package code;
import java.sql.Timestamp;

/**
 *
 * @author DHAIFAN
 */

public class TransactionOut {
    private int idOut;
    private int idItem;
    private int jumlah;
    private String keterangan;
    private Timestamp tanggal;
    private int idUser;

    public TransactionOut() {}

    public TransactionOut(int idOut, int idItem, int jumlah, String keterangan, Timestamp tanggal, int idUser) {
        this.idOut = idOut;
        this.idItem = idItem;
        this.jumlah = jumlah;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
        this.idUser = idUser;
    }

    public int getIdOut() {
        return idOut;
    }

    public void setIdOut(int idOut) {
        this.idOut = idOut;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }

    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
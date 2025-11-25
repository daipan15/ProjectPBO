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

public class Item {
    private int idItem;
    private String namaItem;
    private Integer idKategori;  // bisa null
    private int harga;
    private int stok;
    private String satuan;
    private Timestamp updatedAt;

    public Item() {}

    public Item(int idItem, String namaItem, Integer idKategori, int harga, int stok, String satuan, Timestamp updatedAt) {
        this.idItem = idItem;
        this.namaItem = namaItem;
        this.idKategori = idKategori;
        this.harga = harga;
        this.stok = stok;
        this.satuan = satuan;
        this.updatedAt = updatedAt;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getNamaItem() {
        return namaItem;
    }

    public void setNamaItem(String namaItem) {
        this.namaItem = namaItem;
    }

    public Integer getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(Integer idKategori) {
        this.idKategori = idKategori;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
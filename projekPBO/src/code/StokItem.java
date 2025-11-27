/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package code;

/**
 *
 * @author DHAIFAN
 */
public class StokItem {
    private int idItem;
    private String namaItem;
    private int stok;

    public StokItem(int idItem, String namaItem, int stok) {
        this.idItem = idItem;
        this.namaItem = namaItem;
        this.stok = stok;
    }

    public int getIdItem() { return idItem; }
    public String getNamaItem() { return namaItem; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    @Override
    public String toString() {
        return namaItem; // biar ComboBox tampil nama_item
    }
}
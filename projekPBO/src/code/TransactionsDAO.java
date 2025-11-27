/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package code;

/**
 *
 * @author DHAIFAN
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class TransactionsDAO {

    private Connection conn;

    public TransactionsDAO(Connection conn) {
        this.conn = conn;
    }

    // Simpan transaksi masuk
    public boolean insertTransactionIn(TransactionIn tIn) {
        String sql = "INSERT INTO transactions_in (id_item, jumlah, keterangan, tanggal, id_user) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tIn.getIdItem());
            ps.setInt(2, tIn.getJumlah());
            ps.setString(3, tIn.getKeterangan());
            ps.setTimestamp(4, tIn.getTanggal());
            ps.setInt(5, tIn.getIdUser());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Simpan transaksi keluar
    public boolean insertTransactionOut(TransactionOut tOut) {
        String sql = "INSERT INTO transactions_out (id_item, jumlah, keterangan, tanggal, id_user) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tOut.getIdItem());
            ps.setInt(2, tOut.getJumlah());
            ps.setString(3, tOut.getKeterangan()); // bisa "Hapus stok"
            ps.setTimestamp(4, tOut.getTanggal());
            ps.setInt(5, tOut.getIdUser());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


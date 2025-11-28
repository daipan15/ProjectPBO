/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package code;

/**
 *
 * @author DHAIFAN
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StokDAO {
    private Connection conn;

    public StokDAO(Connection conn) {
        this.conn = conn;
    }

    // Ambil semua item untuk ComboBox
    public List<StokItem> getAllItems() {
        List<StokItem> list = new ArrayList<>();
        String sql = "SELECT id_item, nama_item, stok, harga, satuan FROM items";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new StokItem(
                    rs.getInt("id_item"),
                    rs.getString("nama_item"),
                    rs.getInt("stok"),
                    rs.getInt("harga"),
                    rs.getString("satuan")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Update stok
    public boolean updateStok(int idItem, int stokBaru) {
        String sql = "UPDATE items SET stok=? WHERE id_item=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stokBaru);
            ps.setInt(2, idItem);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
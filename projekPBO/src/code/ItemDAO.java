/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package code;

/**
 *
 * @author DHAIFAN
 */
import java.util.*;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ItemDAO {

    private Connection conn;

    public ItemDAO(Connection conn) {
        this.conn = conn;
    }
    
    public boolean insertItem(Item item) {
        String sql = "INSERT INTO items (nama_item, id_kategori, harga, stok, satuan) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getNamaItem());

            if (item.getIdKategori() != null) {
                ps.setInt(2, item.getIdKategori());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setInt(3, item.getHarga());
            ps.setInt(4, item.getStok());
            ps.setString(5, item.getSatuan());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Object[]> searchItems(String keyword) {
        List<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT i.id_item, i.nama_item, c.nama_kategori, i.stok 
            FROM items i
            LEFT JOIN categories c ON i.id_kategori = c.id_kategori
            WHERE i.nama_item LIKE ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            var rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_item"),
                    rs.getString("nama_item"),
                    rs.getString("nama_kategori"),
                    rs.getInt("stok")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}

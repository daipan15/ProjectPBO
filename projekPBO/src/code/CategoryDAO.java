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

public class CategoryDAO {

    private Connection conn;

    public CategoryDAO(Connection conn) {
        this.conn = conn;
    }

    public List<CategoryItem> getAllCategories() {
        List<CategoryItem> list = new ArrayList<>();

        String sql = "SELECT id_kategori, nama_kategori FROM categories";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new CategoryItem(
                    rs.getInt("id_kategori"),
                    rs.getString("nama_kategori")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public boolean insert(String name) throws SQLException {
        String sql = "INSERT INTO categories(nama_kategori) VALUES (?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        return ps.executeUpdate() > 0;
    }

    public boolean update(int id, String name) throws SQLException {
        String sql = "UPDATE categories SET nama_kategori=? WHERE id_kategori=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setInt(2, id);
        return ps.executeUpdate() > 0;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id_kategori=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    }
    
    public boolean deleteData(int id) throws SQLException {
        String sql = "DELETE FROM items WHERE id_item=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    }
}

package controller;

import model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MenuController {

    public List<String> getCategories() {
        List<String> cats = new ArrayList<>();
        String sql = "SELECT category_name FROM categories ORDER BY category_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cats.add(rs.getString("category_name"));
        } catch (SQLException ex) { ex.printStackTrace(); }
        return cats;
    }

    public List<MenuItem> getMenuByCategory(String categoryName) {
        List<MenuItem> out = new ArrayList<>();
        String sql = "SELECT mi.*, c.category_name FROM menu_items mi JOIN categories c ON mi.category_id=c.category_id WHERE c.category_name=? ORDER BY mi.name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem m = new MenuItem();
                    m.setItemId(rs.getInt("item_id"));
                    m.setName(rs.getString("name"));
                    m.setCategoryId(rs.getInt("category_id"));
                    m.setCategoryName(rs.getString("category_name"));
                    m.setPrice(rs.getDouble("price"));
                    m.setAvailableQty(rs.getInt("available_qty"));
                    m.setDescription(rs.getString("description"));
                    out.add(m);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return out;
    }

    public List<MenuItem> getAllMenu() {
        List<MenuItem> out = new ArrayList<>();
        String sql = "SELECT mi.*, c.category_name FROM menu_items mi JOIN categories c ON mi.category_id=c.category_id ORDER BY c.category_name, mi.name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MenuItem m = new MenuItem();
                m.setItemId(rs.getInt("item_id"));
                m.setName(rs.getString("name"));
                m.setCategoryId(rs.getInt("category_id"));
                m.setCategoryName(rs.getString("category_name"));
                m.setPrice(rs.getDouble("price"));
                m.setAvailableQty(rs.getInt("available_qty"));
                m.setDescription(rs.getString("description"));
                out.add(m);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return out;
    }

    public MenuItem findById(int itemId) {
        String sql = "SELECT mi.*, c.category_name FROM menu_items mi JOIN categories c ON mi.category_id=c.category_id WHERE mi.item_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MenuItem m = new MenuItem();
                    m.setItemId(rs.getInt("item_id"));
                    m.setName(rs.getString("name"));
                    m.setCategoryId(rs.getInt("category_id"));
                    m.setCategoryName(rs.getString("category_name"));
                    m.setPrice(rs.getDouble("price"));
                    m.setAvailableQty(rs.getInt("available_qty"));
                    m.setDescription(rs.getString("description"));
                    return m;
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    public boolean updateStock(int itemId, int newQty) {
        String sql = "UPDATE menu_items SET available_qty = ? WHERE item_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, itemId);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }
}

package controller;

import model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InventoryController {
    private final MenuController menuController = new MenuController();

    public List<MenuItem> getInventory() {
        return menuController.getAllMenu();
    }

    public boolean restock(int itemId, int addQty) {
        MenuItem m = menuController.findById(itemId);
        if (m == null) return false;
        int prev = m.getAvailableQty();
        int newQty = prev + addQty;

        
        boolean ok = menuController.updateStock(itemId, newQty);
        if (!ok) return false;

       
        String sql = "INSERT INTO inventory_logs (item_id, previous_qty, new_qty) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.setInt(2, prev);
            ps.setInt(3, newQty);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }

        return true;
    }
}

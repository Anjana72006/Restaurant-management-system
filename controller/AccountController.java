package controller;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Account / Financial summaries.
 */
public class AccountController {

    /**
     * Quick summary: revenue today and total orders today.
     * Returns map with keys "revenue" (Double) and "orders" (Integer).
     */
    public Map<String, Object> getTodaySummary() {
        Map<String, Object> out = new HashMap<>();
        String sql = "SELECT IFNULL(SUM(net_amount),0) as revenue, COUNT(*) as orders FROM orders WHERE DATE(order_date) = CURDATE()";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                out.put("revenue", rs.getDouble("revenue"));
                out.put("orders", rs.getInt("orders"));
            } else {
                out.put("revenue", 0.0);
                out.put("orders", 0);
            }
        } catch (SQLException ex) { ex.printStackTrace(); out.put("revenue", 0.0); out.put("orders", 0); }
        return out;
    }
}

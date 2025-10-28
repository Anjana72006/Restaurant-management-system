package controller;

import model.MenuItem;
import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class OrderController {
    private final List<OrderItem> cart = new ArrayList<>();
    private final MenuController menuController = new MenuController();

    public void addToCart(int itemId, int qty) throws Exception {
        MenuItem item = menuController.findById(itemId);
        if (item == null) throw new Exception("Item not found.");
        if (qty <= 0) throw new Exception("Quantity must be > 0.");
        if (item.getAvailableQty() < qty) throw new Exception("Insufficient stock. Available: " + item.getAvailableQty());
        // If same item exists in cart, increase
        for (OrderItem oi : cart) {
            if (oi.getItemId() == itemId) {
                oi.setQuantity(oi.getQuantity() + qty);
                return;
            }
        }
        cart.add(new OrderItem(itemId, item.getName(), qty, item.getPrice()));
    }

    public List<OrderItem> getCart() { return cart; }

    public double getSubtotal() {
        return cart.stream().mapToDouble(OrderItem::getTotal).sum();
    }

    
    public int placeOrder(double discountAmount) throws Exception {
        if (cart.isEmpty()) throw new Exception("Cart is empty.");
        String insertOrder = "INSERT INTO orders (total_amount, discount, net_amount) VALUES (?, ?, ?)";
        String insertItem = "INSERT INTO order_items (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE menu_items SET available_qty = available_qty - ? WHERE item_id = ? AND available_qty >= ?";

        double subtotal = getSubtotal();
        double net = Math.max(0, subtotal - discountAmount);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psOrder = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psItem = conn.prepareStatement(insertItem);
                 PreparedStatement psUpdateStock = conn.prepareStatement(updateStock)) {

                psOrder.setDouble(1, subtotal);
                psOrder.setDouble(2, discountAmount);
                psOrder.setDouble(3, net);
                psOrder.executeUpdate();
                int orderId;
                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    if (keys.next()) orderId = keys.getInt(1);
                    else throw new SQLException("Failed to create order.");
                }

                for (OrderItem oi : cart) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, oi.getItemId());
                    psItem.setInt(3, oi.getQuantity());
                    psItem.setDouble(4, oi.getPrice());
                    psItem.addBatch();

                    psUpdateStock.setInt(1, oi.getQuantity());
                    psUpdateStock.setInt(2, oi.getItemId());
                    psUpdateStock.setInt(3, oi.getQuantity());
                    psUpdateStock.addBatch();
                }
                psItem.executeBatch();
                int[] stockRes = psUpdateStock.executeBatch();
                for (int r : stockRes) {
                    if (r == 0) throw new SQLException("Stock update failed (insufficient stock). Transaction rolled back.");
                }

                conn.commit();
                cart.clear();
                return orderId;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("DB error: " + ex.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    public void clearCart() { cart.clear(); }
}

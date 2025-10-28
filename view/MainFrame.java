package view;

import controller.AccountController;
import controller.InventoryController;
import controller.MenuController;
import controller.OrderController;
import model.MenuItem;
import model.OrderItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * MainFrame - complete UI with colored backgrounds and modern fonts.
 * All panels are inner classes to keep a single-file view implementation.
 */
public class MainFrame extends JFrame {
    private final MenuController menuController = new MenuController();
    private final OrderController orderController = new OrderController();
    private final InventoryController inventoryController = new InventoryController();
    private final AccountController accountController = new AccountController();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // panels
    private WelcomePanel welcomePanel;
    private AdminPanel adminPanel;
    private AccountsPanel accountsPanel;
    private InventoryPanel inventoryPanel;
    private OrdersPanel ordersPanel;
    private CustomerMenuPanel customerMenuPanel;
    private CategoryPanel categoryPanel;
    private CartPanel cartPanel;
    private BillPanel billPanel;
    private ThankYouPanel thankYouPanel;

    public MainFrame() {
        setTitle("🍽️ Restaurant Management");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // create panels
        welcomePanel = new WelcomePanel();
        adminPanel = new AdminPanel();
        accountsPanel = new AccountsPanel();
        inventoryPanel = new InventoryPanel();
        ordersPanel = new OrdersPanel();
        customerMenuPanel = new CustomerMenuPanel();
        categoryPanel = new CategoryPanel();
        cartPanel = new CartPanel();
        billPanel = new BillPanel();
        thankYouPanel = new ThankYouPanel();

        // add to cards
        cards.add(welcomePanel, "WELCOME!");
        cards.add(adminPanel, "admin");
        cards.add(accountsPanel, "accounts");
        cards.add(inventoryPanel, "inventory");
        cards.add(ordersPanel, "orders");
        cards.add(customerMenuPanel, "customer");
        cards.add(categoryPanel, "category");
        cards.add(cartPanel, "cart");
        cards.add(billPanel, "bill");
        cards.add(thankYouPanel, "thankyou");

        add(cards);
        showCard("WELCOME!");
    }

    public void showCard(String name) {
        if ("inventory".equals(name)) inventoryPanel.refresh();
        if ("orders".equals(name)) ordersPanel.refresh();
        if ("accounts".equals(name)) accountsPanel.refresh();
        if ("category".equals(name)) categoryPanel.refresh();
        if ("cart".equals(name)) cartPanel.refresh();
        cardLayout.show(cards, name);
    }

    /* ----------------- Shared UI helpers ----------------- */
    private static void styleButton(JButton b) {
        b.setBackground(new Color(25, 118, 210));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(10, 0, 20, 0));
        return lbl;
    }

    /* ----------------- Panels ----------------- */

    // Welcome
    class WelcomePanel extends JPanel {
        public WelcomePanel() {
            setLayout(new BorderLayout());
            // gradient background via custom painting
            setBackground(new Color(48, 63, 159));

            JLabel title = titleLabel("✨ Welcome! ✨");
            add(title, BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new GridLayout(2, 1, 0, 10));

            JLabel msg = new JLabel(
                    "<html><div style='text-align:center;font-size:16px;color:white;'>"
                            + "Choose your role to get started.<br>Admin can manage menu, orders & accounts.<br>"
                            + "Customers can view menu & place orders.</div></html>", SwingConstants.CENTER);
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            center.add(msg);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
            btnPanel.setOpaque(false);
            JButton admin = new JButton("Admin");
            JButton customer = new JButton("Customer");
            styleButton(admin);
            styleButton(customer);
            admin.setBackground(new Color(244, 81, 30));
            customer.setBackground(new Color(0, 150, 136));
            btnPanel.add(admin);
            btnPanel.add(customer);
            center.add(btnPanel);

            add(center, BorderLayout.CENTER);

            admin.addActionListener(e -> showCard("admin"));
            customer.addActionListener(e -> showCard("customer"));
        }

        // optional paint for subtle gradient card effect (not necessary)
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // no custom paint needed here because we set background color
        }
    }

    // Admin Dashboard
    class AdminPanel extends JPanel {
        public AdminPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(33, 150, 243));
            add(titleLabel("👩‍💼 Admin Dashboard"), BorderLayout.NORTH);

            JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 80));
            center.setOpaque(false);

            JButton accounts = new JButton("Accounts");
            JButton inventory = new JButton("Inventory");
            JButton orders = new JButton("Orders");
            styleButton(accounts);
            styleButton(inventory);
            styleButton(orders);

            accounts.setPreferredSize(new Dimension(200, 70));
            inventory.setPreferredSize(new Dimension(200, 70));
            orders.setPreferredSize(new Dimension(200, 70));

            center.add(accounts);
            center.add(inventory);
            center.add(orders);
            add(center, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottom.setOpaque(false);
            JButton back = new JButton("← Back");
            styleButton(back);
            back.setBackground(new Color(96, 125, 139));
            back.addActionListener(e -> showCard("welcome"));
            bottom.add(back);
            add(bottom, BorderLayout.SOUTH);

            accounts.addActionListener(e -> showCard("accounts"));
            inventory.addActionListener(e -> showCard("inventory"));
            orders.addActionListener(e -> showCard("orders"));
        }
    }

    // Accounts panel
    class AccountsPanel extends JPanel {
        private final JLabel revenueLbl = new JLabel("Revenue: ₹0.00", SwingConstants.CENTER);
        private final JLabel ordersLbl = new JLabel("Orders today: 0", SwingConstants.CENTER);

        public AccountsPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(0, 121, 107));

            add(titleLabel("📊 Accounts & Financial Summary"), BorderLayout.NORTH);
            revenueLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            revenueLbl.setForeground(Color.WHITE);
            ordersLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            ordersLbl.setForeground(Color.WHITE);

            JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
            center.setOpaque(false);
            center.add(revenueLbl);
            center.add(ordersLbl);
            add(center, BorderLayout.CENTER);

            JButton back = new JButton("← Back");
            styleButton(back);
            back.setBackground(new Color(96, 125, 139));
            back.addActionListener(e -> showCard("admin"));

            JPanel bottom = new JPanel();
            bottom.setOpaque(false);
            bottom.add(back);
            add(bottom, BorderLayout.SOUTH);
        }

        public void refresh() {
            Map<String, Object> s = accountController.getTodaySummary();
            double revenue = (double) s.getOrDefault("revenue", 0.0);
            int orders = (int) s.getOrDefault("orders", 0);
            revenueLbl.setText(String.format("Revenue today: ₹%.2f", revenue));
            ordersLbl.setText("Orders today: " + orders);
        }
    }

    // Inventory panel
    class InventoryPanel extends JPanel {
        private final DefaultTableModel model = new DefaultTableModel(new String[]{"Item ID", "Name", "Category", "Price", "Available"}, 0);
        private final JTable table = new JTable(model);

        public InventoryPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setBackground(new Color(46, 125, 50)); // green

            JLabel header = titleLabel("📦 Inventory Management");
            add(header, BorderLayout.NORTH);

            table.setRowHeight(26);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottom.setOpaque(false);
            JButton restock = new JButton("Restock Selected");
            JButton back = new JButton("← Back");
            styleButton(restock);
            styleButton(back);
            restock.setBackground(new Color(244, 67, 54));
            back.setBackground(new Color(96, 125, 139));
            bottom.add(restock);
            bottom.add(back);
            add(bottom, BorderLayout.SOUTH);

            restock.addActionListener(e -> {
                int r = table.getSelectedRow();
                if (r == -1) {
                    JOptionPane.showMessageDialog(this, "Select an item to restock.");
                    return;
                }
                int itemId = (int) model.getValueAt(r, 0);
                String qtyS = JOptionPane.showInputDialog(this, "Enter qty to add:", "10");
                try {
                    int q = Integer.parseInt(qtyS);
                    boolean ok = inventoryController.restock(itemId, q);
                    if (ok) {
                        JOptionPane.showMessageDialog(this, "Restocked successfully.");
                        refresh();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to restock (check DB).");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number.");
                }
            });

            back.addActionListener(e -> showCard("admin"));
            refresh();
        }

        public void refresh() {
            model.setRowCount(0);
            List<MenuItem> all = menuController.getAllMenu();
            for (MenuItem m : all) {
                model.addRow(new Object[]{m.getItemId(), m.getName(), m.getCategoryName(), String.format("₹%.2f", m.getPrice()), m.getAvailableQty()});
            }
        }
    }

    // Orders (admin) panel
    class OrdersPanel extends JPanel {
        private final DefaultTableModel model = new DefaultTableModel(new String[]{"Order ID", "Total", "Discount", "Net"}, 0);
        private final JTable table = new JTable(model);

        public OrdersPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(255, 167, 38)); // amber
            add(titleLabel("🧾 Today's Orders"), BorderLayout.NORTH);

            table.setRowHeight(26);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottom.setOpaque(false);
            JButton back = new JButton("← Back");
            styleButton(back);
            back.setBackground(new Color(96, 125, 139));
            bottom.add(back);
            add(bottom, BorderLayout.SOUTH);

            back.addActionListener(e -> showCard("admin"));
            refresh();
        }

        public void refresh() {
            model.setRowCount(0);
            String sql = "SELECT order_id, total_amount, discount, net_amount FROM orders WHERE DATE(order_date)=CURDATE() ORDER BY order_date DESC";
            try (Connection conn = controller.DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("order_id"),
                            String.format("₹%.2f", rs.getDouble("total_amount")),
                            String.format("₹%.2f", rs.getDouble("discount")),
                            String.format("₹%.2f", rs.getDouble("net_amount"))
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load orders: " + ex.getMessage());
            }
        }
    }

    // Customer menu (category selection)
    class CustomerMenuPanel extends JPanel {
        public CustomerMenuPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(103, 58, 183)); // purple
            add(titleLabel("🍽️ Menu Categories"), BorderLayout.NORTH);

            JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
            center.setOpaque(false);

            List<String> categories = menuController.getCategories();
            if (categories.isEmpty()) {
                center.add(new JLabel("No categories found (DB empty)."));
            } else {
                for (String cat : categories) {
                    JButton b = new JButton(cat);
                    styleButton(b);
                    b.setPreferredSize(new Dimension(160, 60));
                    b.addActionListener(e -> {
                        categoryPanel.setCategory(cat);
                        showCard("category");
                    });
                    center.add(b);
                }
            }

            add(new JScrollPane(center), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottom.setOpaque(false);
            JButton back = new JButton("← Back");
            styleButton(back);
            back.setBackground(new Color(96, 125, 139));
            back.addActionListener(e -> showCard("welcome"));
            bottom.add(back);
            add(bottom, BorderLayout.SOUTH);
        }
    }

    // Category items listing
    class CategoryPanel extends JPanel {
        private final DefaultTableModel model = new DefaultTableModel(new String[]{"ItemID", "Name", "Price", "Available", "Qty to add"}, 0);
        private final JTable table = new JTable(model);
        private String currentCategory;

        public CategoryPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(3, 169, 244)); // light blue
            add(titleLabel("📚 Category Items"), BorderLayout.NORTH);

            table.setRowHeight(26);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.setOpaque(false);
            JButton addToCart = new JButton("Add Selected to Cart");
            JButton viewCart = new JButton("View Cart");
            JButton back = new JButton("← Back");
            styleButton(addToCart);
            styleButton(viewCart);
            styleButton(back);
            addToCart.setBackground(new Color(76, 175, 80));
            viewCart.setBackground(new Color(244, 67, 54));
            back.setBackground(new Color(96, 125, 139));

            bottom.add(back);
            bottom.add(viewCart);
            bottom.add(addToCart);
            add(bottom, BorderLayout.SOUTH);

            back.addActionListener(e -> showCard("customer"));
            viewCart.addActionListener(e -> {
                cartPanel.refresh();
                showCard("cart");
            });

            addToCart.addActionListener(e -> {
                int r = table.getSelectedRow();
                if (r == -1) {
                    JOptionPane.showMessageDialog(this, "Select an item.");
                    return;
                }
                int itemId = (int) model.getValueAt(r, 0);
                int qty = 1;
                try {
                    Object v = model.getValueAt(r, 4);
                    qty = Integer.parseInt(String.valueOf(v));
                } catch (Exception ex) {
                    qty = 1;
                }
                try {
                    orderController.addToCart(itemId, qty);
                    JOptionPane.showMessageDialog(this, "Added to cart.");
                    refresh();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            });
        }

        public void setCategory(String cat) {
            this.currentCategory = cat;
            refresh();
        }

        public void refresh() {
            model.setRowCount(0);
            if (currentCategory == null) return;
            List<MenuItem> list = menuController.getMenuByCategory(currentCategory);
            for (MenuItem m : list) {
                model.addRow(new Object[]{m.getItemId(), m.getName(), String.format("₹%.2f", m.getPrice()), m.getAvailableQty(), 1});
            }
        }
    }

    // Cart panel
    class CartPanel extends JPanel {
        private final DefaultTableModel model = new DefaultTableModel(new String[]{"Name", "Qty", "Price", "Total"}, 0);
        private final JTextField discountField = new JTextField("0", 6);
        private final JLabel subtotalLabel = new JLabel("₹0.00");

        public CartPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(121, 85, 72)); // brown
            add(titleLabel("🛒 Cart"), BorderLayout.NORTH);

            JTable t = new JTable(model);
            t.setRowHeight(26);
            add(new JScrollPane(t), BorderLayout.CENTER);

            JPanel right = new JPanel();
            right.setOpaque(false);
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel subLbl = new JLabel("Subtotal:");
            subLbl.setForeground(Color.WHITE);
            subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            subtotalLabel.setForeground(Color.WHITE);

            right.add(subLbl);
            right.add(subtotalLabel);
            right.add(Box.createVerticalStrut(10));

            JPanel disc = new JPanel(new FlowLayout(FlowLayout.LEFT));
            disc.setOpaque(false);
            disc.add(new JLabel("Discount (₹): "));
            disc.add(discountField);
            right.add(disc);
            right.add(Box.createVerticalStrut(12));

            JButton generate = new JButton("Generate Bill");
            styleButton(generate);
            generate.setBackground(new Color(0, 188, 212));
            generate.addActionListener(e -> {
                double discVal = 0;
                try {
                    discVal = Double.parseDouble(discountField.getText());
                } catch (Exception ex) {
                    discVal = 0;
                }
                try {
                    int orderId = orderController.placeOrder(discVal);
                    JOptionPane.showMessageDialog(this, "Order placed (ID: " + orderId + ")");
                    billPanel.renderFromOrder(orderId);
                    showCard("bill");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            });

            JButton back = new JButton("← Back");
            styleButton(back);
            back.setBackground(new Color(96, 125, 139));
            back.addActionListener(e -> showCard("customer"));

            right.add(generate);
            right.add(Box.createVerticalStrut(8));
            right.add(back);

            add(right, BorderLayout.EAST);
            refresh();
        }

        public void refresh() {
            model.setRowCount(0);
            for (OrderItem oi : orderController.getCart()) {
                model.addRow(new Object[]{oi.getName(), oi.getQuantity(), String.format("₹%.2f", oi.getPrice()), String.format("₹%.2f", oi.getTotal())});
            }
            subtotalLabel.setText(String.format("₹%.2f", orderController.getSubtotal()));
        }
    }

    // Bill panel
    class BillPanel extends JPanel {
        private final JTextArea area = new JTextArea();

        public BillPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(96, 125, 139));
            add(titleLabel("🧾 Bill"), BorderLayout.NORTH);

            area.setFont(new Font("Monospaced", Font.PLAIN, 14));
            area.setEditable(false);
            add(new JScrollPane(area), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.setOpaque(false);
            JButton done = new JButton("Done");
            styleButton(done);
            done.setBackground(new Color(233, 30, 99));
            done.addActionListener(e -> showCard("thankyou"));
            bottom.add(done);
            add(bottom, BorderLayout.SOUTH);
        }

        public void renderFromOrder(int orderId) {
            StringBuilder sb = new StringBuilder();
            sb.append("The Palate — Bill\n\n");
            try (Connection conn = controller.DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders WHERE order_id = ?");
                 PreparedStatement psItems = conn.prepareStatement("SELECT oi.*, mi.name FROM order_items oi JOIN menu_items mi ON oi.item_id = mi.item_id WHERE oi.order_id = ?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        sb.append("Order ID: ").append(orderId).append("\n");
                        sb.append("Date: ").append(rs.getTimestamp("order_date")).append("\n\n");
                        psItems.setInt(1, orderId);
                        try (ResultSet r2 = psItems.executeQuery()) {
                            sb.append(String.format("%-30s %5s %10s\n", "Item", "Qty", "Total"));
                            sb.append("-----------------------------------------------\n");
                            double subtotal = 0;
                            while (r2.next()) {
                                String iname = r2.getString("name");
                                int q = r2.getInt("quantity");
                                double price = r2.getDouble("price");
                                sb.append(String.format("%-30s %5d %10.2f\n", iname, q, price * q));
                                subtotal += price * q;
                            }
                            sb.append("\nSubtotal: ₹").append(String.format("%.2f", subtotal)).append("\n");
                            sb.append("Discount: ₹").append(String.format("%.2f", rs.getDouble("discount"))).append("\n");
                            sb.append("Net: ₹").append(String.format("%.2f", rs.getDouble("net_amount"))).append("\n");
                        }
                    } else {
                        sb.append("Order not found.\n");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                sb.append("\nError fetching order.\n");
            }
            area.setText(sb.toString());
        }
    }

    // Thank you
    class ThankYouPanel extends JPanel {
        public ThankYouPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(255, 204, 128));
            JLabel t = new JLabel("🎉 Thank You for Visiting The Palate!", SwingConstants.CENTER);
            t.setFont(new Font("Segoe UI", Font.BOLD, 28));
            t.setForeground(new Color(85, 65, 0));
            add(t, BorderLayout.CENTER);

            JButton home = new JButton("Back to Home");
            styleButton(home);
            home.setBackground(new Color(233, 30, 99));
            home.addActionListener(e -> showCard("welcome"));

            JPanel p = new JPanel();
            p.setOpaque(false);
            p.add(home);
            add(p, BorderLayout.SOUTH);
        }
    }
}

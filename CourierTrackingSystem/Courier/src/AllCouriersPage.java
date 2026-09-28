import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AllCouriersPage extends JFrame {

    static final Color ORANGE = new Color(255, 102, 0);
    static final Color DARK_GRAY = new Color(50, 50, 50);
    static final Color WHITE = Color.WHITE;

    JTable table;
    DefaultTableModel model;
    JTextField searchField;
    JComboBox<String> statusFilter;
    String loggedInUser;
    int userId;

    public AllCouriersPage(String username, int userId) {
        this.loggedInUser = username;
        this.userId = userId;

        setTitle("SwiftCourier - All Couriers");
        setSize(1100, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(WHITE);

        // Header
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(1100, 55));
        header.setBackground(DARK_GRAY);

        JLabel title = new JLabel("📋 All Couriers");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(WHITE);
        title.setBounds(15, 13, 250, 30);
        header.add(title);

        JButton homeBtn = new JButton("🏠 Home");
        homeBtn.setBounds(980, 12, 90, 30);
        homeBtn.setBackground(ORANGE);
        homeBtn.setForeground(WHITE);
        homeBtn.setFocusPainted(false); homeBtn.setBorderPainted(false);
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.add(homeBtn);

        panel.add(header, BorderLayout.NORTH);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setBackground(new Color(245, 245, 245));
        filterBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        searchField = new JTextField(18);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        filterBar.add(new JLabel("Search:"));
        filterBar.add(searchField);

        statusFilter = new JComboBox<>(new String[]{"All Status", "Booked", "Picked Up", "In Transit", "Out for Delivery", "Delivered", "Cancelled"});
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        filterBar.add(new JLabel("Status:"));
        filterBar.add(statusFilter);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(ORANGE);
        searchBtn.setForeground(WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.setFocusPainted(false); searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterBar.add(searchBtn);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(DARK_GRAY);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.setFocusPainted(false); refreshBtn.setBorderPainted(false);
        filterBar.add(refreshBtn);

        panel.add(filterBar, BorderLayout.CENTER);

        // Table
        String[] cols = {"Tracking No", "Sender", "Receiver", "From", "To", "Weight", "Type", "Priority", "Price", "Status", "Date"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setBackground(ORANGE);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionBackground(new Color(255, 230, 200));
        table.setSelectionForeground(DARK_GRAY);

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.SOUTH);

        add(panel);
        loadData("", "All Status");

        searchBtn.addActionListener(e -> loadData(searchField.getText().trim(), statusFilter.getSelectedItem().toString()));
        refreshBtn.addActionListener(e -> loadData("", "All Status"));
        homeBtn.addActionListener(e -> { new HomePage(loggedInUser, userId); dispose(); });

        // Double click - show receipt
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String trackNo = model.getValueAt(row, 0).toString();
                        showReceipt(trackNo);
                    }
                }
            }
        });

        setVisible(true);
    }

    void loadData(String search, String status) {
        model.setRowCount(0);
        try {
            String query = "SELECT * FROM couriers WHERE 1=1 ";
            if (!search.isEmpty()) query += "AND (tracking_no LIKE '%" + search + "%' OR sender_name LIKE '%" + search + "%' OR receiver_name LIKE '%" + search + "%') ";
            if (!status.equals("All Status")) query += "AND status='" + status + "' ";
            query += "ORDER BY id DESC";

            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(query);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("tracking_no"),
                    rs.getString("sender_name"),
                    rs.getString("receiver_name"),
                    rs.getString("sender_address").length() > 20 ? rs.getString("sender_address").substring(0, 20) + "..." : rs.getString("sender_address"),
                    rs.getString("receiver_address").length() > 20 ? rs.getString("receiver_address").substring(0, 20) + "..." : rs.getString("receiver_address"),
                    rs.getDouble("weight") + " kg",
                    rs.getString("type"),
                    rs.getString("priority"),
                    "₹" + rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    void showReceipt(String trackNo) {
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT * FROM couriers WHERE tracking_no=?");
            ps.setString(1, trackNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this,
                    "═══════════════════════════════\n" +
                    "       SWIFTCOURIER RECEIPT      \n" +
                    "═══════════════════════════════\n" +
                    "Tracking No : " + trackNo + "\n" +
                    "───────────────────────────────\n" +
                    "SENDER\n" +
                    "Name    : " + rs.getString("sender_name") + "\n" +
                    "Phone   : " + rs.getString("sender_phone") + "\n" +
                    "Address : " + rs.getString("sender_address") + "\n" +
                    "───────────────────────────────\n" +
                    "RECEIVER\n" +
                    "Name    : " + rs.getString("receiver_name") + "\n" +
                    "Phone   : " + rs.getString("receiver_phone") + "\n" +
                    "Address : " + rs.getString("receiver_address") + "\n" +
                    "───────────────────────────────\n" +
                    "Weight  : " + rs.getDouble("weight") + " kg\n" +
                    "Type    : " + rs.getString("type") + "\n" +
                    "Priority: " + rs.getString("priority") + "\n" +
                    "Status  : " + rs.getString("status") + "\n" +
                    "───────────────────────────────\n" +
                    "TOTAL   : ₹" + rs.getDouble("price") + "\n" +
                    "═══════════════════════════════\n" +
                    "  Thank you for using SwiftCourier!",
                    "Receipt - " + trackNo, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}

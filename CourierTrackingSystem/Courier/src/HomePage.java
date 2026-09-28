import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class HomePage extends JFrame {

    static final Color ORANGE = new Color(255, 102, 0);
    static final Color DARK_GRAY = new Color(50, 50, 50);
    static final Color WHITE = Color.WHITE;
    static final Color LIGHT_BG = new Color(248, 248, 248);

    String loggedInUser;
    int userId;

    public HomePage(String username, int userId) {
        this.loggedInUser = username;
        this.userId = userId;

        setTitle("SwiftCourier - Dashboard");
        setSize(750, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(LIGHT_BG);

        // Top Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 750, 65);
        header.setBackground(DARK_GRAY);

        JLabel logo = new JLabel("🚚 SwiftCourier");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setForeground(WHITE);
        logo.setBounds(20, 12, 250, 35);
        header.add(logo);

        JLabel welcomeLabel = new JLabel("👤 " + username);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(200, 200, 200));
        welcomeLabel.setBounds(540, 22, 180, 22);
        header.add(welcomeLabel);

        panel.add(header);

        // Orange stats bar
        JPanel statsBar = new JPanel(new GridLayout(1, 4, 2, 0));
        statsBar.setBounds(0, 65, 750, 80);
        statsBar.setBackground(ORANGE);

        statsBar.add(createStat("📦 Total", getCount("couriers")));
        statsBar.add(createStat("🚚 In Transit", getStatusCount("In Transit")));
        statsBar.add(createStat("✅ Delivered", getStatusCount("Delivered")));
        statsBar.add(createStat("⏳ Pending", getStatusCount("Booked")));

        panel.add(statsBar);

        // Section title
        JLabel menuTitle = new JLabel("Quick Actions");
        menuTitle.setFont(new Font("Arial", Font.BOLD, 16));
        menuTitle.setForeground(DARK_GRAY);
        menuTitle.setBounds(20, 160, 200, 28);
        panel.add(menuTitle);

        // Menu Buttons — 2 rows
        JButton newCourierBtn = createMenuBtn("📦 New Courier", ORANGE);
        newCourierBtn.setBounds(20, 200, 220, 70);
        panel.add(newCourierBtn);

        JButton trackBtn = createMenuBtn("🔍 Track Courier", new Color(30, 30, 80));
        trackBtn.setBounds(260, 200, 220, 70);
        panel.add(trackBtn);

        JButton allCourierBtn = createMenuBtn("📋 All Couriers", new Color(20, 120, 60));
        allCourierBtn.setBounds(500, 200, 220, 70);
        panel.add(allCourierBtn);

        JButton updateBtn = createMenuBtn("🔄 Update Status", new Color(100, 60, 180));
        updateBtn.setBounds(20, 290, 220, 70);
        panel.add(updateBtn);

        JButton historyBtn = createMenuBtn("📜 Delivery History", new Color(180, 80, 20));
        historyBtn.setBounds(260, 290, 220, 70);
        panel.add(historyBtn);

        JButton receiptBtn = createMenuBtn("🧾 Print Receipt", new Color(0, 120, 160));
        receiptBtn.setBounds(500, 290, 220, 70);
        panel.add(receiptBtn);

        // Logout
        JButton logoutBtn = new JButton("🔒 Logout");
        logoutBtn.setBounds(20, 390, 150, 40);
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 13));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(logoutBtn);

        // Bottom bar
        JPanel bottomBar = new JPanel(null);
        bottomBar.setBounds(0, 510, 750, 20);
        bottomBar.setBackground(ORANGE);
        panel.add(bottomBar);

        add(panel);

        newCourierBtn.addActionListener(e -> { new AddCourierPage(loggedInUser, userId); dispose(); });
        trackBtn.addActionListener(e -> { new TrackPage(loggedInUser, userId); dispose(); });
        allCourierBtn.addActionListener(e -> { new AllCouriersPage(loggedInUser, userId); dispose(); });
        updateBtn.addActionListener(e -> { new UpdateStatusPage(loggedInUser, userId); dispose(); });
        historyBtn.addActionListener(e -> { new AllCouriersPage(loggedInUser, userId); dispose(); });
        receiptBtn.addActionListener(e -> { new AllCouriersPage(loggedInUser, userId); dispose(); });
        logoutBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == 0) { new LoginPage(); dispose(); }
        });

        setVisible(true);
    }

    JPanel createStat(String label, int count) {
        JPanel card = new JPanel(null);
        card.setBackground(ORANGE);
        card.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(255, 130, 30)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(WHITE);
        lbl.setBounds(10, 8, 150, 20);
        card.add(lbl);

        JLabel val = new JLabel(String.valueOf(count));
        val.setFont(new Font("Arial", Font.BOLD, 30));
        val.setForeground(WHITE);
        val.setBounds(10, 28, 150, 40);
        card.add(val);

        return card;
    }

    JButton createMenuBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        return btn;
    }

    int getCount(String table) {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM " + table);
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    int getStatusCount(String status) {
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT COUNT(*) FROM couriers WHERE status=?");
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}

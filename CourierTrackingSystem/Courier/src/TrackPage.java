import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TrackPage extends JFrame {

    static final Color ORANGE = new Color(255, 102, 0);
    static final Color DARK_GRAY = new Color(50, 50, 50);
    static final Color WHITE = Color.WHITE;
    static final Color LIGHT_BG = new Color(248, 248, 248);

    JTextField trackingField;
    JPanel resultPanel;
    String loggedInUser;
    int userId;

    public TrackPage(String username, int userId) {
        this.loggedInUser = username;
        this.userId = userId;

        setTitle("SwiftCourier - Track Courier");
        setSize(650, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(LIGHT_BG);

        // Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 650, 55);
        header.setBackground(DARK_GRAY);

        JLabel title = new JLabel("🔍 Track Your Courier");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(WHITE);
        title.setBounds(15, 13, 300, 30);
        header.add(title);

        JButton homeBtn = new JButton("🏠 Home");
        homeBtn.setBounds(540, 12, 90, 30);
        homeBtn.setBackground(ORANGE);
        homeBtn.setForeground(WHITE);
        homeBtn.setFocusPainted(false); homeBtn.setBorderPainted(false);
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.add(homeBtn);

        panel.add(header);

        // Search Box
        JPanel searchBox = new JPanel(null);
        searchBox.setBounds(15, 70, 610, 70);
        searchBox.setBackground(WHITE);
        searchBox.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel trackLabel = new JLabel("Enter Tracking Number:");
        trackLabel.setFont(new Font("Arial", Font.BOLD, 13));
        trackLabel.setForeground(DARK_GRAY);
        trackLabel.setBounds(15, 8, 200, 22);
        searchBox.add(trackLabel);

        trackingField = new JTextField();
        trackingField.setBounds(15, 32, 430, 28);
        trackingField.setFont(new Font("Arial", Font.PLAIN, 14));
        trackingField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        searchBox.add(trackingField);

        JButton trackBtn = new JButton("TRACK →");
        trackBtn.setBounds(460, 32, 130, 28);
        trackBtn.setBackground(ORANGE);
        trackBtn.setForeground(WHITE);
        trackBtn.setFont(new Font("Arial", Font.BOLD, 13));
        trackBtn.setFocusPainted(false); trackBtn.setBorderPainted(false);
        trackBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBox.add(trackBtn);

        panel.add(searchBox);

        // Result Panel
        resultPanel = new JPanel(null);
        resultPanel.setBounds(15, 155, 610, 400);
        resultPanel.setBackground(WHITE);
        resultPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(resultPanel);

        showPlaceholder();

        add(panel);

        trackBtn.addActionListener(e -> trackCourier());
        trackingField.addActionListener(e -> trackCourier());
        homeBtn.addActionListener(e -> { new HomePage(loggedInUser, userId); dispose(); });

        setVisible(true);
    }

    void showPlaceholder() {
        resultPanel.removeAll();
        JLabel placeholder = new JLabel("Enter tracking number above to see courier status");
        placeholder.setFont(new Font("Arial", Font.ITALIC, 14));
        placeholder.setForeground(new Color(180, 180, 180));
        placeholder.setBounds(80, 180, 460, 30);
        resultPanel.add(placeholder);
        resultPanel.repaint();
    }

    void trackCourier() {
        String trackNo = trackingField.getText().trim().toUpperCase();
        if (trackNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter tracking number!"); return;
        }

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT * FROM couriers WHERE tracking_no=?");
            ps.setString(1, trackNo);
            ResultSet rs = ps.executeQuery();

            resultPanel.removeAll();

            if (!rs.next()) {
                JLabel notFound = new JLabel("❌ Tracking number not found: " + trackNo);
                notFound.setFont(new Font("Arial", Font.BOLD, 14));
                notFound.setForeground(new Color(200, 50, 50));
                notFound.setBounds(50, 180, 500, 30);
                resultPanel.add(notFound);
                resultPanel.repaint();
                return;
            }

            // Courier Details
            String status = rs.getString("status");
            Color statusColor = getStatusColor(status);

            // Status Badge
            JPanel statusBadge = new JPanel(null);
            statusBadge.setBounds(15, 15, 580, 60);
            statusBadge.setBackground(statusColor);

            JLabel statusLabel = new JLabel("📦 " + status.toUpperCase());
            statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
            statusLabel.setForeground(WHITE);
            statusLabel.setBounds(15, 8, 350, 30);
            statusBadge.add(statusLabel);

            JLabel trackNoLabel = new JLabel("Tracking: " + trackNo);
            trackNoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            trackNoLabel.setForeground(new Color(230, 230, 230));
            trackNoLabel.setBounds(15, 38, 300, 18);
            statusBadge.add(trackNoLabel);

            resultPanel.add(statusBadge);

            // Details
            int y = 90;
            addDetailRow(resultPanel, "👤 Sender:", rs.getString("sender_name") + " | " + rs.getString("sender_phone"), y); y += 35;
            addDetailRow(resultPanel, "📍 From:", rs.getString("sender_address"), y); y += 35;
            addDetailRow(resultPanel, "👤 Receiver:", rs.getString("receiver_name") + " | " + rs.getString("receiver_phone"), y); y += 35;
            addDetailRow(resultPanel, "📍 To:", rs.getString("receiver_address"), y); y += 35;
            addDetailRow(resultPanel, "⚖️ Weight:", rs.getDouble("weight") + " kg | " + rs.getString("type"), y); y += 35;
            addDetailRow(resultPanel, "⚡ Priority:", rs.getString("priority"), y); y += 35;
            addDetailRow(resultPanel, "💰 Price:", "₹" + rs.getDouble("price"), y);

            // Timeline
            y = 330;
            JLabel timelineTitle = new JLabel("📜 Tracking History:");
            timelineTitle.setFont(new Font("Arial", Font.BOLD, 13));
            timelineTitle.setForeground(DARK_GRAY);
            timelineTitle.setBounds(15, y, 200, 22);
            resultPanel.add(timelineTitle);

            PreparedStatement logPs = DBConnection.getConnection().prepareStatement(
                "SELECT * FROM tracking_logs WHERE tracking_no=? ORDER BY created_at DESC");
            logPs.setString(1, trackNo);
            ResultSet logRs = logPs.executeQuery();

            y += 28;
            while (logRs.next() && y < 390) {
                JLabel logEntry = new JLabel("• " + logRs.getString("status") +
                    " - " + logRs.getString("location") + " (" + logRs.getTimestamp("created_at") + ")");
                logEntry.setFont(new Font("Arial", Font.PLAIN, 11));
                logEntry.setForeground(new Color(80, 80, 80));
                logEntry.setBounds(15, y, 580, 18);
                resultPanel.add(logEntry);
                y += 20;
            }

            resultPanel.repaint();
            resultPanel.revalidate();

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    void addDetailRow(JPanel p, String label, String value, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(DARK_GRAY);
        lbl.setBounds(15, y, 130, 22);
        p.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 12));
        val.setForeground(new Color(80, 80, 80));
        val.setBounds(150, y, 440, 22);
        p.add(val);
    }

    Color getStatusColor(String status) {
        switch (status) {
            case "Booked": return new Color(100, 100, 200);
            case "Picked Up": return new Color(200, 130, 0);
            case "In Transit": return ORANGE;
            case "Out for Delivery": return new Color(0, 150, 100);
            case "Delivered": return new Color(30, 160, 30);
            case "Cancelled": return new Color(180, 50, 50);
            default: return DARK_GRAY;
        }
    }
}

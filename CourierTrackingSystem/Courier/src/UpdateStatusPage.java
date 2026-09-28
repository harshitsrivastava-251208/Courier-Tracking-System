import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UpdateStatusPage extends JFrame {

    static final Color ORANGE = new Color(255, 102, 0);
    static final Color DARK_GRAY = new Color(50, 50, 50);
    static final Color WHITE = Color.WHITE;

    JTable table;
    DefaultTableModel model;
    JTextField trackingField, locationField, noteField;
    JComboBox<String> statusBox;
    JButton updateBtn, homeBtn;
    String selectedTrackNo = "";
    String loggedInUser;
    int userId;

    public UpdateStatusPage(String username, int userId) {
        this.loggedInUser = username;
        this.userId = userId;

        setTitle("SwiftCourier - Update Status");
        setSize(1050, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(248, 248, 248));

        // Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1050, 55);
        header.setBackground(DARK_GRAY);

        JLabel title = new JLabel("🔄 Update Courier Status");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(WHITE);
        title.setBounds(15, 13, 300, 30);
        header.add(title);

        homeBtn = new JButton("🏠 Home");
        homeBtn.setBounds(930, 12, 90, 30);
        homeBtn.setBackground(ORANGE);
        homeBtn.setForeground(WHITE);
        homeBtn.setFocusPainted(false); homeBtn.setBorderPainted(false);
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.add(homeBtn);

        panel.add(header);

        // Update Form
        JPanel form = new JPanel(null);
        form.setBounds(10, 65, 330, 490);
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel formTitle = new JLabel("UPDATE STATUS");
        formTitle.setFont(new Font("Arial", Font.BOLD, 14));
        formTitle.setForeground(ORANGE);
        formTitle.setBounds(0, 0, 200, 25);
        form.add(formTitle);

        addLabel(form, "Tracking Number:", 0, 35);
        trackingField = new JTextField();
        trackingField.setBounds(0, 55, 295, 35);
        trackingField.setFont(new Font("Arial", Font.PLAIN, 13));
        trackingField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        form.add(trackingField);

        addLabel(form, "New Status:", 0, 105);
        statusBox = new JComboBox<>(new String[]{
            "Booked", "Picked Up", "In Transit",
            "Out for Delivery", "Delivered", "Cancelled"
        });
        statusBox.setBounds(0, 125, 295, 35);
        statusBox.setFont(new Font("Arial", Font.PLAIN, 13));
        form.add(statusBox);

        addLabel(form, "Current Location:", 0, 175);
        locationField = new JTextField();
        locationField.setBounds(0, 195, 295, 35);
        locationField.setFont(new Font("Arial", Font.PLAIN, 13));
        locationField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        form.add(locationField);

        addLabel(form, "Note:", 0, 245);
        noteField = new JTextField();
        noteField.setBounds(0, 265, 295, 35);
        noteField.setFont(new Font("Arial", Font.PLAIN, 13));
        noteField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        form.add(noteField);

        // Status Timeline Guide
        JLabel guideTitle = new JLabel("Status Flow:");
        guideTitle.setFont(new Font("Arial", Font.BOLD, 12));
        guideTitle.setForeground(DARK_GRAY);
        guideTitle.setBounds(0, 320, 200, 22);
        form.add(guideTitle);

        String[] steps = {"📦 Booked", "→ Picked Up", "→ In Transit", "→ Out for Delivery", "→ Delivered"};
        int sy = 345;
        for (String step : steps) {
            JLabel stepLabel = new JLabel(step);
            stepLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            stepLabel.setForeground(new Color(100, 100, 100));
            stepLabel.setBounds(0, sy, 295, 18);
            form.add(stepLabel);
            sy += 20;
        }

        updateBtn = new JButton("UPDATE STATUS →");
        updateBtn.setBounds(0, 440, 295, 42);
        updateBtn.setBackground(ORANGE);
        updateBtn.setForeground(WHITE);
        updateBtn.setFont(new Font("Arial", Font.BOLD, 14));
        updateBtn.setFocusPainted(false); updateBtn.setBorderPainted(false);
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        form.add(updateBtn);

        panel.add(form);

        // Table
        model = new DefaultTableModel(new String[]{"Tracking No", "Sender", "Receiver", "Status", "Priority", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setBackground(ORANGE);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionBackground(new Color(255, 230, 200));
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(350, 65, 685, 490);
        panel.add(scroll);

        add(panel);
        loadData();

        // Click on table - auto fill tracking number
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedTrackNo = model.getValueAt(row, 0).toString();
                    trackingField.setText(selectedTrackNo);
                    String currentStatus = model.getValueAt(row, 3).toString();
                    statusBox.setSelectedItem(getNextStatus(currentStatus));
                }
            }
        });

        updateBtn.addActionListener(e -> updateStatus());
        homeBtn.addActionListener(e -> { new HomePage(loggedInUser, userId); dispose(); });

        setVisible(true);
    }

    String getNextStatus(String current) {
        switch (current) {
            case "Booked": return "Picked Up";
            case "Picked Up": return "In Transit";
            case "In Transit": return "Out for Delivery";
            case "Out for Delivery": return "Delivered";
            default: return current;
        }
    }

    void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(DARK_GRAY);
        l.setBounds(x, y, 250, 20);
        p.add(l);
    }

    void updateStatus() {
        String trackNo = trackingField.getText().trim().toUpperCase();
        String status = statusBox.getSelectedItem().toString();
        String location = locationField.getText().trim();
        String note = noteField.getText().trim();

        if (trackNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter tracking number!"); return;
        }
        if (location.isEmpty()) location = "Processing Center";

        try {
            // Update courier status
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE couriers SET status=? WHERE tracking_no=?");
            ps.setString(1, status); ps.setString(2, trackNo);
            int updated = ps.executeUpdate();

            if (updated == 0) {
                JOptionPane.showMessageDialog(this, "Tracking number not found!"); return;
            }

            // Add tracking log
            PreparedStatement log = DBConnection.getConnection().prepareStatement(
                "INSERT INTO tracking_logs (tracking_no, status, location, note) VALUES (?,?,?,?)");
            log.setString(1, trackNo); log.setString(2, status);
            log.setString(3, location); log.setString(4, note);
            log.executeUpdate();

            Color statusColor = status.equals("Delivered") ? new Color(30, 160, 30) : ORANGE;
            JOptionPane.showMessageDialog(this,
                "✅ Status Updated!\n\nTracking: " + trackNo + "\nNew Status: " + status,
                "Updated", JOptionPane.INFORMATION_MESSAGE);

            trackingField.setText("");
            locationField.setText("");
            noteField.setText("");
            loadData();

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    void loadData() {
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT tracking_no, sender_name, receiver_name, status, priority, created_at FROM couriers WHERE status != 'Delivered' ORDER BY id DESC");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("tracking_no"), rs.getString("sender_name"),
                    rs.getString("receiver_name"), rs.getString("status"),
                    rs.getString("priority"), rs.getTimestamp("created_at")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}

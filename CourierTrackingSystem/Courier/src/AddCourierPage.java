import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;

public class AddCourierPage extends JFrame {

    static final Color ORANGE = new Color(255, 102, 0);
    static final Color DARK_GRAY = new Color(50, 50, 50);
    static final Color WHITE = Color.WHITE;

    JTextField senderNameField, senderPhoneField, senderAddressField;
    JTextField receiverNameField, receiverPhoneField, receiverAddressField;
    JTextField weightField, descField;
    JComboBox<String> typeBox, priorityBox;
    JButton saveBtn, cancelBtn;
    String loggedInUser;
    int userId;

    public AddCourierPage(String username, int userId) {
        this.loggedInUser = username;
        this.userId = userId;

        setTitle("SwiftCourier - New Courier");
        setSize(620, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(WHITE);

        // Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 620, 55);
        header.setBackground(ORANGE);

        JLabel title = new JLabel("📦 New Courier Booking");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(WHITE);
        title.setBounds(15, 13, 300, 30);
        header.add(title);

        JButton homeBtn = new JButton("🏠 Home");
        homeBtn.setBounds(510, 12, 90, 30);
        homeBtn.setBackground(DARK_GRAY);
        homeBtn.setForeground(WHITE);
        homeBtn.setFocusPainted(false); homeBtn.setBorderPainted(false);
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.add(homeBtn);

        panel.add(header);

        // Sender Section
        JLabel senderTitle = new JLabel("SENDER DETAILS");
        senderTitle.setFont(new Font("Arial", Font.BOLD, 13));
        senderTitle.setForeground(ORANGE);
        senderTitle.setBounds(15, 65, 200, 22);
        panel.add(senderTitle);

        JSeparator sep1 = new JSeparator();
        sep1.setBounds(15, 88, 580, 2);
        sep1.setForeground(ORANGE);
        panel.add(sep1);

        addLabel(panel, "Sender Name", 15, 95);
        senderNameField = createField();
        senderNameField.setBounds(15, 115, 280, 35);
        panel.add(senderNameField);

        addLabel(panel, "Sender Phone", 310, 95);
        senderPhoneField = createField();
        senderPhoneField.setBounds(310, 115, 285, 35);
        panel.add(senderPhoneField);

        addLabel(panel, "Sender Address", 15, 158);
        senderAddressField = createField();
        senderAddressField.setBounds(15, 178, 580, 35);
        panel.add(senderAddressField);

        // Receiver Section
        JLabel receiverTitle = new JLabel("RECEIVER DETAILS");
        receiverTitle.setFont(new Font("Arial", Font.BOLD, 13));
        receiverTitle.setForeground(ORANGE);
        receiverTitle.setBounds(15, 225, 200, 22);
        panel.add(receiverTitle);

        JSeparator sep2 = new JSeparator();
        sep2.setBounds(15, 248, 580, 2);
        sep2.setForeground(ORANGE);
        panel.add(sep2);

        addLabel(panel, "Receiver Name", 15, 255);
        receiverNameField = createField();
        receiverNameField.setBounds(15, 275, 280, 35);
        panel.add(receiverNameField);

        addLabel(panel, "Receiver Phone", 310, 255);
        receiverPhoneField = createField();
        receiverPhoneField.setBounds(310, 275, 285, 35);
        panel.add(receiverPhoneField);

        addLabel(panel, "Receiver Address", 15, 318);
        receiverAddressField = createField();
        receiverAddressField.setBounds(15, 338, 580, 35);
        panel.add(receiverAddressField);

        // Package Details
        JLabel pkgTitle = new JLabel("PACKAGE DETAILS");
        pkgTitle.setFont(new Font("Arial", Font.BOLD, 13));
        pkgTitle.setForeground(ORANGE);
        pkgTitle.setBounds(15, 385, 200, 22);
        panel.add(pkgTitle);

        JSeparator sep3 = new JSeparator();
        sep3.setBounds(15, 408, 580, 2);
        sep3.setForeground(ORANGE);
        panel.add(sep3);

        addLabel(panel, "Weight (kg)", 15, 415);
        weightField = createField();
        weightField.setBounds(15, 435, 130, 35);
        panel.add(weightField);

        addLabel(panel, "Type", 160, 415);
        typeBox = new JComboBox<>(new String[]{"Document", "Parcel", "Fragile", "Electronics", "Clothes", "Other"});
        typeBox.setBounds(160, 435, 140, 35);
        typeBox.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(typeBox);

        addLabel(panel, "Priority", 315, 415);
        priorityBox = new JComboBox<>(new String[]{"Standard", "Express", "Overnight"});
        priorityBox.setBounds(315, 435, 130, 35);
        priorityBox.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(priorityBox);

        addLabel(panel, "Description", 460, 415);
        descField = createField();
        descField.setBounds(460, 435, 135, 35);
        panel.add(descField);

        // Buttons
        saveBtn = new JButton("BOOK COURIER →");
        saveBtn.setBounds(15, 500, 200, 48);
        saveBtn.setBackground(ORANGE);
        saveBtn.setForeground(WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(saveBtn);

        cancelBtn = new JButton("CANCEL");
        cancelBtn.setBounds(230, 500, 150, 48);
        cancelBtn.setBackground(DARK_GRAY);
        cancelBtn.setForeground(WHITE);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(cancelBtn);

        // Bottom bar
        JPanel bottomBar = new JPanel();
        bottomBar.setBounds(0, 600, 620, 10);
        bottomBar.setBackground(ORANGE);
        panel.add(bottomBar);

        add(panel);

        saveBtn.addActionListener(e -> bookCourier());
        cancelBtn.addActionListener(e -> { new HomePage(loggedInUser, userId); dispose(); });
        homeBtn.addActionListener(e -> { new HomePage(loggedInUser, userId); dispose(); });

        setVisible(true);
    }

    void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(DARK_GRAY);
        l.setBounds(x, y, 200, 18);
        p.add(l);
    }

    JTextField createField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return f;
    }

    void bookCourier() {
        String sName = senderNameField.getText().trim();
        String sPhone = senderPhoneField.getText().trim();
        String sAddr = senderAddressField.getText().trim();
        String rName = receiverNameField.getText().trim();
        String rPhone = receiverPhoneField.getText().trim();
        String rAddr = receiverAddressField.getText().trim();
        String weight = weightField.getText().trim();

        if (sName.isEmpty() || rName.isEmpty() || sAddr.isEmpty() || rAddr.isEmpty() || weight.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!"); return;
        }

        try {
            double wt = Double.parseDouble(weight);
            String priority = priorityBox.getSelectedItem().toString();

            // Calculate price based on weight and priority
            double price = wt * 50;
            if (priority.equals("Express")) price *= 1.5;
            if (priority.equals("Overnight")) price *= 2.5;

            // Generate tracking number
            String trackingNo = "SWC" + System.currentTimeMillis() % 1000000;

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO couriers (tracking_no, sender_name, sender_phone, sender_address, " +
                "receiver_name, receiver_phone, receiver_address, weight, type, priority, " +
                "description, price, status, booked_by) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            ps.setString(1, trackingNo); ps.setString(2, sName); ps.setString(3, sPhone);
            ps.setString(4, sAddr); ps.setString(5, rName); ps.setString(6, rPhone);
            ps.setString(7, rAddr); ps.setDouble(8, wt); ps.setString(9, typeBox.getSelectedItem().toString());
            ps.setString(10, priority); ps.setString(11, descField.getText());
            ps.setDouble(12, price); ps.setString(13, "Booked"); ps.setInt(14, userId);
            ps.executeUpdate();

            // Save first status log
            PreparedStatement log = DBConnection.getConnection().prepareStatement(
                "INSERT INTO tracking_logs (tracking_no, status, location, note) VALUES (?,?,?,?)");
            log.setString(1, trackingNo); log.setString(2, "Booked");
            log.setString(3, "Origin Center"); log.setString(4, "Courier booked successfully");
            log.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "✅ Courier Booked Successfully!\n\n" +
                "Tracking Number: " + trackingNo + "\n" +
                "Estimated Price: ₹" + String.format("%.2f", price) + "\n\n" +
                "Use this tracking number to track your courier!",
                "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

            new HomePage(loggedInUser, userId); dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid weight!");
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}

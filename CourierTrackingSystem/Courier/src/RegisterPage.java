import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterPage extends JFrame {

    static final Color ORANGE = new Color(255, 102, 0);
    static final Color DARK_GRAY = new Color(50, 50, 50);
    static final Color WHITE = Color.WHITE;

    JTextField usernameField, emailField, phoneField;
    JPasswordField passwordField, confirmField;
    JButton registerBtn, backBtn;

    public RegisterPage() {
        setTitle("SwiftCourier - Register");
        setSize(500, 530);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(WHITE);

        // Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 500, 75);
        header.setBackground(DARK_GRAY);

        JLabel logo = new JLabel("🚚 SwiftCourier");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(WHITE);
        logo.setBounds(20, 10, 250, 35);
        header.add(logo);

        JLabel sub = new JLabel("Create your account");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(ORANGE);
        sub.setBounds(22, 45, 200, 20);
        header.add(sub);

        panel.add(header);

        int y = 90;

        addLabel(panel, "Full Name", 20, y);
        usernameField = createField();
        usernameField.setBounds(20, y + 22, 450, 38);
        panel.add(usernameField);
        y += 75;

        addLabel(panel, "Email", 20, y);
        emailField = createField();
        emailField.setBounds(20, y + 22, 450, 38);
        panel.add(emailField);
        y += 75;

        addLabel(panel, "Phone", 20, y);
        phoneField = createField();
        phoneField.setBounds(20, y + 22, 450, 38);
        panel.add(phoneField);
        y += 75;

        addLabel(panel, "Password", 20, y);
        passwordField = new JPasswordField();
        passwordField.setBounds(20, y + 22, 215, 38);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        panel.add(passwordField);

        addLabel(panel, "Confirm Password", 255, y);
        confirmField = new JPasswordField();
        confirmField.setBounds(255, y + 22, 215, 38);
        confirmField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        panel.add(confirmField);
        y += 75;

        registerBtn = new JButton("CREATE ACCOUNT →");
        registerBtn.setBounds(20, y + 10, 215, 45);
        registerBtn.setBackground(ORANGE);
        registerBtn.setForeground(WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 13));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(registerBtn);

        backBtn = new JButton("← BACK TO LOGIN");
        backBtn.setBounds(255, y + 10, 215, 45);
        backBtn.setBackground(DARK_GRAY);
        backBtn.setForeground(WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 13));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(backBtn);

        add(panel);

        registerBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> { new LoginPage(); dispose(); });

        setVisible(true);
    }

    void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(DARK_GRAY);
        l.setBounds(x, y, 200, 20);
        p.add(l);
    }

    JTextField createField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return f;
    }

    void doRegister() {
        String name = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();
        String confirm = new String(confirmField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!"); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter valid email!"); return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!"); return;
        }

        try {
            PreparedStatement check = DBConnection.getConnection().prepareStatement(
                "SELECT * FROM users WHERE username=?");
            check.setString(1, name);
            if (check.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Username already exists!"); return;
            }

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO users (username, email, phone, password) VALUES (?,?,?,?)");
            ps.setString(1, name); ps.setString(2, email);
            ps.setString(3, phone); ps.setString(4, pass);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Account Created Successfully!");
            new LoginPage(); dispose();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}

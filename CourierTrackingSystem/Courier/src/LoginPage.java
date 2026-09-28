import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {

    // FedEx Colors
    static final Color ORANGE = new Color(255, 102, 0);
    static final Color PURPLE = new Color(75, 0, 130);
    static final Color WHITE = Color.WHITE;
    static final Color LIGHT_GRAY = new Color(245, 245, 245);
    static final Color DARK_GRAY = new Color(50, 50, 50);

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginBtn, registerBtn;

    public LoginPage() {
        setTitle("SwiftCourier - Login");
        setSize(500, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(WHITE);

        // Left orange bar
        JPanel leftBar = new JPanel(null);
        leftBar.setBounds(0, 0, 10, 420);
        leftBar.setBackground(ORANGE);
        mainPanel.add(leftBar);

        // Top header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 500, 90);
        header.setBackground(DARK_GRAY);

        JLabel logo = new JLabel("🚚 SwiftCourier");
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setForeground(WHITE);
        logo.setBounds(20, 15, 260, 40);
        header.add(logo);

        JLabel tagline = new JLabel("Fast. Reliable. Tracked.");
        tagline.setFont(new Font("Arial", Font.ITALIC, 13));
        tagline.setForeground(ORANGE);
        tagline.setBounds(22, 52, 220, 22);
        header.add(tagline);

        mainPanel.add(header);

        // Welcome text
        JLabel welcome = new JLabel("Welcome Back!");
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setForeground(DARK_GRAY);
        welcome.setBounds(20, 108, 200, 30);
        mainPanel.add(welcome);

        JLabel sub = new JLabel("Please login to your account");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(new Color(120, 120, 120));
        sub.setBounds(20, 135, 250, 22);
        mainPanel.add(sub);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userLabel.setForeground(DARK_GRAY);
        userLabel.setBounds(20, 170, 100, 22);
        mainPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(20, 193, 450, 40);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(usernameField);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passLabel.setForeground(DARK_GRAY);
        passLabel.setBounds(20, 243, 100, 22);
        mainPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(20, 266, 450, 40);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(passwordField);

        // Login Button
        loginBtn = new JButton("LOGIN →");
        loginBtn.setBounds(20, 325, 215, 45);
        loginBtn.setBackground(ORANGE);
        loginBtn.setForeground(WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 15));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(loginBtn);

        // Register Button
        registerBtn = new JButton("CREATE ACCOUNT");
        registerBtn.setBounds(255, 325, 215, 45);
        registerBtn.setBackground(DARK_GRAY);
        registerBtn.setForeground(WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(registerBtn);

        add(mainPanel);

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> { new RegisterPage(); dispose(); });

        setVisible(true);
    }

    void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!"); return;
        }

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                new HomePage(username, rs.getInt("id"));
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}

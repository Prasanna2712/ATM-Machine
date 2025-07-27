import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ATMFrontPage extends JFrame implements ActionListener {

    JButton loginButton, generatePinButton, changePinButton, exitButton;
    JTextField pinField;
    JLabel background;

    ATMFrontPage() {
        setTitle("ATM Machine - Welcome");
        setSize(500, 400); // Frame size to match background image
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // ✅ Load and set background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("Alty _ Projects _ Privat Terminal — Self Service….jpg"));

        Image img = bgIcon.getImage().getScaledInstance(500, 400, Image.SCALE_SMOOTH);
        background = new JLabel(new ImageIcon(img));
        background.setBounds(0, 0, 500, 400);
        background.setLayout(null); // so we can add buttons on it

        // ✅ Add components to background
        JLabel title = new JLabel("ATM System");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE); // optional: better contrast
        title.setBounds(160, 30, 200, 30);
        background.add(title);

        JLabel pinLabel = new JLabel("Enter 4-digit PIN:");
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setBounds(120, 90, 120, 30);
        background.add(pinLabel);

        pinField = new JTextField();
        pinField.setBounds(250, 90, 100, 30);
        background.add(pinField);

        loginButton = new JButton("Login");
        loginButton.setBounds(180, 140, 120, 30);
        loginButton.addActionListener(this);
        background.add(loginButton);

        generatePinButton = new JButton("Generate PIN");
        generatePinButton.setBounds(180, 180, 120, 30);
        generatePinButton.addActionListener(this);
        background.add(generatePinButton);

        changePinButton = new JButton("Change PIN");
        changePinButton.setBounds(180, 220, 120, 30);
        changePinButton.addActionListener(this);
        background.add(changePinButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(180, 260, 120, 30);
        exitButton.addActionListener(this);
        background.add(exitButton);

        add(background);
    }

    // ✅ Action logic for buttons
    public void actionPerformed(ActionEvent e) {
        String pin = pinField.getText().trim();

        if (e.getSource() != exitButton && (pin.length() != 4 || !pin.matches("\\d{4}"))) {
            JOptionPane.showMessageDialog(this, "PIN must be exactly 4 digits.");
            return;
        }

        if (e.getSource() == loginButton) {
            if (isPinAlreadyExists(pin)) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                new Options(pin).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "PIN not found. Please generate a PIN.");
            }
        }

        if (e.getSource() == generatePinButton) {
            if (isPinAlreadyExists(pin)) {
                JOptionPane.showMessageDialog(this, "This PIN already exists. Try another one.");
            } else {
                insertNewUser(pin);
                JOptionPane.showMessageDialog(this, "PIN created successfully!");
                new Options(pin).setVisible(true);
                this.dispose();
            }
        }

        if (e.getSource() == changePinButton) {
            String newPin = JOptionPane.showInputDialog(this, "Enter new 4-digit PIN:");
            if (newPin != null && newPin.matches("\\d{4}")) {
                if (!isPinAlreadyExists(pin)) {
                    JOptionPane.showMessageDialog(this, "Current PIN not found. Please check or generate a PIN first.");
                    return;
                }
                if (isPinAlreadyExists(newPin)) {
                    JOptionPane.showMessageDialog(this, "New PIN already in use. Choose another.");
                    return;
                }
                changeUserPin(pin, newPin);
                JOptionPane.showMessageDialog(this, "PIN changed successfully!");
                pinField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid new PIN. It must be 4 digits.");
            }
        }

        if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    // ✅ Database methods
    private boolean isPinAlreadyExists(String pin) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE pin = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, pin);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void insertNewUser(String pin) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users (pin, balance) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, pin);
            stmt.setDouble(2, 10000.0); // default balance
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while setting PIN.");
        }
    }

    private void changeUserPin(String oldPin, String newPin) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE users SET pin = ? WHERE pin = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newPin);
            stmt.setString(2, oldPin);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while changing PIN.");
        }
    }

    public static void main(String[] args) {
        new ATMFrontPage().setVisible(true);
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Options extends JFrame implements ActionListener {
    String userPIN;
    JButton withdrawButton, depositButton, balanceButton, exitButton,backButton;
    double balance;

    public Options(String pin) {
        this.userPIN = pin;
        fetchBalanceFromDB(); // Load balance from DB

        setTitle("ATM Machine");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("ATM Operations");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(100, 20, 250, 30);
        add(title);

        withdrawButton = new JButton("Withdraw Amount");
        withdrawButton.setBounds(120, 70, 150, 30);
        withdrawButton.addActionListener(this);
        add(withdrawButton);

        depositButton = new JButton("Deposit Amount");
        depositButton.setBounds(120, 110, 150, 30);
        depositButton.addActionListener(this);
        add(depositButton);

        balanceButton = new JButton("Check Balance");
        balanceButton.setBounds(120, 150, 150, 30);
        balanceButton.addActionListener(this);
        add(balanceButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(120, 190, 150, 30);
        exitButton.addActionListener(this);
        add(exitButton);

        backButton = new JButton("Back");
        backButton.setBounds(120, 230, 150, 30);
        backButton.addActionListener(this);
        add(backButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == withdrawButton) {
            String input = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0 || amount > balance) {
                    JOptionPane.showMessageDialog(this, "Invalid or Insufficient Balance.");
                } else {
                    balance -= amount;
                    updateBalanceInDB();
                    JOptionPane.showMessageDialog(this, "₹" + amount + " withdrawn.\nRemaining Balance: ₹" + balance);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }

        if (e.getSource() == depositButton) {
            String input = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
            try {
                double amount = Double.parseDouble(input);
                if (amount > 0) {
                    balance += amount;
                    updateBalanceInDB();
                    JOptionPane.showMessageDialog(this, "₹" + amount + " deposited.\nNew Balance: ₹" + balance);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        }

        if (e.getSource() == balanceButton) {
            JOptionPane.showMessageDialog(this, "Current Balance: ₹" + balance);
        }

        if (e.getSource() == exitButton) {
            System.exit(0);
        }
        if (e.getSource() == backButton) {
            new ATMFrontPage().setVisible(true);
        }
    }

    // ✅ Fetch balance from the database using userPIN
    private void fetchBalanceFromDB() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT balance FROM users WHERE pin = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userPIN);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                balance = rs.getDouble("balance");
            } else {
                JOptionPane.showMessageDialog(this, "User not found in the database.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while fetching balance.");
        }
    }

    // ✅ Update balance in the database using userPIN
    private void updateBalanceInDB() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE users SET balance = ? WHERE pin = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, balance);
            stmt.setString(2, userPIN);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while updating balance.");
        }
    }

    // (Optional main method for testing)
    public static void main(String[] args) {
        // new Options("1234").setVisible(true);
    }
}

package org.example.main;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class AuthenticationFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final String DATABASE_FILE = "src/maliarenko_database.csv";

    private final String accessControlMethod;

    public AuthenticationFrame(String accessControlMethod) {
        this.accessControlMethod = accessControlMethod;
        initializeFrame();
        addComponents();
    }


    private void initializeFrame() {
        setTitle("Автентифікація");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Ім'я користувача:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = createLoginButton();
        panel.add(loginButton);

        add(panel);
    }

    private JButton createLoginButton() {
        JButton loginButton = new JButton("Увійти");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(username, password)) {
                MainFrame mainFrame = new MainFrame(username, accessControlMethod);
                mainFrame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(AuthenticationFrame.this,
                        "Невірне ім'я користувача або пароль.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        });
        return loginButton;
    }

    private boolean authenticateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}

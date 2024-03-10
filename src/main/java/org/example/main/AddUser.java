package org.example.main;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class AddUser extends JFrame {
    private final JTextField usernameField;
    private final JTextField passwordField;
    private final JCheckBox complexPasswordCheckbox;
    private final JTextField accessLevelField;
    private static final String DATABASE_FILE = "src/maliarenko_database.csv";

    public AddUser() {
        setTitle("Додати нового користувача");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        panel.add(new Label("Ім'я користувача:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Складний пароль:"));
        complexPasswordCheckbox = new JCheckBox();
        panel.add(complexPasswordCheckbox);

        panel.add(new JLabel("Рівень доступу:"));
        accessLevelField = new JTextField();
        panel.add(accessLevelField);

        JButton addUserButton = getjButton();
        panel.add(addUserButton);

        add(panel);
    }

    private JButton getjButton() {
        JButton addUserButton = new JButton("Додати користувача");
        addUserButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            boolean isComplex = complexPasswordCheckbox.isSelected();
            String accessLevel = accessLevelField.getText();
            addUser(username, password, isComplex, accessLevel);
        });
        return addUserButton;
    }


    private void addUser(String username, String password, boolean isComplex, String accessLevel) {
        if ((accessLevel.equals("Середній") || accessLevel.equals("Високий")) && !isComplex) {
            JOptionPane.showMessageDialog(AddUser.this,
                    "Користувачі з рівнями доступу 'Середній' та 'Високий' повинні мати складний пароль.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PasswordUtils.validatePasswordComplexity(isComplex, password);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE, true))) {
            writer.write(username + ":" + password + ":" + (isComplex ? "Complex" : "Simple") + ":" + accessLevel);
            writer.newLine();
            JOptionPane.showMessageDialog(AddUser.this,
                    "Користувач " + username + " успішно доданий до бази даних.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(AddUser.this,
                    "Помилка під час запису до файлу бази даних.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }


}

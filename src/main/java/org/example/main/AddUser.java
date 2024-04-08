package org.example.main;

import org.example.discretionaryAccess.DiscretionaryAccessDialog;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.MessageDigest;

public class AddUser extends JFrame {
    private final JTextField usernameField;
    private final JTextField passwordField;
    private final JCheckBox complexPasswordCheckbox;
    private final JTextField accessLevelField;
    private final JComboBox<String> roleComboBox;
    private final JCheckBox discretionaryAccessCheckbox;
    private final JTextField expiryDaysField; // Додано поле для введення терміну дії пароля
    private static final String DATABASE_FILE = "src/maliarenko_database.csv";
    private static final String csvFile = "src/roleAccess.csv";
    private static int adminCount = 0;

    public AddUser() {
        setTitle("Додати нового користувача");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2)); // Змінено кількість рядків на 8
        panel.add(new JLabel("Ім'я користувача:"));
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

        panel.add(new JLabel("Роль:"));
        roleComboBox = new JComboBox<>(new String[]{"Користувач", "Редактор", "Адміністратор"});
        panel.add(roleComboBox);

        panel.add(new JLabel("Дискреційний доступ:"));
        discretionaryAccessCheckbox = new JCheckBox();
        panel.add(discretionaryAccessCheckbox);

        panel.add(new JLabel("Термін дії паролю (дні):")); // Додано мітку для нового поля
        expiryDaysField = new JTextField("30"); // Встановлення значення за замовчуванням 30 днів
        panel.add(expiryDaysField);

        JButton addUserButton = getAddUserButton();
        panel.add(addUserButton);

        add(panel);
    }

    private JButton getAddUserButton() {
        JButton addUserButton = new JButton("Додати користувача");
        addUserButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            boolean isComplex = complexPasswordCheckbox.isSelected();
            String role = (String) roleComboBox.getSelectedItem();
            String accessLevel = accessLevelField.getText();
            int expiryDays = Integer.parseInt(expiryDaysField.getText()); // Отримання значення терміну дії пароля
            if (discretionaryAccessCheckbox.isSelected()) {
                new DiscretionaryAccessDialog(AddUser.this, username);
            }
            if (role != null) {
                checkAdminExistence();
                if (role.equals("Адміністратор") && adminCount >= 2) {
                    JOptionPane.showMessageDialog(AddUser.this,
                            "Досягнуто максимальну кількість адміністраторів у системі.",
                            "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addUser(username, password, isComplex, role, expiryDays); // Додано передачу значення терміну дії пароля
            } else {
                addUser(username, password, isComplex, accessLevel, expiryDays); // Додано передачу значення терміну дії пароля
            }
        });
        return addUserButton;
    }

    private void addUser(String username, String password, boolean isComplex, String access, int expiryDays) {
        if ((access.equals("Середній") || access.equals("Високий")) && !isComplex) {
            JOptionPane.showMessageDialog(AddUser.this,
                    "Користувачі з рівнями доступу 'Середній' та 'Високий' повинні мати складний пароль.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if ((access.equals("Редактор") || access.equals("Адміністратор")) && !isComplex) {
            JOptionPane.showMessageDialog(AddUser.this,
                    "Користувачі з ролями 'Редактор' або 'Адміністратор' повинні мати складний пароль.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            try (BufferedWriter writerRole = new BufferedWriter(new FileWriter(csvFile, true))) {
                writerRole.write(username + "," + access);
                writerRole.newLine();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(AddUser.this,
                        "Помилка під час запису до файлу бази даних.",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (access.equals("Адміністратор")) {
            adminCount++;
        }

        PasswordUtils.validatePasswordComplexity(isComplex, password);

        String creationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Зашифрувати пароль за допомогою алгоритму SHA-256
        String encryptedPassword = encryptPassword(password);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE, true))) {
            writer.write(username + ":" + encryptedPassword + ":" + (isComplex ? "Complex" : "Simple") + ":" + access + ":" + expiryDays + ":" + creationDate);
            writer.newLine();
            JOptionPane.showMessageDialog(AddUser.this,
                    "Користувач " + username + " успішно доданий до бази даних.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(AddUser.this,
                    "Помилка під час запису до файлу бази даних.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void checkAdminExistence() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[1].equals("Адміністратор")) {
                    adminCount++;
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


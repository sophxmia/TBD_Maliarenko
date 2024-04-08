package org.example.main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChangePassword extends JFrame {
    private JComboBox<String> usernameDropdown;
    private JTextField oldPasswordField;
    private JTextField newPasswordField;
    private JCheckBox complexPasswordCheckbox;
    private JTextField expiryDaysField; // Додано поле для введення терміну дії паролів
    private static final String DATABASE_FILE = "src/maliarenko_database.csv";
    private static final String OLD_PASSWORDS_FILE = "src/maliarenko_old_passwords.csv";

    public ChangePassword() {
        initializeFrame();
        JPanel panel = createPanel();
        addComponentsToPanel(panel);
        add(panel);
    }

    private void initializeFrame() {
        setTitle("Змінити пароль");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2)); // Змінено кількість рядків на 6
        return panel;
    }

    private void addComponentsToPanel(JPanel panel) {
        panel.add(new JLabel("Ім'я користувача:"));
        List<String> usernames = getUsernamesFromDatabase();
        usernameDropdown = new JComboBox<>(usernames.toArray(new String[0]));
        panel.add(usernameDropdown);
        panel.add(new JLabel("Старий пароль:"));
        oldPasswordField = new JPasswordField();
        panel.add(oldPasswordField);
        panel.add(new JLabel("Новий пароль:"));
        newPasswordField = new JPasswordField();
        panel.add(newPasswordField);
        panel.add(new JLabel("Складний пароль:"));
        complexPasswordCheckbox = new JCheckBox();
        panel.add(complexPasswordCheckbox);
        panel.add(new JLabel("Термін дії паролю (дні):")); // Додано мітку для нового поля
        expiryDaysField = new JTextField("30"); // Встановлення значення за замовчуванням 30 днів
        panel.add(expiryDaysField);
        JButton changePasswordButton = new JButton("Змінити пароль");
        changePasswordButton.addActionListener(e -> {
            String username = (String) usernameDropdown.getSelectedItem();
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            boolean isComplex = complexPasswordCheckbox.isSelected();
            int expiryDays = Integer.parseInt(expiryDaysField.getText()); // Отримання значення терміну дії пароля
            changeUserPassword(username, oldPassword, newPassword, isComplex, expiryDays);
        });
        panel.add(changePasswordButton);
    }

    private List<String> getUsernamesFromDatabase() {
        List<String> usernames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                usernames.add(parts[0]);
            }
        } catch (IOException e) {
            showError("Помилка при читанні бази даних.");
        }
        return usernames;
    }

    private void changeUserPassword(String username, String oldPassword, String newPassword, boolean isComplexNew, int expiryDays) {
        String lastPasswordChangeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String[] oldPasswords = getOldPasswords(username);
        boolean passwordChanged = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE + ".tmp"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    if (parts[1].equals(oldPassword)) {
                        if (!isOldPassword(username, newPassword)) {
                            // Шифрування нового паролю перед збереженням у базі даних
                            String encryptedNewPassword = encryptPassword(newPassword);
                            writer.write(username + ":" + encryptedNewPassword + ":" + (isComplexNew ? "Complex" : "Simple") + ":" + expiryDays + ":" + lastPasswordChangeDate);
                            passwordChanged = true;
                        } else {
                            showError("Новий пароль збігається з одним з останніх трьох старих паролів.");
                            return;
                        }
                    } else {
                        showError("Введений старий пароль невірний.");
                        return;
                    }
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Помилка під час зміни паролю.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
        if (passwordChanged) {
            saveOldPasswords(username, newPassword, oldPasswords);
            // Видалення оригінального файлу та перейменування тимчасового файлу
            File originalFile = new File(DATABASE_FILE);
            File tempFile = new File(DATABASE_FILE + ".tmp");
            if (originalFile.delete()) {
                if (!tempFile.renameTo(originalFile)) {
                    showError("Помилка під час перейменування файлу.");
                }
            } else {
                showError("Помилка під час видалення оригінального файлу.");
            }
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

    private boolean isOldPassword(String username, String password) {
        String[] oldPasswords = getOldPasswords(username);
        for (String oldPassword : oldPasswords) {
            if (oldPassword.equals(password)) {
                return true;
            }
        }
        return false;
    }

    private String[] getOldPasswords(String username) {
        File file = new File(OLD_PASSWORDS_FILE);
        if (!file.exists()) {
            return new String[0];
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(OLD_PASSWORDS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    return parts[1].split(",");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String[0];
    }

    private void saveOldPasswords(String username, String newPassword, String[] oldPasswords) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OLD_PASSWORDS_FILE, true))) {
            StringBuilder sb = new StringBuilder();
            sb.append(username).append(":");
            if (oldPasswords.length >= 3) {
                for (int i = 1; i < oldPasswords.length; i++) sb.append(oldPasswords[i]).append(",");
                sb.append(newPassword);
            } else {
                for (String oldPassword : oldPasswords) sb.append(oldPassword).append(",");
                sb.append(newPassword);
            }
            writer.write(sb.toString());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Помилка", JOptionPane.ERROR_MESSAGE);
    }
}


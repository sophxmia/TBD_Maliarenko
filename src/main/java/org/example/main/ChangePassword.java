package org.example.main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangePassword extends JFrame {
    private JComboBox<String> usernameDropdown;
    private JTextField oldPasswordField;
    private JTextField newPasswordField;
    private JCheckBox complexPasswordCheckbox;
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
        panel.setLayout(new GridLayout(5, 2));
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
        JButton changePasswordButton = new JButton("Змінити пароль");
        changePasswordButton.addActionListener(e -> {
            String username = (String) usernameDropdown.getSelectedItem();
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            boolean isComplex = complexPasswordCheckbox.isSelected();
            changeUserPassword(username, oldPassword, newPassword, isComplex);
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

    private void changeUserPassword(String username, String oldPassword, String newPassword, boolean isComplexNew) {
        try {
            // Check complexity requirements
            if (isComplexNew && isUserRequiresComplexPassword(username)) {
                showError("Користувачі з рівнями доступу 'Середній' та 'Високий' повинні мати складний пароль.");
                return;
            }
            PasswordUtils.validatePasswordComplexity(isComplexNew, newPassword);

            // Check if the new password is not among the last three old passwords
            String[] oldPasswords = getOldPasswords(username);
            if (Arrays.asList(oldPasswords).contains(newPassword)) {
                showError("Новий пароль не може співпадати з попередніми трьома старими паролями.");
                return;
            }

            // Update password in database
            updateUserPasswordInDatabase(username, oldPassword, newPassword, isComplexNew);

            // Save the new password to the list of old passwords
            saveOldPasswords(username, newPassword, oldPasswords);
        } catch (IOException e) {
            showError("Помилка під час зміни паролю.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private boolean isUserRequiresComplexPassword(String username) throws IOException {
        String accessLevel = getAccessLevel(username);
        return accessLevel.equals("Середній") || accessLevel.equals("Високий");
    }

    private void updateUserPasswordInDatabase(String username, String oldPassword, String newPassword, boolean isComplexNew) throws IOException {
        boolean userFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE + ".tmp"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    userFound = true;
                    if (parts.length == 1) {
                        writer.write(username + ":" + newPassword + ":" + (isComplexNew ? "Complex" : "Simple"));
                    } else if (parts.length == 3 && parts[1].equals(oldPassword)) {
                        writer.write(username + ":" + newPassword + ":" + (isComplexNew ? "Complex" : "Simple") + ":" + parts[2]);
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }

        if (!userFound) {
            showError("Користувача з ім'ям " + username + " не знайдено.");
            return;
        }

        File originalFile = new File(DATABASE_FILE);
        File tempFile = new File(DATABASE_FILE + ".tmp");
        if (!originalFile.delete()) {
            showError("Помилка під час видалення оригінального файлу.");
            return;
        }
        if (!tempFile.renameTo(originalFile)) {
            showError("Помилка під час перейменування файлу.");
        }
    }

    private String getAccessLevel(String username) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 4 && parts[0].equals(username)) {
                    return parts[3]; // Повертаємо рівень доступу
                }
            }
        }
        throw new IllegalArgumentException("Користувача з ім'ям " + username + " не знайдено.");
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

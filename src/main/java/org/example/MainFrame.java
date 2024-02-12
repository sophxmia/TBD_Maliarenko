package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;

public class MainFrame extends JFrame {
    private final JTextField usernameField;
    private final JTextField passwordField;
    private final JTextField newPasswordField;
    private final JCheckBox complexPasswordCheckbox;
    private static final String DATABASE_FILE = "src/maliarenko_database.csv";
    //файл, в якому зберігатимуться старі паролі
    private static final String OLD_PASSWORDS_FILE = "src/maliarenko_old_passwords.csv";
    // Поле для зберігання імені користувача
    private final String username;

    public MainFrame(String username) {
        // Збереження імені користувача
        this.username = username;

        // Встановлення заголовку вікна
        setTitle("TBD_Maliarenko - " + username);

        // Встановлення розміру вікна
        setSize(400, 400);

        // Встановлення локації вікна
        setLocation(500, 200);

        // Встановлення дії закриття вікна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Створення пункту меню та підменю
        JMenuBar menuBar = getjMenuBar();

        // Встановлення панелі меню для головного вікна
        setJMenuBar(menuBar);

        // Створення панелі для компонентів
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));
        // Додавання поля для введення імені користувача
        panel.add(new Label("Ім'я користувача:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        // Додавання поля для введення пароля
        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        // Додавання чекбоксу для вибору складності пароля
        panel.add(new JLabel("Складний пароль:"));
        complexPasswordCheckbox = new JCheckBox();
        panel.add(complexPasswordCheckbox);

        // Додавання поля для введення нового пароля
        panel.add(new JLabel("Новий пароль:"));
        newPasswordField = new JPasswordField();
        panel.add(newPasswordField);

        // Додавання кнопки для зміни пароля
        JButton changePasswordButton = getButton();
        panel.add(changePasswordButton);

        // Додавання кнопки для додавання користувача
        JButton addUserButton = getjButton();
        panel.add(addUserButton);
        // Додавання панелі до вікна
        add(panel);
    }

    private JButton getButton() {
        JButton changePasswordButton = new JButton("Змінити пароль");
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String oldPassword = passwordField.getText();
                String newPassword = newPasswordField.getText();
                boolean isComplex = complexPasswordCheckbox.isSelected();
                changeUserPassword(username, oldPassword, newPassword, isComplex);
            }
        });
        return changePasswordButton;
    }

    private JButton getjButton() {
        JButton addUserButton = new JButton("Додати користувача");
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                boolean isComplex = complexPasswordCheckbox.isSelected();
                addUser(username, password, isComplex);
            }
        });
        return addUserButton;
    }

    private void addUser(String username, String password, boolean isComplex) {
        validatePasswordComplexity(isComplex, password);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE, true))) {
            writer.write(username + ":" + password + ":" + (isComplex ? "Complex" : "Simple"));
            writer.newLine();
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Користувач " + username + " успішно доданий до бази даних.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Помилка під час запису до файлу бази даних.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void validatePasswordComplexity(boolean isComplex, String password) {
        // Перевірка на довжину паролю для складного паролю
        if (isComplex && password.length() < 8) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Складний пароль повинен містити принаймні 8 символів.", "Помилка", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException();
        }
        // Перевірка на довжину паролю для простого паролю
        if (!isComplex && password.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Простий пароль повинен містити хоча б один символ.", "Помилка", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException();
        }
        if (isComplex) {
            boolean containsLowercase = false;
            boolean containsUppercase = false;
            boolean containsDigit = false;

            for (char c : password.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    containsLowercase = true;
                } else if (Character.isUpperCase(c)) {
                    containsUppercase = true;
                } else if (Character.isDigit(c)) {
                    containsDigit = true;
                }
            }
            if (!(containsLowercase && containsUppercase && containsDigit)) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Складний пароль повинен містити символи з трьох різних наборів: великі літери, малі літери, цифри.",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException();
            }
        }
    }

    private void changeUserPassword(String username, String oldPassword, String newPassword, boolean isComplexNew) {
        try {
            validatePasswordComplexity(isComplexNew, newPassword);
            //масив для зберігання старих паролів
            String[] oldPasswords = getOldPasswords(username);
            // Перевірка, чи новий пароль не співпадає з одним з трьох останніх старих паролів
            if (Arrays.asList(oldPasswords).contains(newPassword)) {
                JOptionPane.showMessageDialog(null,
                        "Новий пароль не може співпадати з попередніми трьома старими паролями.",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean userFound = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE + ".tmp"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length >= 1 && parts[0].equals(username)) {
                        userFound = true;
                        if (parts.length == 1) { // Користувач без пароля
                            writer.write(username + ":" + newPassword + ":" + (isComplexNew ? "Complex" : "Simple"));
                        } else if (parts.length == 3 && parts[1].equals(oldPassword)) {
                            writer.write(username + ":" + newPassword + ":" + (isComplexNew ? "Complex" : "Simple"));
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
                JOptionPane.showMessageDialog(null,
                        "Користувача з ім'ям " + username + " не знайдено.",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
            } else {
                File originalFile = new File(DATABASE_FILE);
                File tempFile = new File(DATABASE_FILE + ".tmp");
                if (originalFile.delete()) {
                    if (!tempFile.renameTo(originalFile)) {
                        JOptionPane.showMessageDialog(null,
                                "Помилка під час перейменування файлу.",
                                "Помилка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Помилка під час видалення оригінального файлу.",
                            "Помилка", JOptionPane.ERROR_MESSAGE);
                }

                // Збереження нового пароля в список старих паролей
                saveOldPasswords(username, newPassword, oldPasswords);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Помилка під час зміни паролю.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IllegalArgumentException ex) {
            // Обробка помилок перевірки складності пароля
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
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


    private JMenuBar getjMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu aboutMenu = new JMenu("Про автора");

        // Додавання пункту меню "Про автора"
        JMenuItem authorItem = new JMenuItem("Інформація про автора");
        authorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Номер групи: BI-444\nПрізвище: Maliarenko\nІм'я: Sofiia");
            }
        });
        aboutMenu.add(authorItem);

        // Додавання пункту меню до панелі меню
        menuBar.add(aboutMenu);
        return menuBar;
    }

    public static void main(String[] args) {
        // Створення та відображення головного вікна
        SwingUtilities.invokeLater(() -> {
            AuthenticationFrame authFrame = new AuthenticationFrame();
            authFrame.setVisible(true);
        });
    }
}

class AuthenticationFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private static final String DATABASE_FILE = "src/maliarenko_database.csv";

    public AuthenticationFrame() {
        setTitle("Автентифікація");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Ім'я користувача:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = getjButton();
        panel.add(loginButton);

        add(panel);
    }

    private JButton getjButton() {
        JButton loginButton = new JButton("Увійти");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (authenticateUser(username, password)) {
                    MainFrame mainFrame = new MainFrame(username);
                    mainFrame.setVisible(true);
                    dispose(); // Закриваємо вікно автентифікації після успішної автентифікації
                } else {
                    JOptionPane.showMessageDialog(AuthenticationFrame.this,
                            "Невірне ім'я користувача або пароль.", "Помилка", JOptionPane.ERROR_MESSAGE);
                }
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
                    return true; // Користувача знайдено і пароль співпадає
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Користувача не знайдено або пароль не співпадає
    }
}
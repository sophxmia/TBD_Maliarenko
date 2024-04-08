package org.example.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class AuthenticationFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final String DATABASE_FILE = "src/maliarenko_database.csv";
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private int failedAttemptsCount = 0;

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
        passwordField.setTransferHandler(null); // Встановлення TransferHandler на null
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Блокування Ctrl + V (Ctrl + Insert)
                if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_V || e.getKeyCode() == KeyEvent.VK_INSERT)) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        });
        panel.add(passwordField);

        JButton loginButton = createLoginButton();
        panel.add(loginButton);

        add(panel);
    }

    private JButton createLoginButton() {
        JButton loginButton = new JButton("Увійти");
        loginButton.addActionListener(e -> attemptLogin());
        return loginButton;
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (authenticateUser(username, password)) {
            MainFrame mainFrame = new MainFrame(username, accessControlMethod);
            mainFrame.setVisible(true);
            dispose();
        } else {
            failedAttemptsCount++;
            if (failedAttemptsCount >= MAX_FAILED_ATTEMPTS) {
                // Блокування системи або користувача при досягненні максимальної кількості невдалих спроб
                JOptionPane.showMessageDialog(AuthenticationFrame.this,
                        "Досягнуто максимальну кількість невдалих спроб. Система буде заблокована.",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
                System.exit(0); // Або виконайте дії для блокування користувача
            } else {
                JOptionPane.showMessageDialog(AuthenticationFrame.this,
                        "Невірне ім'я користувача або пароль. Спроба " + failedAttemptsCount + " з " + MAX_FAILED_ATTEMPTS,
                        "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    // Перевірка чи введений пароль збігатися з поточним паролем користувача
                    if (parts[1].equals(password)) {
                        String lastPasswordChangeDate = parts[5]; // Отримання дати останньої зміни пароля
                        if (isPasswordStillValid(lastPasswordChangeDate)) {
                            failedAttemptsCount = 0; // Скидання лічильника невдалих спроб при успішній аутентифікації
                            return true; // Пароль ще актуальний
                        } else {
                            JOptionPane.showMessageDialog(AuthenticationFrame.this,
                                    "Пароль не актуальний. Будь ласка, змініть пароль і спробуйте ще раз.",
                                    "Помилка", JOptionPane.ERROR_MESSAGE);
                            return false; // Пароль не актуальний
                        }
                    } else {
                        // Невірний пароль
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Користувач не знайдений
        return false;
    }

    private boolean isPasswordStillValid(String lastPasswordChangeDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date lastChangeDate = dateFormat.parse(lastPasswordChangeDate);
            Date currentDate = new Date();

            // Кількість мілісекунд у 30 днях
            long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;

            // Перевірка, чи не минуло 30 днів з останньої зміни пароля
            return currentDate.getTime() - lastChangeDate.getTime() <= thirtyDaysInMillis; // Пароль ще актуальний чи ні
        } catch (ParseException e) {
            // Обробка помилки розбору дати
            e.printStackTrace();
            return false; // Якщо сталася помилка, повертаємо false
        }
    }
}
package org.example.main;

import javax.swing.*;

public class Main {
    private static String accessControlMethod;

    public static void main(String[] args) {
        // Запитуємо користувача про метод розмежування доступу
        accessControlMethod = askForAccessControlMethod();

        SwingUtilities.invokeLater(() -> {
            AuthenticationFrame authFrame = new AuthenticationFrame();
            authFrame.setVisible(true);
        });

    }

    private static String askForAccessControlMethod() {
        String[] options = {"Мандатне", "Дискреційне", "Рольове"};
        int choice = JOptionPane.showOptionDialog(null,
                "Будь ласка, виберіть метод розмежування доступу:",
                "Вибір методу розмежування доступу",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == -1) {
            // Користувач скасував вибір
            System.exit(0);
        }

        // Повертаємо обраний метод розмежування доступу
        return options[choice];
    }
}

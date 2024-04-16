package org.example.main;

import org.opencv.core.Core;

import javax.swing.*;

import static org.example.main.OpenCVExample.recognizeFace;

public class Main {
    private static String accessControlMethod;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.VERSION);
        if(recognizeFace()) {
            // Запитуємо користувача про метод розмежування доступу
            accessControlMethod = askForAccessControlMethod();

            SwingUtilities.invokeLater(() -> {
                AuthenticationFrame authFrame = new AuthenticationFrame(accessControlMethod);
                authFrame.setVisible(true);
            });
        }else {
            // Вивести повідомлення про невдалий вхід
            JOptionPane.showMessageDialog(null,
                    "Не вдалося розпізнати обличчя. Спробуйте ще раз.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
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

package org.example.main;

import javax.swing.JOptionPane;

public class PasswordUtils {

    public static void validatePasswordComplexity(boolean isComplex, String password) {
        // Перевірка на довжину паролю для складного паролю

        if (isComplex && password.length() < 8) {
            JOptionPane.showMessageDialog(null,
                    "Складний пароль повинен містити принаймні 8 символів.", "Помилка", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException();
        }
        // Перевірка на довжину паролю для простого паролю
        if (!isComplex && password.isEmpty()) {
            JOptionPane.showMessageDialog(null,
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
                JOptionPane.showMessageDialog(null,
                        "Складний пароль повинен містити символи з трьох різних наборів: великі літери, малі літери, цифри.",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
                throw new IllegalArgumentException();
            }
        }
    }
}


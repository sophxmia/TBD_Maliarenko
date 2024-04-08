package org.example.passwordCracker;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BruteForceDialog extends JDialog {
    private final JTextField lengthField;
    private final JCheckBox digitsCheckbox;
    private final JCheckBox specialCharactersCheckbox;
    private final JCheckBox lowercaseLettersCheckbox;
    private final JCheckBox uppercaseLettersCheckbox;

    public BruteForceDialog(String username) {
        setTitle("Підбір пароля для " + username);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 1));

        lengthField = new JTextField();
        panel.add(new JLabel("Довжина паролю:"));
        panel.add(lengthField);

        digitsCheckbox = new JCheckBox("Цифри");
        panel.add(digitsCheckbox);

        specialCharactersCheckbox = new JCheckBox("Спеціальні символи");
        panel.add(specialCharactersCheckbox);

        lowercaseLettersCheckbox = new JCheckBox("Малі літери");
        panel.add(lowercaseLettersCheckbox);

        uppercaseLettersCheckbox = new JCheckBox("Великі літери");
        panel.add(uppercaseLettersCheckbox);

        JButton startButton = new JButton("Почати підбір");
        startButton.addActionListener(e -> startBruteForce(username));
        panel.add(startButton);

        add(panel);
    }

    private void startBruteForce(String username) {
        // Отримання введених параметрів підбору пароля
        int length = Integer.parseInt(lengthField.getText());
        boolean includeDigits = digitsCheckbox.isSelected();
        boolean includeSpecialCharacters = specialCharactersCheckbox.isSelected();
        boolean includeLowercaseLetters = lowercaseLettersCheckbox.isSelected();
        boolean includeUppercaseLetters = uppercaseLettersCheckbox.isSelected();

        // Генерація списку всіх можливих символів для підбору
        List<Character> characterList = new ArrayList<>();
        if (includeDigits) {
            for (char digit = '0'; digit <= '9'; digit++) {
                characterList.add(digit);
            }
        }
        if (includeSpecialCharacters) {
            // Додати спеціальні символи
            char[] specialCharacters = "!@#$%^&*()-_=+[]{};:'\"\\|,.<>?/".toCharArray();
            for (char character : specialCharacters) {
                characterList.add(character);
            }
        }
        if (includeLowercaseLetters) {
            for (char letter = 'a'; letter <= 'z'; letter++) {
                characterList.add(letter);
            }
        }
        if (includeUppercaseLetters) {
            for (char letter = 'A'; letter <= 'Z'; letter++) {
                characterList.add(letter);
            }
        }

        // Початок процесу підбору пароля
        generatePasswords("", length, characterList);
    }

    private void generatePasswords(String prefix, int length, List<Character> characterList) {
        if (length == 0) {
            System.out.println(prefix); // Вивести знайдений пароль
            return;
        }

        for (char character : characterList) {
            generatePasswords(prefix + character, length - 1, characterList);
        }
    }
}


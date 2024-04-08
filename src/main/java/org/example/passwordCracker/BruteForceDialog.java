package org.example.passwordCracker;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        generatePasswords(username,"", length, characterList);
    }

    private void generatePasswords(String username, String prefix, int length, List<Character> characterList) {
        if (length == 0) {
            System.out.println(prefix);
            if (authenticateUser(username, prefix)) {
                JOptionPane.showMessageDialog(this, "Пароль для користувача " + username + " знайдений: " + prefix);
                dispose();
            }
            return;
        }

        for (char character : characterList) {
            generatePasswords(username, prefix + character, length - 1, characterList);
        }
    }

    private boolean authenticateUser(String username, String password) {
        String DATABASE_FILE = "src/maliarenko_database.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}


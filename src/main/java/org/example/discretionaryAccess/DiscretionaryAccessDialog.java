package org.example.discretionaryAccess;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DiscretionaryAccessDialog extends JDialog {
    private final String username;
    private final JCheckBox[] resourceCheckboxes;
    private final JComboBox<String>[] accessComboBoxes;
    private static final String RESOURCE_FILE = "src/resourses_discretionary.csv";

    public DiscretionaryAccessDialog(JFrame parent, String username) {
        super(parent, "Встановити дискреційний доступ", true);
        this.username = username;

        // Отримуємо список ресурсів з файлу або іншого джерела
        String[] resources = {"file1.txt", "file2.txt", "file3.txt", "file.exe", "image.bmp"};

        JPanel panel = new JPanel(new GridLayout(resources.length, 3));

        resourceCheckboxes = new JCheckBox[resources.length];
        accessComboBoxes = new JComboBox[resources.length];

        for (int i = 0; i < resources.length; i++) {
            resourceCheckboxes[i] = new JCheckBox(resources[i]);
            panel.add(resourceCheckboxes[i]);

            panel.add(new JLabel("Доступ:"));

            accessComboBoxes[i] = new JComboBox<>(new String[]{"No Access", "Read", "Read/Write", "Execute"});
            panel.add(accessComboBoxes[i]);
        }

        JButton saveButton = new JButton("Зберегти");
        saveButton.addActionListener(e -> {
            saveAccess();
            dispose();
        });

        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Внесіть зміни у ваш метод saveAccess ()
    private void saveAccess() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESOURCE_FILE, true))) {
            writer.write(username);
            for (int i = 0; i < resourceCheckboxes.length; i++) {
                if (resourceCheckboxes[i].isSelected()) {
                    String accessType = (String) accessComboBoxes[i].getSelectedItem();
                    String timeLimit = askForTimeLimit(); // Запитайте користувача про часове обмеження
                    writer.write("," + accessType + "|" + timeLimit); // Збережіть тип доступу та часове обмеження
                } else {
                    writer.write(",No Access");
                }
            }
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Дискреційний доступ для користувача " + username + " успішно встановлено.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Помилка під час запису до файлу дискреційного доступу.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Метод для запиту користувача про часове обмеження
    private String askForTimeLimit() {
        return JOptionPane.showInputDialog(this, "Введіть часове обмеження (ISO формат дати, наприклад, 2024-03-20):");
    }

}

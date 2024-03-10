package org.example.main;

import org.example.mandatoryAccess.MandatoryAccessControlSystem;
import org.example.discretionaryAccess.DiscretionaryAccessControlSystem;
import org.example.roleAccess.RoleAccessControlSystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ResourceWindow extends JFrame {

    private final String[] resourcePaths = {
            "C:/Users/marsh/Desktop/Uni/Технології безпечного доступу/TBD_Maliarenko/Data/file1.txt",
            "C:/Users/marsh/Desktop/Uni/Технології безпечного доступу/TBD_Maliarenko/Data/file2.txt",
            "C:/Users/marsh/Desktop/Uni/Технології безпечного доступу/TBD_Maliarenko/Data/file3.txt",
            "C:/Users/marsh/Desktop/Uni/Технології безпечного доступу/TBD_Maliarenko/Data/file.exe",
            "C:/Users/marsh/Desktop/Uni/Технології безпечного доступу/TBD_Maliarenko/Data/image.bmp"
    };

    private final String accessControlMethod;

    public ResourceWindow(String username, String accessControlMethod) {
        this.accessControlMethod = accessControlMethod;
        initializeFrame();
        addResourceButtons(username);
    }

    private void initializeFrame() {
        setTitle("Ресурси");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void addResourceButtons(String username) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        for (int i = 0; i < resourcePaths.length; i++) {
            String resourcePath = resourcePaths[i];
            JButton button = createResourceButton(resourcePath, i + 1, username);
            panel.add(button);
        }

        add(panel);
    }

    private JButton createResourceButton(String resourcePath, int resourceId, String username) {
        JButton button = new JButton("Ресурс " + resourceId);
        button.addActionListener(e -> openFile(resourcePath, username));
        return button;
    }

    private void openFile(String filePath, String username) {
        switch (accessControlMethod) {
            case "Мандатне" -> {
                MandatoryAccessControlSystem accessControlSystem = new MandatoryAccessControlSystem();
                if (accessControlSystem.hasAccess(username, filePath)) {
                    openFile(filePath);
                } else {
                    showAccessError();
                }
            }
            case "Дискреційне" -> {
                DiscretionaryAccessControlSystem accessControlSystem = new DiscretionaryAccessControlSystem();
            }
            case "Рольове" -> {
                RoleAccessControlSystem accessControlSystem = new RoleAccessControlSystem();
            }
            default ->
                    JOptionPane.showMessageDialog(this, "Непідтримуваний метод розмежування доступу", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFile(String filePath) {
        File file = new File(filePath);
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Помилка при відкритті файлу", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAccessError() {
        JOptionPane.showMessageDialog(this, "Недостатньо прав для доступу до цього ресурсу", "Помилка", JOptionPane.ERROR_MESSAGE);
    }
}


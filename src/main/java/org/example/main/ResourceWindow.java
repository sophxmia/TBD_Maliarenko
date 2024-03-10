package org.example.main;

import org.example.mandatoryAccess.MandatoryAccessControlSystem;

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

    public ResourceWindow(String username) {
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
        MandatoryAccessControlSystem mandatoryAccessControlSystem = new MandatoryAccessControlSystem();
        if (mandatoryAccessControlSystem.hasAccess(username, filePath)) {
            File file = new File(filePath);
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Помилка при відкритті файлу", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Недостатньо прав для доступу до цього ресурсу", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }
}

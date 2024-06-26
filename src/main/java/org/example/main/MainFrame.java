package org.example.main;

import org.example.passwordCracker.BruteForceDialog;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final String accessControlMethod;

    public MainFrame(String username, String accessControlMethod) {
        this.accessControlMethod = accessControlMethod;
        initializeFrame(username);
        addComponents(username);
    }

    private void initializeFrame(String username) {
        setTitle("TBD_Maliarenko - " + username);
        setSize(500, 500);
        setLocation(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
    }

    private void addComponents(String username) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JButton addUserButton = new JButton("Додати Користувача");
        addUserButton.addActionListener(e -> {
            AddUser addUser = new AddUser();
            addUser.setVisible(true);
        });
        panel.add(addUserButton);

        JButton addChangePasswordButton = new JButton("Змінити Пароль");
        addChangePasswordButton.addActionListener(e -> {
            ChangePassword changePassword = new ChangePassword();
            changePassword.setVisible(true);
        });
        panel.add(addChangePasswordButton);

        JButton openResourcesButton = new JButton("Відкрити ресурси");
        openResourcesButton.addActionListener(e -> {
            ResourceWindow resourceWindow = new ResourceWindow(username, accessControlMethod);
            resourceWindow.setVisible(true);
        });
        panel.add(openResourcesButton);

        JButton switchUserButton = new JButton("Змінити Користувача");
        switchUserButton.addActionListener(e -> {
            AuthenticationFrame authFrame = new AuthenticationFrame(accessControlMethod);
            authFrame.setVisible(true);
        });
        panel.add(switchUserButton);

        JButton bruteForceButton = new JButton("Підібрати пароль");
        bruteForceButton.addActionListener(e -> {
            BruteForceDialog bruteForceDialog = new BruteForceDialog(username);
            bruteForceDialog.setVisible(true);
        });
        panel.add(bruteForceButton);


        add(panel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu aboutMenu = new JMenu("Про автора");

        JMenuItem authorItem = new JMenuItem("Інформація про автора");
        authorItem.addActionListener(e -> JOptionPane.showMessageDialog(MainFrame.this,
                "Номер групи: BI-444\nПрізвище: Maliarenko\nІм'я: Sofiia"));
        aboutMenu.add(authorItem);

        menuBar.add(aboutMenu);
        return menuBar;
    }
}

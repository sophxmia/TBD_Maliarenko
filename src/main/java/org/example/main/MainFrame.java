package org.example.main;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(String username) {
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
        panel.setLayout(new GridLayout(2, 2));

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
            ResourceWindow resourceWindow = new ResourceWindow(username);
            resourceWindow.setVisible(true);
        });
        panel.add(openResourcesButton);

        JButton switchUserButton = new JButton("Змінити Користувача");
        switchUserButton.addActionListener(e -> {
            AuthenticationFrame authFrame = new AuthenticationFrame();
            authFrame.setVisible(true);
        });
        panel.add(switchUserButton);

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

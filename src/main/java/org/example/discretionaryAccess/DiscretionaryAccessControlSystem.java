package org.example.discretionaryAccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class DiscretionaryAccessControlSystem {
    private static final String RESOURCE_FILE = "resources_discretionary.csv";
    private final Map<String, Map<String, String>> accessMatrix;

    public DiscretionaryAccessControlSystem() {
        accessMatrix = new HashMap<>();
        loadAccessMatrix();
    }

    private void loadAccessMatrix() {
        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String user = parts[0];
                Map<String, String> resourceAccess = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    String[] access = parts[i].split("/");
                    resourceAccess.put(access[0], access[1]);
                }
                accessMatrix.put(user, resourceAccess);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasAccess(String username, String resourceName, String action) {
        Map<String, String> resourceAccess = accessMatrix.get(username);
        if (resourceAccess != null && resourceAccess.containsKey(resourceName)) {
            String permissions = resourceAccess.get(resourceName);
            return permissions.contains(action) && checkTimeConstraints(username, resourceName);
        }
        return false;
    }

    private boolean checkTimeConstraints(String username, String resourceName) {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = readStartTime(username, resourceName);
        LocalTime endTime = readEndTime(username, resourceName);

        // Перевірка чи час наразі потрапляє в обмеження часу доступу
        return (startTime == null || currentTime.isAfter(startTime)) && (endTime == null || currentTime.isBefore(endTime));
    }

    private LocalTime readStartTime(String username, String resourceName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    for (int i = 1; i < parts.length; i++) {
                        String[] resourceAccess = parts[i].split("/");
                        if (resourceAccess[0].equals(resourceName)) {
                            // Повертаємо початковий час доступу, якщо встановлено
                            return LocalTime.parse(resourceAccess[2]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Повертаємо null, якщо обмеження часу не встановлено
        return null;
    }

    private LocalTime readEndTime(String username, String resourceName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    for (int i = 1; i < parts.length; i++) {
                        String[] resourceAccess = parts[i].split("/");
                        if (resourceAccess[0].equals(resourceName)) {
                            // Повертаємо кінцевий час доступу, якщо встановлено
                            return LocalTime.parse(resourceAccess[3]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Повертаємо null, якщо обмеження часу не встановлено
        return null;
    }
}

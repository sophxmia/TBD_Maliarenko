package org.example.discretionaryAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiscretionaryAccessControlSystem {
    private static final String RESOURCE_FILE = "src/resourses_discretionary.csv";

    private final Map<String, Map<String, String>> accessMatrix;

    public DiscretionaryAccessControlSystem() {
        accessMatrix = loadAccessMatrix();
    }

    private Map<String, Map<String, String>> loadAccessMatrix() {
        Map<String, Map<String, String>> accessMatrix = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE_FILE))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("Файл resources_discretionary.csv порожній");
            }
            String[] resourceNames = headerLine.split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                Map<String, String> resourceAccess = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    resourceAccess.put(resourceNames[i].trim(), parts[i].trim());
                }
                accessMatrix.put(username, resourceAccess);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(accessMatrix);
        return accessMatrix;
    }

    public boolean hasAccess(String username, String filePath) {
        String resource = getResourceNameFromFilePath(filePath);
        Map<String, String> userAccess = accessMatrix.get(username);
        if (userAccess == null) {
            return false;
        }
        String access = userAccess.get(resource);
        System.out.println("Access for " + username + " to " + resource + ": " + access);
        if (access != null) {

            switch (access) {
                case "Read":
                    // Перевіряємо, чи є доступ на читання
                    return true;
                case "Read/Write":
                    // Перевіряємо, чи є доступ на читання та запис
                    return true;
                case "Execute":
                    // Перевіряємо, чи є доступ на виконання
                    return true;
                default:
                    // Перевіряємо, чи є обмеження на доступ
                    return false;
            }
        }
        return false;
    }

    private String getResourceNameFromFilePath(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }
}

package org.example.discretionaryAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DiscretionaryAccessControlSystem {
    private static final String RESOURCE_FILE = "src/resourses_discretionary.csv";

    private final Map<String, Map<String, ResourceAccess>> accessMatrix;

    public DiscretionaryAccessControlSystem() {
        accessMatrix = loadAccessMatrix();
    }

    private Map<String, Map<String, ResourceAccess>> loadAccessMatrix() {
        Map<String, Map<String, ResourceAccess>> accessMatrix = new HashMap<>();
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
                Map<String, ResourceAccess> resourceAccess = new HashMap<>();
                for (int i = 1; i < parts.length; i++) {
                    String[] accessParts = parts[i].trim().split("\\|");
                    String accessType = accessParts[0];
                    String timeLimit = accessParts.length > 1 ? accessParts[1] : null;
                    resourceAccess.put(resourceNames[i].trim(), new ResourceAccess(accessType, timeLimit));
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
        Map<String, ResourceAccess> userAccess = accessMatrix.get(username);
        if (userAccess == null) {
            return false;
        }
        ResourceAccess access = userAccess.get(resource);
        System.out.println("Access for " + username + " to " + resource + ": " + access.getAccessType() + " | Time limit: " + access.getTimeLimit());
        // Перевірка обмеження по часу
        if (access.getTimeLimit() != null && !isTimeLimitExpired(access.getTimeLimit())) {
            return false; // Часовий ліміт сплив
        }
        return switch (access.getAccessType()) {
            case "Read" -> true;
            case "Read/Write" -> true;
            case "Execute" -> true;
            default -> false;
        };
    }

    private boolean isTimeLimitExpired(String timeLimit) {
        if (timeLimit == null || timeLimit.isEmpty()) {
            // Якщо часовий ліміт не вказаний, вважаємо, що він не сплив
            return false;
        }

        LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(timeLimit, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            // Неправильний формат дати, тому не можемо його перевірити
            return false;
        }

        // Перевіряємо, чи часовий ліміт для доступу accessType ще не сплив
        LocalDate currentDate = LocalDate.now();
        return currentDate.isBefore(expiryDate) || currentDate.isEqual(expiryDate);
    }


    private String getResourceNameFromFilePath(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }
}

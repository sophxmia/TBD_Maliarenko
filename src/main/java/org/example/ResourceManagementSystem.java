package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResourceManagementSystem {
    private List<Resource> resources;

    public ResourceManagementSystem() {
        loadResources();
    }

    private void loadResources() {
        resources = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources.csv"))) {
            // Читаємо перший рядок для отримання імен ресурсів та їх рівнів конфіденційності
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("Файл resources.csv порожній");
            }
            String[] resourceNames = headerLine.split(",");
            // Читаємо наступні рядки з даними доступу для кожного користувача
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                for (int i = 1; i < parts.length; i++) {
                    String access = parts[i].trim(); // Отримуємо доступ
                    // Створюємо об'єкт ресурсу з іменем та рівнем доступу
                    Resource resource = new Resource(resourceNames[i].trim(), access);
                    resources.add(resource); // Додаємо ресурс у список
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getResourceAccessLevel(String resourceName) {
        for (Resource resource : resources) {
            if (resource.resourceName().equals(resourceName)) {
                return resource.accessLevel();
            }
        }
        return null;
    }
}


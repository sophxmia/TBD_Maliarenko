package org.example.roleAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class RoleAccessControlSystem {
    private final Map<String, Map<String, Boolean>> roleAccessMatrix;

    public RoleAccessControlSystem() {
        roleAccessMatrix = loadRoleAccessMatrix();
    }

    private Map<String, Map<String, Boolean>> loadRoleAccessMatrix() {
        // Завантаження матриці доступу ролей з деякого джерела
        // В цьому випадку, ми просто створимо тестову матрицю
        Map<String, Map<String, Boolean>> roleAccessMatrix = new HashMap<>();

        // Роль Адміністратора
        Map<String, Boolean> adminAccess = new HashMap<>();
        adminAccess.put("file1.txt", true);
        adminAccess.put("file2.txt", true);
        adminAccess.put("file3.txt", true);
        adminAccess.put("file.exe", true);
        adminAccess.put("image.bmp", true);
        roleAccessMatrix.put("Адміністратор", adminAccess);

        // Роль Редактора
        Map<String, Boolean> editorAccess = new HashMap<>();
        editorAccess.put("file1.txt", true);
        editorAccess.put("file2.txt", true);
        editorAccess.put("file3.txt", true);
        editorAccess.put("file.exe", false); // Редактори не мають доступу до виконуваних файлів
        editorAccess.put("image.bmp", true);
        roleAccessMatrix.put("Редактор", editorAccess);

        // Роль Користувача
        Map<String, Boolean> userAccess = new HashMap<>();
        userAccess.put("file1.txt", true);
        userAccess.put("file2.txt", true);
        userAccess.put("file3.txt", true);
        userAccess.put("file.exe", false); // Користувачі також не мають доступу до виконуваних файлів
        userAccess.put("image.bmp", false); // Користувачі також не мають доступу до фото
        roleAccessMatrix.put("Користувач", userAccess);

        return roleAccessMatrix;
    }

    public boolean hasAccess(String username, String resource) {
        // Отримуємо роль користувача
        String role = getRole(username);
        if (role == null) {
            return false; // Користувач не має призначеної ролі
        }

        // Перевіряємо доступ до ресурсу на основі ролі користувача
        Map<String, Boolean> resourceAccess = roleAccessMatrix.get(role);
        if (resourceAccess == null) {
            return false; // Роль не знайдена у матриці доступу
        }

        if (role.equals("Користувач")) {
            LocalTime currentTime = LocalTime.now();
            LocalTime startTime = LocalTime.of(9, 0); // Початок робочого дня
            LocalTime endTime = LocalTime.of(18, 0); // Кінець робочого дня
            if (currentTime.isBefore(startTime) || currentTime.isAfter(endTime)) {
                return false; // Поза робочим часом
            }
        }

        String resourceName = getResourceNameFromFilePath(resource);
        Boolean access = resourceAccess.get(resourceName);
        return access != null && access; // Повертаємо доступність ресурсу для користувача
    }

    private String getResourceNameFromFilePath(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    private String getRole(String username) {
        String csvFile = "C:/Users/marsh/Desktop/8 sem/Технології безпечного доступу/TBD_Maliarenko/TBD_Maliarenko/src/roleAccess.csv";
        String line;
        String delimiter = ",";
        String role = "Користувач";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(delimiter);
                if (parts.length == 2 && parts[0].equals(username)) {
                    role = parts[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(role);
        return role;
    }
}

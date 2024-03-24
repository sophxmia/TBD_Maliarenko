package org.example.roleAccess;

import java.io.File;
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
        String resourceName = getResourceNameFromFilePath(resource);
        Boolean access = resourceAccess.get(resourceName);
        return access != null && access; // Повертаємо доступність ресурсу для користувача
    }

    private String getResourceNameFromFilePath(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    private String getRole(String username) {
        return switch (username) {
            case "Maliarenko_3", "Maliarenko_4" -> "Редактор";
            case "Maliarenko_5" -> "Адміністратор";
            default -> "Користувач";
        };
    }
}

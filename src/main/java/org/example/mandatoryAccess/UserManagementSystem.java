package org.example.mandatoryAccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManagementSystem {
    private static final String DATABASE_FILE = "src/maliarenko_database.csv";

    private List<User> users;

    public UserManagementSystem(){
        loadUsers();
    }

    private void loadUsers() {
        users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 4) { // Перевірка на кількість елементів
                    users.add(new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getUserAccessLevel(String username){
        for (User user : users){
            if(user.username().equals(username)){
                return user.accessLevel();
            }
        }
        return null; // Користувач не знайдений
    }
}

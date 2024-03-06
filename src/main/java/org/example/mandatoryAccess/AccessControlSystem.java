package org.example.mandatoryAccess;

import java.io.File;

public class AccessControlSystem {
    private final UserManagementSystem userManagementSystem;
    private final ResourceManagementSystem resourceManagementSystem;

    public AccessControlSystem(){
        this.userManagementSystem = new UserManagementSystem();
        this.resourceManagementSystem =  new ResourceManagementSystem();
    }

    public boolean checkAccess(String username, String resourceName) {
        String userAccessLevel = userManagementSystem.getUserAccessLevel(username);
        String resourceAccessLevel = resourceManagementSystem.getResourceAccessLevel(resourceName);

        if (userAccessLevel == null || resourceAccessLevel == null) {
            System.out.println("Недостатньо інформації для перевірки доступу.");
            return false;
        }

        return switch (userAccessLevel) {
            case "Низький" -> isLowAccessAllowed(resourceName);
            case "Середній" -> isMediumAccessAllowed(resourceName);
            case "Високий" -> isHighAccessAllowed();
            default -> false;
        };
    }

    private boolean isLowAccessAllowed(String resourceName) {
        return resourceName.equals("file1.txt") || resourceName.equals("image.bmp");
    }

    private boolean isMediumAccessAllowed(String resourceName) {
        return resourceName.equals("file1.txt") || resourceName.equals("file2.txt") || resourceName.equals("file.exe");
    }

    private boolean isHighAccessAllowed() {
        return true;
    }


    public boolean hasAccess(String username, String filePath) {
        String resourceName = getResourceNameFromFilePath(filePath);
        return checkAccess(username, resourceName);
    }

    private String getResourceNameFromFilePath(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }
}

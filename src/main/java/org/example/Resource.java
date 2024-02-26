package org.example;

public class Resource {
    private final String resourceName;
    private final String accessLevel;

    public Resource(String resourceName, String accessLevel) {
        this.resourceName = resourceName;
        this.accessLevel = accessLevel;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getAccessLevel() {
        return accessLevel;
    }
}


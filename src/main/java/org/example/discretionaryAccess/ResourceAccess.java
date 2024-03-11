package org.example.discretionaryAccess;

public class ResourceAccess {
    private final String accessType;
    private final String timeLimit;

    public ResourceAccess(String accessType, String timeLimit) {
        this.accessType = accessType;
        this.timeLimit = timeLimit;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getTimeLimit() {
        return timeLimit;
    }
}

package org.example.mandatoryAccess;

public record User(String username, String password, String password_complexity, String accessLevel) {
}


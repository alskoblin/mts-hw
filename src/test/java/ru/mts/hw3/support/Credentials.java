package ru.mts.hw3.support;

public final class Credentials {

    private static final String USERNAME_VAR = "PLAYGROUND_USERNAME";
    private static final String PASSWORD_VAR = "PLAYGROUND_PASSWORD";

    private Credentials() {
    }

    public static String username() {
        return required(USERNAME_VAR);
    }

    public static String password() {
        return required(PASSWORD_VAR);
    }

    public static String passwordWithFlippedCase() {
        String password = password();
        StringBuilder flipped = new StringBuilder(password.length());
        for (char symbol : password.toCharArray()) {
            if (Character.isUpperCase(symbol)) {
                flipped.append(Character.toLowerCase(symbol));
            } else if (Character.isLowerCase(symbol)) {
                flipped.append(Character.toUpperCase(symbol));
            } else {
                flipped.append(symbol);
            }
        }
        return flipped.toString();
    }

    private static String required(String variable) {
        String value = System.getenv(variable);
        if (value == null || value.isBlank()) {
            value = System.getProperty(variable);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Не задана переменная окружения " + variable + ". "
                            + "Локально: export " + USERNAME_VAR + "=... " + PASSWORD_VAR + "=...; "
                            + "в CI: Settings → Secrets and variables → Actions.");
        }
        return value;
    }
}

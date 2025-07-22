package com.monivue.util;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import java.util.Properties;

public class TokenPersistence {

    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".monivue";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "config.properties";
    private static final String TOKEN_KEY = "api_token";

    static {
        new File(CONFIG_DIR).mkdirs(); // Ensure config directory exists
    }

    public static void saveToken(String token) {
        Properties props = new Properties();
        props.setProperty(TOKEN_KEY, token);
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Monivue Config");
        } catch (IOException e) {
            System.err.println("⚠️ Failed to save token: " + e.getMessage());
        }
    }

    public static Optional<String> loadToken() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return Optional.empty();

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
            String token = props.getProperty(TOKEN_KEY);
            return token == null || token.isBlank() ? Optional.empty() : Optional.of(token);
        } catch (IOException e) {
            System.err.println("⚠️ Failed to load token: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void resetToken() {
        try {
            Files.deleteIfExists(Paths.get(CONFIG_FILE));
        } catch (IOException e) {
            System.err.println("⚠️ Failed to reset token: " + e.getMessage());
        }
    }
}

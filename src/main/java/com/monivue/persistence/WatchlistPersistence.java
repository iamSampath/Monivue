package com.monivue.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistPersistence {

    private static final Path WATCHLIST_FILE = getAppDataPath().resolve("watchlist.json");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Path getAppDataPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String basePath;

        if (os.contains("win")) {
            basePath = System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            basePath = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            basePath = System.getProperty("user.home") + "/.monivue";
        }

        Path path = Paths.get(basePath, "Monivue");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("⚠️ Failed to create app data directory: " + e.getMessage());
        }
        return path;
    }

    public static void save(List<String> watchlist) {
        try {
            String json = objectMapper.writeValueAsString(watchlist);
            Files.write(WATCHLIST_FILE, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("⚠️ Failed to save watchlist: " + e.getMessage());
        }
    }

    public static List<String> load() {
        if (!Files.exists(WATCHLIST_FILE)) {
            return new ArrayList<>();
        }
        try {
            byte[] jsonBytes = Files.readAllBytes(WATCHLIST_FILE);
            return objectMapper.readValue(jsonBytes,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (IOException e) {
            System.err.println("⚠️ Failed to load watchlist: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void clear() {
        try {
            Files.deleteIfExists(WATCHLIST_FILE); // ✅ Correct: WATCHLIST_FILE is already a Path
        } catch (IOException e) {
            System.err.println("⚠️ Failed to clear watchlist: " + e.getMessage());
        }
    }
}

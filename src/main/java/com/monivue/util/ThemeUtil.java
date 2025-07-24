package com.monivue.util;

import javafx.scene.Scene;

import java.io.InputStream;
import java.util.prefs.Preferences;

public class ThemeUtil {

    private static final String THEME_PREF_KEY = "MONIVUE_THEME_IS_DARK";

    private static final String LIGHT_THEME = "/css/light.css";
    private static final String DARK_THEME = "/css/dark.css";
    private static final String SPACE_GROTESK = "/fonts/SpaceGrotesk-Regular.ttf";
    private static final String DM_SANS = "/fonts/DMSans-Regular.ttf";
        private static final String SPACE_GROTESK_BOLD = "/fonts/SpaceGrotesk-Bold.ttf";
    private static final String DM_SANS_BOLD = "/fonts/DMSans-Bold.ttf";

    public static void applyTheme(Scene scene, boolean isDark) {
        scene.getStylesheets().clear();
        String stylesheet = isDark ? DARK_THEME : LIGHT_THEME;
        scene.getStylesheets().add(ThemeUtil.class.getResource(stylesheet).toExternalForm());

        loadFont(SPACE_GROTESK);
        loadFont(DM_SANS);
        loadFont(DM_SANS_BOLD);
        loadFont(SPACE_GROTESK_BOLD);
    }

    public static boolean getSavedTheme() {
        Preferences prefs = Preferences.userNodeForPackage(ThemeUtil.class);
        return prefs.getBoolean(THEME_PREF_KEY, false); // false = light by default
    }

    public static void saveTheme(boolean isDark) {
        Preferences prefs = Preferences.userNodeForPackage(ThemeUtil.class);
        prefs.putBoolean(THEME_PREF_KEY, isDark);
    }

    private static void loadFont(String path) {
    try (InputStream fontStream = ThemeUtil.class.getResourceAsStream(path)) {
        if (fontStream != null) {
            javafx.scene.text.Font.loadFont(fontStream, 12);
        } else {
            System.err.println("Font not found: " + path);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}

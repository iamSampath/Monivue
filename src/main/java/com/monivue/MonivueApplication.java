package com.monivue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class MonivueApplication extends Application {

    private static MonivueApplication instance;
    private static final String TOKEN_KEY = "MONIVUE_API_TOKEN";

    static {
        try {
            File logDir = new File(System.getProperty("user.home"), "MonivueLogs");
            logDir.mkdirs();

            File errorLog = new File(logDir, "monivue-error.log");
            File outputLog = new File(logDir, "monivue-output.log");

            System.setErr(new PrintStream(new FileOutputStream(errorLog, true)));
            System.setOut(new PrintStream(new FileOutputStream(outputLog, true)));
        } catch (Exception e) {
            System.err.println("⚠️ Failed to redirect logs: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        String token = loadTokenFromPreferences();

        if (token == null || token.isBlank()) {
            token = promptForToken();
            if (token == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "API token is required. Exiting.");
                alert.showAndWait();
                Platform.exit();
                return;
            }
            saveTokenToPreferences(token);
        }

        System.setProperty(TOKEN_KEY, token);
        System.out.println("✅ Token loaded from preferences.");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Monivue");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            try (PrintWriter out = new PrintWriter(
                    new File(System.getProperty("user.home"), "MonivueLogs/monivue-error.log"))) {
                e.printStackTrace(out);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void init() {
        instance = this;
    }

    public static MonivueApplication getInstance() {
        return instance;
    }
    public static void main(String[] args) {
        System.setProperty("https.protocols", "TLSv1.2");
        launch(args);
    }

    private String loadTokenFromPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(MonivueApplication.class);
        return prefs.get(TOKEN_KEY, null);
    }

    private String promptForToken() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("API Token Required");
        dialog.setHeaderText("Enter your API token to use Monivue");
        dialog.setContentText("Token:");

        Optional<String> result = dialog.showAndWait();
        return result.filter(s -> !s.isBlank()).map(String::trim).orElse(null);
    }

    private void saveTokenToPreferences(String token) {
        Preferences prefs = Preferences.userNodeForPackage(MonivueApplication.class);
        prefs.put(TOKEN_KEY, token);
    }

    public static void clearSavedToken() {
        Preferences prefs = Preferences.userNodeForPackage(MonivueApplication.class);
        prefs.remove(TOKEN_KEY);
    }
}

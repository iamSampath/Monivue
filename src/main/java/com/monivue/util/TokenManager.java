package com.monivue.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class TokenManager {

    public static String getOrPromptToken() {
        String token = System.getenv("MONIVUE_API_TOKEN");

        if (token == null || token.isBlank()) {
            token = System.getProperty("MONIVUE_API_TOKEN");
        }

        if ((token == null || token.isBlank())) {
            token = TokenPersistence.loadToken().orElse(null);
            if (token != null) {
                System.setProperty("MONIVUE_API_TOKEN", token); // cache in session
            }
        }

        if (token == null || token.isBlank()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("API Token Required");
            dialog.setHeaderText("Enter your API token to use Monivue");
            dialog.setContentText("Token:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().isBlank()) {
                token = result.get().trim();
                System.setProperty("MONIVUE_API_TOKEN", token);
                TokenPersistence.saveToken(token);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "API token is required. Exiting.");
                alert.showAndWait();
                Platform.exit();
                return null;
            }
        }

        return token;
    }

    public static void resetToken() {
        TokenPersistence.resetToken();
        System.clearProperty("MONIVUE_API_TOKEN"); // This is crucial!

        com.monivue.persistence.WatchlistPersistence.clear();
    }
}

package com.monivue.util;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class TokenPrompt {

    private static String token = null;

    public static String getToken() {
        if (token != null) return token;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("API Token Required");
        dialog.setHeaderText("Enter your TwelveData API Token");
        dialog.setContentText("Token:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isBlank()) {
            token = result.get().trim();
            System.setProperty("MONIVUE_API_TOKEN", token); // optionally set it for global access
            return token;
        } else {
            System.err.println("No token provided. Exiting.");
            System.exit(1);
            return null; // unreachable, but for compiler
        }
    }
}

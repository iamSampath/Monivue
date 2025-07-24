package com.monivue.controller;

import com.monivue.model.StockQuote;
import com.monivue.persistence.WatchlistPersistence;
import com.monivue.service.StockApiService;
import com.monivue.MonivueApplication;
import com.monivue.util.ThemeUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.Scene;

public class DashboardController {

    public static class IntervalOption {
        private final String label;
        private final int seconds;

        public IntervalOption(String label, int seconds) {
            this.label = label;
            this.seconds = seconds;
        }

        public String getLabel() {
            return label;
        }

        public int getSeconds() {
            return seconds;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @FXML
    private ToggleButton themeToggle;
    @FXML
    private TextField symbolInput;
    @FXML
    private Button addButton;
    @FXML
    private ComboBox<IntervalOption> intervalSelector;

    @FXML
    private TableView<StockQuote> stockTable;
    @FXML
    private TableColumn<StockQuote, String> symbolColumn;
    @FXML
    private TableColumn<StockQuote, String> nameColumn;
    @FXML
    private TableColumn<StockQuote, Number> priceColumn;
    @FXML
    private TableColumn<StockQuote, Number> changeColumn;
    @FXML
    private Label lastRefreshLabel;

    private final ObservableList<StockQuote> watchlist = FXCollections.observableArrayList();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Timer refreshTimer;
    private int refreshIntervalSeconds = 60;

    @FXML
    public void initialize() {

        boolean isDark = ThemeUtil.getSavedTheme();
        themeToggle.setSelected(isDark);
        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Apply saved theme to the scene once it's ready
        Platform.runLater(() -> {
            Scene scene = themeToggle.getScene();
            if (scene != null) {
                ThemeUtil.applyTheme(scene, isDark);
            }
        });

        intervalSelector.getItems().addAll(
                new IntervalOption("OFF", 0),
                new IntervalOption("30 sec", 30),
                new IntervalOption("1 min", 60),
                new IntervalOption("2 min", 120),
                new IntervalOption("5 min", 300));
        intervalSelector.setValue(intervalSelector.getItems().get(1)); // default to 1 min

        intervalSelector.setOnAction(e -> {
            IntervalOption selected = intervalSelector.getValue();
            if (selected != null) {
                refreshIntervalSeconds = selected.getSeconds();

                if (refreshIntervalSeconds > 0) {
                    startAutoRefresh(); // normal refresh
                } else {
                    if (refreshTimer != null) {
                        refreshTimer.cancel(); // cancel timer if OFF
                    }
                }
            }
        });

        // Add toggle listener
        themeToggle.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            ThemeUtil.saveTheme(isNowSelected);
            Scene scene = themeToggle.getScene();
            if (scene != null) {
                ThemeUtil.applyTheme(scene, isNowSelected);
            } else {
                System.out.println("Scene is null during toggle.");
            }
        });

        symbolColumn.setCellValueFactory(cell -> cell.getValue().symbolProperty());
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty());
        changeColumn.setCellValueFactory(cell -> cell.getValue().changePercentProperty());

        stockTable.setItems(watchlist);
        List<String> savedSymbols = WatchlistPersistence.load();
        for (String symbol : savedSymbols) {
            StockApiService.fetchStock(symbol).ifPresent(watchlist::add);
        }
        startAutoRefresh();
    }

    @FXML
    private void handleResetToken() {
        // Clear the stored token from preferences
        MonivueApplication.clearSavedToken();

        // Clear cached data if needed
        WatchlistPersistence.clear();

        // Inform user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Token Reset");
        alert.setHeaderText(null);
        alert.setContentText("API token has been reset. The app will now close.");
        alert.showAndWait();

        // Exit app so token prompt is triggered on next launch
        Platform.exit();
    }

    @FXML
    public void handleAddSymbol() {
        String symbol = symbolInput.getText().trim().toUpperCase();
        if (symbol.isEmpty()) {
            showAlert("Input Error", "Please enter a valid stock symbol.");
            return;
        }

        boolean alreadyExists = watchlist.stream()
                .anyMatch(stock -> stock.getSymbol().equalsIgnoreCase(symbol));
        if (alreadyExists) {
            showAlert("Duplicate", "This symbol is already in your watchlist.");
            return;
        }

        Optional<StockQuote> result = StockApiService.fetchStock(symbol);
        result.ifPresentOrElse(
                stock -> {
                    watchlist.add(stock);
                    WatchlistPersistence.save(
                            watchlist.stream()
                                    .map(StockQuote::getSymbol)
                                    .toList());

                    updateLastRefreshTime();
                },
                () -> showAlert("API Error", "Could not fetch data for: " + symbol));

        symbolInput.clear();
    }

    private void refreshAllStocks() {
        for (int i = 0; i < watchlist.size(); i++) {
            StockQuote current = watchlist.get(i);
            StockApiService.fetchStock(current.getSymbol()).ifPresent(updated -> {
                current.setPrice(updated.getPrice());
                current.setChangePercent(updated.getChangePercent());
            });
        }
        stockTable.refresh();
        updateLastRefreshTime();
    }

    private void startAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }

        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> refreshAllStocks());
            }
        }, 0, refreshIntervalSeconds * 1000L);
    }

    private void updateLastRefreshTime() {
        String now = LocalDateTime.now().format(timeFormatter);
        lastRefreshLabel.setText("Last refresh: " + now);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

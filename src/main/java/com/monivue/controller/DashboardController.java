package com.monivue.controller;

import com.monivue.model.StockQuote;
import com.monivue.persistence.WatchlistPersistence;
import com.monivue.service.StockApiService;
import com.monivue.MonivueApplication;
import com.monivue.util.ThemeUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.HostServices;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javafx.util.Duration;


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
    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<StockQuote, Boolean> favoriteColumn;

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
        priceColumn.setCellFactory(col -> new TableCell<>() {
    @Override
    protected void updateItem(Number price, boolean empty) {
        super.updateItem(price, empty);
        if (empty || price == null) {
            setText(null);
            setGraphic(null);
            setStyle("");
        } else {
            StockQuote stock = getTableView().getItems().get(getIndex());
            double currentPrice = price.doubleValue();
            double previous = stock.getPreviousPrice();

            String arrow = "";
            String color = "-fx-text-fill: inherit;";
            if (!Double.isNaN(previous)) {
                if (currentPrice > previous) {
                    arrow = " 🔼";
                    color = "-fx-text-fill: #10B981;";
                    animateFlash(this, "#10B98155");
                } else if (currentPrice < previous) {
                    arrow = " 🔽";
                    color = "-fx-text-fill: #EF4444;";
                    animateFlash(this, "#EF444455");
                }
            }

            setText(String.format("%.2f%s", currentPrice, arrow));
            setStyle(color);
        }
    }

    private void animateFlash(TableCell<?, ?> cell, String flashColor) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(cell.backgroundProperty(), new Background(
                new BackgroundFill(Color.web(flashColor), null, null)))),
            new KeyFrame(Duration.seconds(0.5), new KeyValue(cell.backgroundProperty(), Background.EMPTY))
        );
        timeline.play();
    }
});


        changeColumn.setCellValueFactory(cell -> cell.getValue().changePercentProperty());

        FilteredList<StockQuote> filteredList = new FilteredList<>(watchlist, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal == null ? "" : newVal.toLowerCase();
            filteredList.setPredicate(stock -> stock.getSymbol().toLowerCase().contains(query) ||
                    stock.getName().toLowerCase().contains(query));
        });

        SortedList<StockQuote> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(stockTable.comparatorProperty());
        stockTable.setItems(sortedList);

        favoriteColumn.setCellValueFactory(cell -> cell.getValue().favoriteProperty());

        favoriteColumn.setCellFactory(col -> new TableCell<>() {
            private final ToggleButton starButton = new ToggleButton();

            {
                starButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand;");
                starButton.setOnAction(e -> {
                    StockQuote stock = getTableView().getItems().get(getIndex());
                    boolean fav = !stock.isFavorite();
                    stock.setFavorite(fav);
                    updateStar(fav);

                    // ✅ Persist favorites
                    WatchlistPersistence.save(watchlist);
                });
            }

            private void updateStar(boolean fav) {
                starButton.setText(fav ? "★" : "☆");
            }

            @Override
            protected void updateItem(Boolean fav, boolean empty) {
                super.updateItem(fav, empty);
                if (empty || fav == null) {
                    setGraphic(null);
                } else {
                    updateStar(fav);
                    setGraphic(starButton);
                }
            }
        });

        stockTable.setRowFactory(table -> {
            TableRow<StockQuote> row = new TableRow<>() {
                @Override
                protected void updateItem(StockQuote item, boolean empty) {
                    super.updateItem(item, empty);
                    getStyleClass().removeAll("row-gain", "row-loss", "row-inactive");

                    if (empty || item == null) {
                        setStyle("");
                        setContextMenu(null);
                        return;
                    }

                    // 🎨 Row coloring
                    if (!item.isActive()) {
                        getStyleClass().add("row-inactive");
                    } else if (item.getChangePercent() >= 0) {
                        getStyleClass().add("row-gain");
                    } else {
                        getStyleClass().add("row-loss");
                    }

                    // 📜 Context menu
                    MenuItem toggleFavorite = new MenuItem(item.isFavorite() ? "★ Unfavorite" : "☆ Favorite");
                    toggleFavorite.setOnAction(e -> {
                        item.setFavorite(!item.isFavorite());
                        WatchlistPersistence.save(watchlist);
                        stockTable.refresh();
                    });

                    MenuItem removeItem = new MenuItem("🗑 Remove from Watchlist");
                    removeItem.setOnAction(e -> {
                        watchlist.remove(item);
                        WatchlistPersistence.save(watchlist);
                    });

                    MenuItem setAlert = new MenuItem("🔔 Set Price Alert...");
                    setAlert.setOnAction(e -> showAlertDialog(item));

                    MenuItem openYahoo = new MenuItem("🌐 View on Yahoo Finance");
                    openYahoo.setOnAction(e -> getHostServices()
                            .showDocument("https://finance.yahoo.com/quote/" + item.getSymbol()));

                    ContextMenu contextMenu = new ContextMenu(toggleFavorite, removeItem, setAlert, openYahoo);
                    setContextMenu(contextMenu);

                    // ✅ ADD THIS to force show context menu reliably
                    setOnContextMenuRequested(event -> {
                        if (!isEmpty() && getContextMenu() != null) {
                            getContextMenu().show(this, event.getScreenX(), event.getScreenY());
                        }
                    });
                }
            };
            return row;
        });

        // Load watchlist from storage
        List<StockQuote> saved = WatchlistPersistence.load();
        watchlist.addAll(saved);

        // Refresh with latest data but keep favorites
        for (StockQuote current : watchlist) {
            StockApiService.fetchStock(current.getSymbol()).ifPresent(updated -> {
                current.setPrice(updated.getPrice());
                current.setChangePercent(updated.getChangePercent());
            });
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
    private void handleManualRefresh() {
        refreshAllStocks();
    }

    @FXML
    private void handleExportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Watchlist");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("watchlist.csv");

        File file = fileChooser.showSaveDialog(symbolInput.getScene().getWindow());
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Symbol,Name,Price,Change %,Favorite\n");
                for (StockQuote stock : watchlist) {
                    writer.write(String.format("%s,%s,%.2f,%.2f,%s\n",
                            stock.getSymbol(),
                            stock.getName(),
                            stock.getPrice(),
                            stock.getChangePercent(),
                            stock.isFavorite() ? "Yes" : "No"));
                }
                showInfo("Export Successful", "Watchlist exported to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Export Failed", "Could not save CSV file:\n" + e.getMessage());
            }
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
                    WatchlistPersistence.save(watchlist);

                    updateLastRefreshTime();
                },
                () -> showAlert("API Error", "Could not fetch data for: " + symbol));

        symbolInput.clear();
    }

    private void refreshAllStocks() {
        for (int i = 0; i < watchlist.size(); i++) {
            StockQuote current = watchlist.get(i);
            StockApiService.fetchStock(current.getSymbol()).ifPresent(updated -> {
                current.setPreviousPrice(current.getPrice()); // Store before update
                current.setPrice(updated.getPrice());
                current.setChangePercent(updated.getChangePercent());

                // 🔔 Check thresholds AFTER updating
                if (!Double.isNaN(current.getAlertAbove()) && current.getPrice() >= current.getAlertAbove()) {
                    showPriceAlert(current, "📈 Price is above $" + current.getAlertAbove());
                }
                if (!Double.isNaN(current.getAlertBelow()) && current.getPrice() <= current.getAlertBelow()) {
                    showPriceAlert(current, "📉 Price is below $" + current.getAlertBelow());
                }

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

    private HostServices getHostServices() {
        return MonivueApplication.getInstance().getHostServices();
    }

    private void showPriceAlert(StockQuote stock, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Price Alert - " + stock.getSymbol());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertDialog(StockQuote stock) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Set Alert for " + stock.getSymbol());

        Label aboveLabel = new Label("Alert if price is ABOVE:");
        TextField aboveField = new TextField(
                !Double.isNaN(stock.getAlertAbove()) ? String.valueOf(stock.getAlertAbove()) : "");

        Label belowLabel = new Label("Alert if price is BELOW:");
        TextField belowField = new TextField(
                !Double.isNaN(stock.getAlertBelow()) ? String.valueOf(stock.getAlertBelow()) : "");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.addRow(0, aboveLabel, aboveField);
        grid.addRow(1, belowLabel, belowField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    String above = aboveField.getText().trim();
                    String below = belowField.getText().trim();

                    stock.setAlertAbove(above.isEmpty() ? Double.NaN : Double.parseDouble(above));
                    stock.setAlertBelow(below.isEmpty() ? Double.NaN : Double.parseDouble(below));

                    WatchlistPersistence.save(watchlist);
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Enter valid numbers for price alerts.");
                }
            }
        });
    }

}

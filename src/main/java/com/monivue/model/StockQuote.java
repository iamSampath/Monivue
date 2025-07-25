package com.monivue.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import javafx.beans.property.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockQuote implements Serializable {
    private final StringProperty symbol = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final DoubleProperty changePercent = new SimpleDoubleProperty();
    private final BooleanProperty favorite = new SimpleBooleanProperty(false);

    private Double alertAbove = Double.NaN;
    private Double alertBelow = Double.NaN;
    private double previousPrice = Double.NaN;

    public StockQuote() {
    }

    public StockQuote(String symbol, String name, double price, double changePercent) {
        this.symbol.set(symbol);
        this.name.set(name);
        this.price.set(price);
        this.changePercent.set(changePercent);
    }

    @JsonProperty("alertAbove")
    public Double getAlertAbove() {
        return alertAbove;
    }

    public void setAlertAbove(Double alertAbove) {
        this.alertAbove = alertAbove;
    }

    @JsonProperty("alertBelow")
    public Double getAlertBelow() {
        return alertBelow;
    }

    public void setAlertBelow(Double alertBelow) {
        this.alertBelow = alertBelow;
    }

    public String getSymbol() {
        return symbol.get();
    }

    public void setSymbol(String value) {
        symbol.set(value);
    }

    public StringProperty symbolProperty() {
        return symbol;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double value) {
        price.set(value);
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public double getChangePercent() {
        return changePercent.get();
    }

    public void setChangePercent(double value) {
        changePercent.set(value);
    }

    public DoubleProperty changePercentProperty() {
        return changePercent;
    }

    public boolean isActive() {
        return true;
    }

    public BooleanProperty favoriteProperty() {
        return favorite;
    }

    public boolean isFavorite() {
        return favorite.get();
    }

    public void setFavorite(boolean favorite) {
        this.favorite.set(favorite);
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(double previousPrice) {
        this.previousPrice = previousPrice;
    }
}

package com.monivue.model;

import javafx.beans.property.*;

public class StockQuote {
    private final StringProperty symbol = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final DoubleProperty changePercent = new SimpleDoubleProperty();

    public StockQuote() {}

    public StockQuote(String symbol, String name, double price, double changePercent) {
        this.symbol.set(symbol);
        this.name.set(name);
        this.price.set(price);
        this.changePercent.set(changePercent);
    }

    public String getSymbol() { return symbol.get(); }
    public void setSymbol(String value) { symbol.set(value); }
    public StringProperty symbolProperty() { return symbol; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public double getPrice() { return price.get(); }
    public void setPrice(double value) { price.set(value); }
    public DoubleProperty priceProperty() { return price; }

    public double getChangePercent() { return changePercent.get(); }
    public void setChangePercent(double value) { changePercent.set(value); }
    public DoubleProperty changePercentProperty() { return changePercent; }
}

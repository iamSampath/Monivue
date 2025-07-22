package com.monivue.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monivue.model.StockQuote;
import com.monivue.util.TokenPrompt;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Optional;

public class StockApiService {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String token;

    static {
        String temp = System.getenv("MONIVUE_API_TOKEN");

        if (temp == null || temp.isBlank()) {
            temp = System.getProperty("MONIVUE_API_TOKEN");
        }

        if (temp == null || temp.isBlank()) {
            temp = TokenPrompt.getToken();  // 👈 prompt user if still not found
            if (!temp.isBlank()) {
                System.setProperty("MONIVUE_API_TOKEN", temp);  // optional: store for runtime access
            }
        }

        token = temp;
    }

    private static final String BASE_URL = "https://api.twelvedata.com/quote?symbol=%s&apikey=%s";

    public static Optional<StockQuote> fetchStock(String symbol) {
        if (token == null || token.isBlank()) {
            System.err.println("❌ API token is missing. Set MONIVUE_API_TOKEN as env or system property.");
            return Optional.empty();
        }

        String url = String.format(BASE_URL, symbol, token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            JsonNode json = objectMapper.readTree(responseBody);

            if (json.has("status") && json.get("status").asText().equalsIgnoreCase("error")) {
                System.err.println("API Error: " + json.get("message").asText());
                return Optional.empty();
            }

            String name = json.path("name").asText("Unknown");
            String priceStr = json.path("close").asText("0");
            String changeStr = json.path("percent_change").asText("0");

            if (priceStr.equalsIgnoreCase("N/A") || changeStr.equalsIgnoreCase("N/A")) {
                System.err.println("Symbol " + symbol + " returned N/A values.");
                return Optional.empty();
            }

            double price = Double.parseDouble(priceStr);
            double change = Double.parseDouble(changeStr);

            return Optional.of(new StockQuote(symbol, name, price, change));

        } catch (IOException | InterruptedException | NumberFormatException e) {
            System.err.println("Error fetching data for: " + symbol);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}

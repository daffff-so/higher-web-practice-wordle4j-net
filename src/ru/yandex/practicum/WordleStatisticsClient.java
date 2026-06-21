package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WordleStatisticsClient {
    private static final String SERVER_URL = "http://localhost:8080/statistics";

    private final HttpClient client;
    private final PrintWriter log;

    public WordleStatisticsClient(PrintWriter log) {
        this.client = HttpClient.newHttpClient();
        this.log = log;
    }

    public boolean sendResult(PlayerResult result) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL))
                .header("Content-Type", "application/json;charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(result.toJson(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.println("Ответ сервера на POST: " + response.statusCode() + " " + response.body());
            log.flush();

            return response.statusCode() == 201;
        } catch (IOException | InterruptedException exception) {
            log.println("Не удалось отправить статистику: " + exception.getMessage());
            log.flush();

            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            return false;
        }
    }

    public String getTopPlayers() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.println("Ответ сервера на GET: " + response.statusCode() + " " + response.body());
            log.flush();

            if (response.statusCode() == 200) {
                return response.body();
            }

            return "[]";
        } catch (IOException | InterruptedException exception) {
            log.println("Не удалось получить рейтинг: " + exception.getMessage());
            log.flush();

            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            return "[]";
        }
    }
}
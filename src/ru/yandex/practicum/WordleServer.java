package ru.yandex.practicum;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WordleServer {
    private static final int PORT = 8080;
    private static final String STATISTICS_FILE = "statistics.json";
    private static final String SERVER_LOG_FILE = "wordle-server.log";

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter(SERVER_LOG_FILE, true))) {
            WordleServerStatisticLoader statisticLoader =
                    new WordleServerStatisticLoader(STATISTICS_FILE, log);

            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/statistics", exchange -> {
                try {
                    if ("POST".equals(exchange.getRequestMethod())) {
                        handlePost(exchange, statisticLoader, log);
                    } else if ("GET".equals(exchange.getRequestMethod())) {
                        handleGet(exchange, statisticLoader);
                    } else {
                        sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                    }
                } catch (RuntimeException exception) {
                    log.println("Ошибка обработки запроса: " + exception.getMessage());
                    exception.printStackTrace(log);
                    log.flush();

                    sendJson(exchange, 500, "{\"error\":\"Internal server error\"}");
                }
            });

            server.start();

            System.out.println("WordleServer запущен на порту " + PORT);
        } catch (IOException exception) {
            System.out.println("Не удалось запустить сервер: " + exception.getMessage());
        }
    }

    private static void handlePost(
            HttpExchange exchange,
            WordleServerStatisticLoader statisticLoader,
            PrintWriter log
    ) throws IOException {
        String body = readBody(exchange);
        PlayerResult result = PlayerResult.fromJson(body);

        statisticLoader.saveResult(result);

        log.println("Сохранён результат игрока: " + result.getNickname());
        log.flush();

        sendJson(exchange, 201, "{\"status\":\"created\"}");
    }

    private static void handleGet(
            HttpExchange exchange,
            WordleServerStatisticLoader statisticLoader
    ) throws IOException {
        String topJson = statisticLoader.getTopPlayersJson();
        sendJson(exchange, 200, topJson);
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);

        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
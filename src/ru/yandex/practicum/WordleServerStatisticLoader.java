package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class WordleServerStatisticLoader {
    private final Path statisticsFile;
    private final PrintWriter log;

    public WordleServerStatisticLoader(String fileName, PrintWriter log) {
        this.statisticsFile = Path.of(fileName);
        this.log = log;
    }

    public List<PlayerResult> loadResults() {
        if (!Files.exists(statisticsFile)) {
            return new ArrayList<>();
        }

        List<PlayerResult> results = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(statisticsFile, StandardCharsets.UTF_8)) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            String json = jsonBuilder.toString().trim();

            if (json.isEmpty() || json.equals("[]")) {
                return results;
            }

            String[] objects = json.split("\\},\\{");

            for (String object : objects) {
                String normalizedObject = object;

                if (!normalizedObject.startsWith("{")) {
                    normalizedObject = "{" + normalizedObject;
                }

                if (!normalizedObject.endsWith("}")) {
                    normalizedObject = normalizedObject + "}";
                }

                normalizedObject = normalizedObject
                        .replace("[", "")
                        .replace("]", "");

                results.add(PlayerResult.fromJson(normalizedObject));
            }

            return results;
        } catch (IOException | RuntimeException exception) {
            log.println("Ошибка при загрузке статистики: " + exception.getMessage());
            exception.printStackTrace(log);
            log.flush();

            return new ArrayList<>();
        }
    }

    public void saveResult(PlayerResult result) {
        List<PlayerResult> results = loadResults();
        results.add(result);
        saveAll(results);
    }

    private void saveAll(List<PlayerResult> results) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(statisticsFile, StandardCharsets.UTF_8))) {
            writer.println(toJsonArray(results));
        } catch (IOException exception) {
            log.println("Ошибка при сохранении статистики: " + exception.getMessage());
            exception.printStackTrace(log);
            log.flush();
        }
    }

    public String getTopPlayersJson() {
        List<PlayerResult> results = loadResults();

        Map<String, Integer> winsByPlayer = new HashMap<>();

        for (PlayerResult result : results) {
            winsByPlayer.put(
                    result.getNickname(),
                    winsByPlayer.getOrDefault(result.getNickname(), 0) + 1
            );
        }

        List<Map.Entry<String, Integer>> top = new ArrayList<>(winsByPlayer.entrySet());

        top.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey()));

        StringJoiner joiner = new StringJoiner(",", "[", "]");

        int limit = Math.min(10, top.size());

        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = top.get(i);

            String json = "{"
                    + "\"nickname\":\"" + entry.getKey() + "\","
                    + "\"wins\":" + entry.getValue()
                    + "}";

            joiner.add(json);
        }

        return joiner.toString();
    }

    private String toJsonArray(List<PlayerResult> results) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");

        for (PlayerResult result : results) {
            joiner.add(result.toJson());
        }

        return joiner.toString();
    }
}
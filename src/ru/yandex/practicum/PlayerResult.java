package ru.yandex.practicum;

public class PlayerResult {
    private final String nickname;
    private final int steps;
    private final boolean usedHints;

    public PlayerResult(String nickname, int steps, boolean usedHints) {
        this.nickname = nickname;
        this.steps = steps;
        this.usedHints = usedHints;
    }

    public String getNickname() {
        return nickname;
    }

    public int getSteps() {
        return steps;
    }

    public boolean isUsedHints() {
        return usedHints;
    }

    public String toJson() {
        return "{"
                + "\"nickname\":\"" + escapeJson(nickname) + "\","
                + "\"steps\":" + steps + ","
                + "\"usedHints\":" + usedHints
                + "}";
    }

    public static PlayerResult fromJson(String json) {
        String nickname = extractString(json, "nickname");
        int steps = Integer.parseInt(extractNumber(json, "steps"));
        boolean usedHints = Boolean.parseBoolean(extractBoolean(json, "usedHints"));

        return new PlayerResult(nickname, steps, usedHints);
    }

    private static String extractString(String json, String fieldName) {
        String marker = "\"" + fieldName + "\":\"";
        int start = json.indexOf(marker);

        if (start == -1) {
            throw new IllegalArgumentException("Поле не найдено: " + fieldName);
        }

        start += marker.length();
        int end = json.indexOf("\"", start);

        return json.substring(start, end);
    }

    private static String extractNumber(String json, String fieldName) {
        String marker = "\"" + fieldName + "\":";
        int start = json.indexOf(marker);

        if (start == -1) {
            throw new IllegalArgumentException("Поле не найдено: " + fieldName);
        }

        start += marker.length();

        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }

        return json.substring(start, end);
    }

    private static String extractBoolean(String json, String fieldName) {
        String marker = "\"" + fieldName + "\":";
        int start = json.indexOf(marker);

        if (start == -1) {
            throw new IllegalArgumentException("Поле не найдено: " + fieldName);
        }

        start += marker.length();

        if (json.startsWith("true", start)) {
            return "true";
        }

        if (json.startsWith("false", start)) {
            return "false";
        }

        throw new IllegalArgumentException("Некорректное boolean-поле: " + fieldName);
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
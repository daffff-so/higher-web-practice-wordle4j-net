package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerResultTest {

    @Test
    void shouldConvertResultToJson() {
        PlayerResult result = new PlayerResult("dasha", 4, true);

        String json = result.toJson();

        assertTrue(json.contains("\"nickname\":\"dasha\""));
        assertTrue(json.contains("\"steps\":4"));
        assertTrue(json.contains("\"usedHints\":true"));
    }

    @Test
    void shouldCreateResultFromJson() {
        String json = "{\"nickname\":\"dasha\",\"steps\":4,\"usedHints\":true}";

        PlayerResult result = PlayerResult.fromJson(json);

        assertEquals("dasha", result.getNickname());
        assertEquals(4, result.getSteps());
        assertTrue(result.isUsedHints());
    }
}
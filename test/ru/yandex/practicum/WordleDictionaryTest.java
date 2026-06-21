package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    @Test
    void shouldNormalizeWord() {
        String result = WordleDictionary.normalize(" ЁЖИК ");

        assertEquals("ежик", result);
    }

    @Test
    void shouldKeepOnlyFiveLetterWords() {
        WordleDictionary dictionary = new WordleDictionary(
                List.of("кот", "герой", "самолет", "ветер"),
                new PrintWriter(System.out)
        );

        assertTrue(dictionary.contains("герой"));
        assertTrue(dictionary.contains("ветер"));
        assertFalse(dictionary.contains("кот"));
        assertFalse(dictionary.contains("самолет"));
    }

    @Test
    void shouldFindWordIgnoringCaseAndYoLetter() {
        WordleDictionary dictionary = new WordleDictionary(
                List.of("берёг"),
                new PrintWriter(System.out)
        );

        assertTrue(dictionary.contains("БЕРЁГ"));
        assertTrue(dictionary.contains("берег"));
    }

    @Test
    void shouldThrowExceptionIfNoValidWords() {
        assertThrows(EmptyDictionaryException.class, () ->
                new WordleDictionary(
                        List.of("кот", "самолет"),
                        new PrintWriter(System.out)
                )
        );
    }
}

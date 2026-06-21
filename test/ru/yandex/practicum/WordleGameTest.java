package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {
    private WordleDictionary dictionary;
    private PrintWriter log;

    @BeforeEach
    void setUp() {
        log = new PrintWriter(System.out);
        dictionary = new WordleDictionary(List.of(
                "герой",
                "гонец",
                "город",
                "берег",
                "ветер",
                "ответ"
        ), log);
    }

    @Test
    void shouldCalculateHintCorrectly() {
        WordleGame game = new WordleGame(dictionary, log);

        String hint = game.calculateHint("гонец", "герой");

        assertEquals("+^-^-", hint);
    }

    @Test
    void shouldRejectShortWord() {
        WordleGame game = new WordleGame(dictionary, log);

        assertThrows(InvalidWordLengthException.class, () -> game.makeGuess("кот"));
    }

    @Test
    void shouldRejectEnglishWord() {
        WordleGame game = new WordleGame(dictionary, log);

        assertThrows(InvalidWordCharactersException.class, () -> game.makeGuess("hello"));
    }

    @Test
    void shouldRejectWordNotFromDictionary() {
        WordleGame game = new WordleGame(dictionary, log);

        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeGuess("лампа"));
    }

    @Test
    void shouldFinishGameAfterCorrectAnswer() throws GameException {
        WordleGame game = new WordleGame(dictionary, log);
        String answer = game.getAnswer();

        game.makeGuess(answer);

        assertTrue(game.isWon());
        assertTrue(game.isFinished());
    }

    @Test
    void shouldGiveHintFromDictionary() {
        WordleGame game = new WordleGame(dictionary, log);

        String hintWord = game.getHintWord();

        assertTrue(dictionary.contains(hintWord));
    }
}

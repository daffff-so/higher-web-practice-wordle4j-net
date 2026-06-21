package ru.yandex.practicum;

public class GuessAttempt {
    private final String word;
    private final String hint;

    public GuessAttempt(String word, String hint) {
        this.word = word;
        this.hint = hint;
    }

    public String getWord() {
        return word;
    }

    public String getHint() {
        return hint;
    }
}
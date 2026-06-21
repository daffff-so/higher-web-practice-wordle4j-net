package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WordleGame {
    private static final int WORD_LENGTH = 5;
    private static final int MAX_STEPS = 6;

    private final String answer;
    private final WordleDictionary dictionary;
    private final List<GuessAttempt> attempts = new ArrayList<>();
    private final PrintWriter log;

    private int stepsLeft = MAX_STEPS;
    private boolean won = false;
    private boolean usedHints = false;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();

        log.println("Создана новая игра. Загаданное слово: " + answer);
        log.flush();
    }

    public String makeGuess(String inputWord) throws GameException {
        if (isFinished()) {
            throw new IllegalStateException("Игра уже завершена.");
        }

        String word = WordleDictionary.normalize(inputWord);
        validateWord(word);

        String hint = calculateHint(word, answer);
        attempts.add(new GuessAttempt(word, hint));
        stepsLeft--;

        if (word.equals(answer)) {
            won = true;
        }

        log.println("Ход: " + word + ", подсказка: " + hint + ", осталось попыток: " + stepsLeft);
        log.flush();

        return hint;
    }

    private void validateWord(String word) throws GameException {
        if (word == null || word.isBlank()) {
            throw new InvalidWordLengthException("Слово не должно быть пустым.");
        }

        if (word.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException("Слово должно состоять из 5 букв.");
        }

        if (!WordleDictionary.containsOnlyRussianLetters(word)) {
            throw new InvalidWordCharactersException("Слово должно содержать только русские буквы.");
        }

        if (!dictionary.contains(word)) {
            throw new WordNotFoundInDictionaryException("Такого слова нет в словаре.");
        }
    }

    public String calculateHint(String guess, String correctAnswer) {
        char[] result = new char[WORD_LENGTH];
        boolean[] usedAnswerLetters = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == correctAnswer.charAt(i)) {
                result[i] = '+';
                usedAnswerLetters[i] = true;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == '+') {
                continue;
            }

            boolean found = false;

            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!usedAnswerLetters[j] && guess.charAt(i) == correctAnswer.charAt(j)) {
                    found = true;
                    usedAnswerLetters[j] = true;
                    break;
                }
            }

            result[i] = found ? '^' : '-';
        }

        return new String(result);
    }

    public String getHintWord() {
        usedHints = true;

        for (String candidate : dictionary.getWords()) {
            if (wasAlreadyUsed(candidate)) {
                continue;
            }

            if (matchesAllAttempts(candidate)) {
                log.println("Выдана подсказка: " + candidate);
                log.flush();
                return candidate;
            }
        }

        throw new IllegalStateException("Не удалось подобрать подсказку.");
    }

    private boolean wasAlreadyUsed(String word) {
        for (GuessAttempt attempt : attempts) {
            if (attempt.getWord().equals(word)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesAllAttempts(String candidate) {
        for (GuessAttempt attempt : attempts) {
            String candidateHint = calculateHint(attempt.getWord(), candidate);

            if (!candidateHint.equals(attempt.getHint())) {
                return false;
            }
        }

        return true;
    }

    public boolean isWon() {
        return won;
    }

    public boolean isFinished() {
        return won || stepsLeft == 0;
    }

    public int getStepsLeft() {
        return stepsLeft;
    }

    public int getUsedSteps() {
        return MAX_STEPS - stepsLeft;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isUsedHints() {
        return usedHints;
    }
}
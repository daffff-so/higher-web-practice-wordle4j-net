package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleDictionary {
    private static final int WORD_LENGTH = 5;

    private final List<String> words;
    private final Set<String> wordSet;
    private final Random random = new Random();
    private final PrintWriter log;

    public WordleDictionary(List<String> rawWords, PrintWriter log) {
        this.log = log;
        this.words = prepareWords(rawWords);
        this.wordSet = new HashSet<>(words);

        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Словарь не содержит подходящих слов.");
        }

        log.println("Словарь загружен. Количество слов: " + words.size());
        log.flush();
    }

    private List<String> prepareWords(List<String> rawWords) {
        List<String> preparedWords = new ArrayList<>();

        for (String rawWord : rawWords) {
            String normalizedWord = normalize(rawWord);

            if (normalizedWord.length() == WORD_LENGTH && containsOnlyRussianLetters(normalizedWord)) {
                preparedWords.add(normalizedWord);
            }
        }

        return preparedWords;
    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }

        return word.trim()
                .toLowerCase()
                .replace('ё', 'е');
    }

    public boolean contains(String word) {
        String normalizedWord = normalize(word);
        return wordSet.contains(normalizedWord);
    }

    public String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public static boolean containsOnlyRussianLetters(String word) {
        return word.matches("[а-я]+");
    }
}
package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {
    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String fileName) {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                words.add(line);
            }

            log.println("Файл словаря прочитан: " + fileName);
            log.flush();

            return new WordleDictionary(words, log);
        } catch (IOException exception) {
            log.println("Ошибка при чтении файла словаря: " + exception.getMessage());
            log.flush();

            throw new DictionaryLoadException("Не удалось загрузить словарь.", exception);
        }
    }
}
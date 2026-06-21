package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Wordle {
    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter(LOG_FILE, true));
             Scanner scanner = new Scanner(System.in)) {

            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.load(DICTIONARY_FILE);
            WordleGame game = new WordleGame(dictionary, log);

            System.out.println("Игра Wordle началась!");
            System.out.println("Введите слово из 5 русских букв.");
            System.out.println("Если нужна подсказка, нажмите Enter на пустой строке.");

            while (!game.isFinished()) {
                System.out.println();
                System.out.println("Осталось попыток: " + game.getStepsLeft());
                System.out.print("Ваше слово: ");

                String input = scanner.nextLine();

                if (input.isBlank()) {
                    String hintWord = game.getHintWord();
                    System.out.println("Подсказка: " + hintWord);
                    continue;
                }

                try {
                    String normalizedWord = WordleDictionary.normalize(input);
                    String hint = game.makeGuess(normalizedWord);

                    System.out.println(normalizedWord);
                    System.out.println(hint);
                } catch (GameException exception) {
                    System.out.println(exception.getMessage());
                    System.out.println("Попытка не засчитана.");
                    log.println("Игровая ошибка: " + exception.getMessage());
                    log.flush();
                }
            }

            System.out.println();

            if (game.isWon()) {
                System.out.println("Вы победили!");
                System.out.println("Количество использованных попыток: " + game.getUsedSteps());

                System.out.print("Введите ваш никнейм для таблицы победителей: ");
                String nickname = scanner.nextLine();

                if (nickname.isBlank()) {
                    nickname = "anonymous";
                }

                PlayerResult result = new PlayerResult(
                        nickname,
                        game.getUsedSteps(),
                        game.isUsedHints()
                );

                WordleStatisticsClient statisticsClient = new WordleStatisticsClient(log);

                boolean sent = statisticsClient.sendResult(result);

                if (sent) {
                    System.out.println("Результат отправлен на сервер.");

                    String topPlayers = statisticsClient.getTopPlayers();

                    System.out.println("Топ игроков:");
                    System.out.println(topPlayers);
                } else {
                    System.out.println("Не удалось отправить результат на сервер.");
                }
            } else {
                System.out.println("Попытки закончились.");
            }

            System.out.println("Загаданное слово: " + game.getAnswer());

        } catch (RuntimeException | IOException exception) {
            System.out.println("Произошла системная ошибка. Подробности записаны в лог.");

            try (PrintWriter log = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                log.println("Системная ошибка: " + exception.getMessage());
                exception.printStackTrace(log);
                log.flush();
            } catch (IOException logException) {
                System.out.println("Не удалось записать ошибку в лог.");
            }
        }
    }
}

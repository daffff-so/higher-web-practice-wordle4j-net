package ru.yandex.practicum;

public class InvalidWordCharactersException extends RuntimeException {
    public InvalidWordCharactersException(String message) {
        super(message);
    }
}

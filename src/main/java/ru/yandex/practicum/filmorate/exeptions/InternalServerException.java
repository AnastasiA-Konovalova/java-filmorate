package ru.yandex.practicum.filmorate.exeptions;

public class InternalServerException extends RuntimeException {
    public InternalServerException (String message) {
        super(message);
    }
}
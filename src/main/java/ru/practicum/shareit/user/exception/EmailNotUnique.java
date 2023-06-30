package ru.practicum.shareit.user.exception;

public class EmailNotUnique extends  Exception {
    public EmailNotUnique(String message) {
        super(message);
    }
}
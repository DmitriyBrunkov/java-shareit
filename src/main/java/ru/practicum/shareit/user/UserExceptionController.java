package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.EmailNotUnique;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class UserExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUserNotFoundException(final UserNotFoundException e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailNotUnique(final EmailNotUnique e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }
}

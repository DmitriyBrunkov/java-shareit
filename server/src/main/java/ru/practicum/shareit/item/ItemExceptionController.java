package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.*;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ItemExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleOwnerNotFoundException(final OwnerNotFoundException e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleItemNotFoundException(final ItemNotFoundException e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAccessViolationException(final AccessViolationException e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCommentAccessViolationException(final CommentAccessViolationException e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCommentTextValidationException(final CommentTextValidationException e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }
}

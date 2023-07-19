package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ItemRequestExceptionController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleItemRequestNotFoundException(final ItemRequestNotFoundException e) {
        log.info("Exception {} with message: {}", e.getClass(), e.getMessage());
        return Map.of("error", e.getMessage());
    }
}

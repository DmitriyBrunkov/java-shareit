package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                      @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info(this.getClass()
                .getSimpleName() + ": POST: userId: " + requestorId + " itemRequest: " + itemRequestDto);
        return itemRequestClient.add(requestorId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("requestId") Long requestId) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " requestId: " + requestId);
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + requestorId);
        return itemRequestClient.getAllByRequestor(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "20") int size) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " from: " + from + " size: " + size);
        return itemRequestClient.getAll(userId, from, size);
    }
}

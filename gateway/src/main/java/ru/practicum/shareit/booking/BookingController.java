package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exception.BookingIntervalInvalid;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getListOfUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getListOfUserBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid BookItemRequestDto requestDto) throws
            BookingIntervalInvalid {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        if (!isValidInterval(requestDto.getStart(), requestDto.getEnd())) {
            throw new BookingIntervalInvalid("Interval: " + requestDto.getStart() + " - " +
                    requestDto.getEnd() + " is invalid");
        }
        return bookingClient.create(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getListOfOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                         @Positive @RequestParam(defaultValue = "20") int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info(this.getClass()
                .getSimpleName() + ": GET: userId: " + ownerId + " state: " + state + " from:" + from + " size: " + size);
        return bookingClient.getListOfOwnerBookings(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("bookingId") Long bookingId,
                                         @RequestParam boolean approved) {
        log.info(this.getClass()
                .getSimpleName() + ": PATCH: userId: " + userId + " bookingId: " + bookingId + " approved: " + approved);
        return bookingClient.update(userId, bookingId, approved);
    }

    private boolean isValidInterval(LocalDateTime start, LocalDateTime end) {
        return end.isAfter(start);
    }
}

package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IllegalPagingArgumentException;
import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingController(BookingService bookingService, ItemService itemService, UserService userService) {
        this.bookingService = bookingService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("bookingId") Long bookingId)
            throws BookingNotFound, ItemNotFoundException, AccessViolationException {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " bookingId: " + bookingId);
        if (bookingService.getById(bookingId).getBooker().getId() != userId) {
            if (itemService.get(bookingService.getById(bookingId).getItem().getId()).getOwner().getId() != userId) {
                throw new AccessViolationException("Only owner or booker can access booking");
            }
        }
        return BookingMapper.toBookingDto(bookingService.getById(bookingId));
    }

    @GetMapping
    public List<BookingDto> getListOfUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "20") int size)
            throws UserNotFoundException, IllegalPagingArgumentException {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " state: " + state + " from:" + from +
                " size: " + size);
        if (from < 0 || size < 1) {
            throw new IllegalPagingArgumentException("Illegal argument");
        }
        userService.get(userId);
        switch (state) {
            case ALL:
                return bookingService.getAllByBookerId(userId, from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingService.getAllByBookerIdFutureStart(userId, from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST:
                return bookingService.getAllByBookerIdPastEnd(userId, from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case CURRENT:
                return bookingService.getAllByBookerIdCurrent(userId, from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingService.getAllByBookerIdAndStatus(userId, BookingStatus.WAITING, from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingService.getAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @GetMapping("/owner")
    List<BookingDto> getListOfOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(defaultValue = "ALL") BookingState state,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "20") int size)
            throws UserNotFoundException, IllegalPagingArgumentException {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + ownerId + " state: " + state + " from:" + from +
                " size: " + size);
        if (from < 0 || size < 1) {
            throw new IllegalPagingArgumentException("Illegal argument");
        }
        switch (state) {
            case ALL:
                return bookingService.getAllByItemList(itemService.getUserItems(ownerId).stream().map(Item::getId)
                                .collect(Collectors.toList()), from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingService.getAllByItemListFutureStart(itemService.getUserItems(ownerId).stream()
                                .map(Item::getId).collect(Collectors.toList()), from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST:
                return bookingService.getAllByItemListPastEnd(itemService.getUserItems(ownerId).stream()
                                .map(Item::getId).collect(Collectors.toList()), from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case CURRENT:
                return bookingService.getAllByItemListCurrent(itemService.getUserItems(ownerId).stream()
                                .map(Item::getId).collect(Collectors.toList()), from, size).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingService.getAllByItemListAndStatus(itemService.getUserItems(ownerId).stream()
                                .map(Item::getId).collect(Collectors.toList()), BookingStatus.WAITING, from, size)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingService.getAllByItemListAndStatus(itemService.getUserItems(ownerId).stream()
                                .map(Item::getId).collect(Collectors.toList()), BookingStatus.REJECTED, from, size)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody @Validated BookingShortDto bookingShortDto)
            throws ItemNotFoundException, UserNotFoundException, BookingItemUnavailable,
            BookingIntervalInvalid, BookingAccessException {
        log.info(this.getClass().getSimpleName() + ": POST: userId: " + userId + " booking: " + bookingShortDto);
        bookingShortDto.setStatus(BookingStatus.WAITING);
        Item item = itemService.get(bookingShortDto.getItemId());
        User user = userService.get(userId);
        if (!item.getAvailable()) {
            throw new BookingItemUnavailable("Item: " + item.getId() + " is unavailable");
        }
        if (user.equals(item.getOwner())) {
            throw new BookingAccessException("User can't book own item");
        }
        if (!isValidInterval(bookingShortDto.getStart(), bookingShortDto.getEnd())) {
            throw new BookingIntervalInvalid("Interval: " + bookingShortDto.getStart() + " - " +
                    bookingShortDto.getEnd() + " is invalid");
        }
        return BookingMapper.toBookingDto(bookingService.create(BookingMapper.toBooking(bookingShortDto, item, user)));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingId") Long bookingId,
                             @RequestParam boolean approved)
            throws ItemNotFoundException, AccessViolationException, BookingNotFound,
            BookingStatusException, BookingAccessException {
        log.info(this.getClass()
                .getSimpleName() + ": PATCH: userId: " + userId + " bookingId: " + bookingId + " approved: "
                + approved);
        if (itemService.get(bookingService.getById(bookingId).getItem().getId()).getOwner().getId() != userId) {
            throw new AccessViolationException("Only owner can can control booking");
        }
        Booking booking = bookingService.getById(bookingId);
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingStatusException("Status of booking " + booking.getId() + " is not WAITING");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingService.update(booking));
    }

    private boolean isValidInterval(LocalDateTime start, LocalDateTime end) {
        return start.isAfter(LocalDateTime.now()) && end.isAfter(LocalDateTime.now()) && end.isAfter(start);
    }
}

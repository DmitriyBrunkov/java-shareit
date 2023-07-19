package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking getById(Long id) throws BookingNotFound;

    List<Booking> getAllByBookerId(Long bookerId, int from, int size);

    List<Booking> getAllByBookerIdFutureStart(Long bookerId, int from, int size);

    List<Booking> getAllByBookerIdPastEnd(Long bookerId, int from, int size);

    List<Booking> getAllByBookerIdCurrent(Long bookerId, int from, int size);

    List<Booking> getAllByBookerIdAndStatus(Long bookerId, BookingStatus status, int from, int size);

    List<Booking> getAllByItemList(List<Long> items, int from, int size);

    List<Booking> getAllByItemListFutureStart(List<Long> items, int from, int size);

    List<Booking> getAllByItemListPastEnd(List<Long> items, int from, int size);

    List<Booking> getAllByItemListAndStatus(List<Long> items, BookingStatus status, int from, int size);

    List<Booking> getAllByItemListCurrent(List<Long> items, int from, int size);

    Booking create(Booking booking);

    Booking update(Booking booking);

    Booking getLastBookingByItem(Long itemId);

    Booking getNextBookingByItem(Long itemId);
}

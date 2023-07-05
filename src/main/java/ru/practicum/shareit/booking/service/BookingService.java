package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {
    Booking getById(Long id) throws BookingNotFound;

    List<Booking> getAllByBookerId(Long bookerId);

    List<Booking> getAllByBookerIdFutureStart(Long bookerId);

    List<Booking> getAllByBookerIdPastEnd(Long bookerId);

    List<Booking> getAllByBookerIdCurrent(Long bookerId);

    List<Booking> getAllByBookerIdAndStatus(Long bookerId, BookingStatus status);

    List<Booking> getAllByItemList(List<Item> items);

    List<Booking> getAllByItemListFutureStart(List<Item> items);

    List<Booking> getAllByItemListPastEnd(List<Item> items);

    List<Booking> getAllByItemListAndStatus(List<Item> items, BookingStatus status);

    List<Booking> getAllByItemListCurrent(List<Item> items);

    Booking create(Booking booking);

    Booking update(Booking booking);

    Booking getLastBookingByItem(Long itemId);

    Booking getNextBookingByItem(Long itemId);
}

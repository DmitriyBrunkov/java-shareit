package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public Booking getById(Long id) throws BookingNotFound {
        return bookingRepository.findById(id).orElseThrow(() -> new BookingNotFound("Booking: " + id + " not found"));
    }

    @Override
    public List<Booking> getAllByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBooker_IdOrderByStartDesc(bookerId);
    }

    @Override
    public List<Booking> getAllByBookerIdFutureStart(Long bookerId) {
        return bookingRepository.findBookingsByBooker_IdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
    }

    @Override
    public List<Booking> getAllByBookerIdPastEnd(Long bookerId) {
        return bookingRepository.findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
    }

    @Override
    public List<Booking> getAllByBookerIdCurrent(Long bookerId) {
        return bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Override
    public List<Booking> getAllByBookerIdAndStatus(Long bookerId, BookingStatus status) {
        return bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(bookerId, status);
    }

    @Override
    public List<Booking> getAllByItemList(List<Item> items) {
        return bookingRepository.findBookingsByItemInOrderByStartDesc(items);
    }

    @Override
    public List<Booking> getAllByItemListFutureStart(List<Item> items) {
        return bookingRepository.findBookingsByItemInAndStartAfterOrderByStartDesc(items, LocalDateTime.now());
    }

    @Override
    public List<Booking> getAllByItemListPastEnd(List<Item> items) {
        return bookingRepository.findBookingsByItemInAndEndBeforeOrderByStartDesc(items, LocalDateTime.now());
    }

    @Override
    public List<Booking> getAllByItemListAndStatus(List<Item> items, BookingStatus status) {
        return bookingRepository.findBookingsByItemInAndStatusOrderByStartDesc(items, status);
    }

    @Override
    public List<Booking> getAllByItemListCurrent(List<Item> items) {
        return bookingRepository.findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(items,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Override
    public Booking create(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getLastBookingByItem(Long itemId) {
        return bookingRepository.getDistinctTopByItem_IdAndStartBeforeAndStatusNotOrderByEndDesc(itemId,
                LocalDateTime.now(), BookingStatus.REJECTED);
    }

    @Override
    public Booking getNextBookingByItem(Long itemId) {
        return bookingRepository.getDistinctTopByItem_IdAndStartAfterAndStatusNotOrderByStartAsc(itemId,
                LocalDateTime.now(), BookingStatus.REJECTED);
    }
}

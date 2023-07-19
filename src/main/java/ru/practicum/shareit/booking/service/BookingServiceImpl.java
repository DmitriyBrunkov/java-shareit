package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

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
    public List<Booking> getAllByBookerId(Long bookerId, int from, int size) {
        return bookingRepository.findBookingsByBooker_Id(bookerId, PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("start").descending())).toList();
    }

    @Override
    public List<Booking> getAllByBookerIdFutureStart(Long bookerId, int from, int size) {
        return bookingRepository.findBookingsByBooker_IdAndStartAfter(bookerId, LocalDateTime.now(), PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("start").descending())).toList();
    }

    @Override
    public List<Booking> getAllByBookerIdPastEnd(Long bookerId, int from, int size) {
        return bookingRepository.findBookingsByBooker_IdAndEndBefore(bookerId, LocalDateTime.now(), PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("start").descending())).toList();
    }

    @Override
    public List<Booking> getAllByBookerIdCurrent(Long bookerId, int from, int size) {
        return bookingRepository.findBookingsByBooker_IdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(),
                LocalDateTime.now(), PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("start")
                        .descending())).toList();
    }

    @Override
    public List<Booking> getAllByBookerIdAndStatus(Long bookerId, BookingStatus status, int from, int size) {
        return bookingRepository.findBookingsByBooker_IdAndStatus(bookerId, status,
                PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("start").descending())).toList();
    }

    @Override
    public List<Booking> getAllByItemList(List<Long> items, int from, int size) {
        return bookingRepository.findBookingsByItem_IdIn(items, PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("start").descending())).toList();
    }

    @Override
    public List<Booking> getAllByItemListFutureStart(List<Long> items, int from, int size) {
        return bookingRepository.findBookingsByItem_IdInAndStartAfter(items, LocalDateTime.now(), PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("start").descending())).toList();
    }

    @Override
    public List<Booking> getAllByItemListPastEnd(List<Long> items, int from, int size) {
        return bookingRepository.findBookingsByItem_IdInAndEndBefore(items, LocalDateTime.now(), PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("start").descending())).toList();
    }

    @Override
    public List<Booking> getAllByItemListAndStatus(List<Long> items, BookingStatus status, int from, int size) {
        return bookingRepository.findBookingsByItem_IdInAndStatus(items, status, PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("start").descending())).toList();
    }

    @Override
    public List<Booking> getAllByItemListCurrent(List<Long> items, int from, int size) {
        return bookingRepository.findBookingsByItem_IdInAndStartBeforeAndEndAfter(items,
                LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from > 0 ? from / size : 0, size,
                        Sort.by("start").descending())).toList();
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

package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByItem_Owner_Id(Long ownerId);

    List<Booking> findBookingsByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findBookingsByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime nowStart, LocalDateTime nowEnd);

    List<Booking> findBookingsByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findBookingsByItemInOrderByStartDesc(Collection<Item> items);

    List<Booking> findBookingsByItemInAndStartAfterOrderByStartDesc(Collection<Item> items, LocalDateTime now);

    List<Booking> findBookingsByItemInAndEndBeforeOrderByStartDesc(Collection<Item> items, LocalDateTime now);

    List<Booking> findBookingsByItemInAndStartBeforeAndEndAfterOrderByStartDesc(Collection<Item> items, LocalDateTime nowStart, LocalDateTime nowEnd);

    List<Booking> findBookingsByItemInAndStatusOrderByStartDesc(Collection<Item> items, BookingStatus status);

    Booking getDistinctTopByItem_IdAndStartBeforeAndStatusNotOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Booking getDistinctTopByItem_IdAndStartAfterAndStatusNotOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);

    boolean existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);
}

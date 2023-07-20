package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.exception.BookingNotFound;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class BookingServiceTests {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @Autowired
    ItemService itemService;

    User user = new User(1, "test_name", "test@test.ru");
    User secondUser = new User(2, "test_name_2", "test2@test.ru");
    Item item = new Item(1, "test_name", "test_description", true, null, user);
    LocalDateTime start = LocalDateTime.now().withNano(0);
    Booking booking = new Booking(1, start, start.plusDays(1), item, secondUser, BookingStatus.WAITING);
    int from = 0;
    int size = 20;

    @BeforeEach
    void addData() throws UserNotFoundException, OwnerNotFoundException, ItemNotFoundException {
        userService.add(user);
        userService.add(secondUser);
        itemService.add(item);
    }

    @Test
    void getByIdTest() throws BookingNotFound {
        bookingService.create(booking);
        Booking resultBooking = bookingService.getById(1L);
        Assertions.assertEquals(resultBooking, booking);
    }

    @Test
    void getAllByBookerIdTest() {
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByBookerId(secondUser.getId(), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByBookerIdFutureStartTest() {
        booking.setStart(start.plusYears(1));
        booking.setEnd(start.plusYears(1).plusDays(1));
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByBookerIdFutureStart(secondUser.getId(), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByBookerIdPastEndTest() {
        booking.setStart(start.minusYears(1));
        booking.setEnd(start.minusYears(1).plusDays(1));
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByBookerIdPastEnd(secondUser.getId(), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByBookerIdCurrentTest() {
        booking.setStart(start.minusYears(1));
        booking.setEnd(start.plusYears(1).plusDays(1));
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByBookerIdCurrent(secondUser.getId(), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByBookerIdAndStatusTest() {
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByBookerIdAndStatus(secondUser.getId(),
                booking.getStatus(), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByItemListTest() {
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByItemList(List.of(item.getId()), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByItemListFutureStartTest() {
        booking.setStart(start.plusYears(1));
        booking.setEnd(start.plusYears(1).plusDays(1));
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByItemListFutureStart(List.of(item.getId()), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByItemListPastEndTest() {
        booking.setStart(start.minusYears(1));
        booking.setEnd(start.minusYears(1).plusDays(1));
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByItemListPastEnd(List.of(item.getId()), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByItemListAndStatusTest() {
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByItemListAndStatus(List.of(item.getId()),
                booking.getStatus(), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void getAllByItemListCurrentTest() {
        booking.setStart(start.minusYears(1));
        booking.setEnd(start.plusYears(1).plusDays(1));
        bookingService.create(booking);
        List<Booking> resultBookings = bookingService.getAllByItemListCurrent(List.of(item.getId()), from, size);
        Assertions.assertEquals(resultBookings, List.of(booking));
    }

    @Test
    void updateTest() {
        bookingService.create(booking);
        Booking modifiedBooking = new Booking(1, start, start.plusDays(1), item, secondUser, BookingStatus.REJECTED);
        Booking resultBooking = bookingService.update(modifiedBooking);
        Assertions.assertEquals(resultBooking, modifiedBooking);
    }

    @Test
    void getLastBookingByItemTest() {
        booking.setStart(start.minusYears(1));
        booking.setEnd(start.minusYears(1).plusDays(1));
        bookingService.create(booking);
        Booking resultBooking = bookingService.getLastBookingByItem(item.getId());
        Assertions.assertEquals(resultBooking, booking);
    }

    @Test
    void getNextBookingByItemTest() {
        booking.setStart(start.plusYears(1));
        booking.setEnd(start.plusYears(1).plusDays(1));
        bookingService.create(booking);
        Booking resultBooking = bookingService.getNextBookingByItem(item.getId());
        Assertions.assertEquals(resultBooking, booking);
    }

    @Test
    void throwsBookingNotFoundExceptionTest() {
        final BookingNotFound exception = Assertions.assertThrows(BookingNotFound.class,
                () -> bookingService.getById(99L));
        Assertions.assertEquals("Booking: 99 not found", exception.getMessage());
    }
}

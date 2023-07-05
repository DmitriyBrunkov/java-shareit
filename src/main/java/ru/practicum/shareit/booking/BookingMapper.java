package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(),
                booking.getBooker(), booking.getStatus());
    }

    public static Booking toBooking(BookingShortDto bookingShortDto, Item item, User booker) {
        return new Booking(bookingShortDto.getId(), bookingShortDto.getStart(), bookingShortDto.getEnd(),
                item, booker, bookingShortDto.getStatus());
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return new BookingShortDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId(),
                booking.getBooker().getId(), booking.getStatus());
    }
}

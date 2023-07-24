package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    private final User user = new User(1, "user_name", "test@test.ru");
    private final User secondUser = new User(2, "user_name", "test@test.ru");
    private final Item item = new Item(1, "item_name", "item_description", true, null, user);

    private final Booking booking = new Booking(1, LocalDateTime.now().withNano(0).plusDays(1),
            LocalDateTime.now().withSecond(1).withNano(0).plusDays(2), item, secondUser, BookingStatus.WAITING);
    private final BookingDto bookingDto = BookingMapper.toBookingDto(booking);

    private final BookingShortDto bookingShortDto = BookingMapper.toBookingShortDto(booking);

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @MockBean
    BookingService bookingService;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void getBookingByIdTest() throws Exception {
        Mockito.when(bookingService.getById(Mockito.anyLong())).thenReturn(booking);
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(bookingService.getById(Mockito.anyLong())).thenReturn(booking);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfUserBookingsWaitingTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingService.getAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfUserBookingsFutureTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingService.getAllByBookerIdFutureStart(Mockito.anyLong(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfUserBookingsAllTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingService.getAllByBookerId(Mockito.anyLong(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfUserBookingsPastTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingService.getAllByBookerIdPastEnd(Mockito.anyLong(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "PAST")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfUserBookingsCurrentTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingService.getAllByBookerIdCurrent(Mockito.anyLong(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfUserBookingsRejectedTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingService.getAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "REJECTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfOwnerBookingsWaitingTest() throws Exception {
        Mockito.when(bookingService.getAllByItemListAndStatus(Mockito.anyList(), Mockito.any(BookingStatus.class),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfOwnerBookingsRejectedTest() throws Exception {
        Mockito.when(bookingService.getAllByItemListAndStatus(Mockito.anyList(), Mockito.any(BookingStatus.class),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "REJECTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfOwnerBookingsAllTest() throws Exception {
        Mockito.when(bookingService.getAllByItemList(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfOwnerBookingsFutureTest() throws Exception {
        Mockito.when(bookingService.getAllByItemListFutureStart(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfOwnerBookingsPastTest() throws Exception {
        Mockito.when(bookingService.getAllByItemListPastEnd(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "PAST")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getListOfOwnerBookingsCurrentTest() throws Exception {
        Mockito.when(bookingService.getAllByItemListCurrent(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void createTest() throws Exception {
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(secondUser);
        Mockito.when(bookingService.create(Mockito.any(Booking.class))).thenReturn(booking);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingShortDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void updateTest() throws Exception {
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(bookingService.getById(Mockito.anyLong())).thenReturn(booking);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(secondUser);
        Mockito.when(bookingService.update(Mockito.any(Booking.class))).thenReturn(booking);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingShortDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void bookingControllerThrowsBookingAccessExceptionTest() throws Exception {
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingService.create(Mockito.any(Booking.class))).thenReturn(booking);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingShortDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is(("User can't book own item"))));
    }

    @Test
    void bookingControllerThrowsBookingItemUnavailableExceptionTest() throws Exception {
        item.setAvailable(false);
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(secondUser);
        Mockito.when(bookingService.create(Mockito.any(Booking.class))).thenReturn(booking);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingShortDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Item: " + item.getId() + " is unavailable")));
    }

    @Test
    void bookingControllerThrowsBookingStatusExceptionTest() throws Exception {
        booking.setStatus(BookingStatus.CANCELED);
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(bookingService.getById(Mockito.anyLong())).thenReturn(booking);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(secondUser);
        Mockito.when(bookingService.update(Mockito.any(Booking.class))).thenReturn(booking);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingShortDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Status of booking " + booking.getId() + " is not WAITING")));
    }

    @Test
    void bookingControllerThrowsIllegalPagingArgumentExceptionTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(bookingService.getAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WAITING")
                        .param("from", "-1")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Illegal argument")));
    }
}

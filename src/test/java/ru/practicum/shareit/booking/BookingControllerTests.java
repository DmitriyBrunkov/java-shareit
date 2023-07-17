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

    private final Booking booking = new Booking(1, LocalDateTime.of(2024, 6, 17, 11, 0, 1),
            LocalDateTime.of(2024, 6, 17, 11, 0, 1)
                    .plusDays(1), item, secondUser, BookingStatus.WAITING);
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
    void getBookingById() throws Exception {
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
    void getListOfUserBookings() throws Exception {
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
    void getListOfOwnerBookings() throws Exception {
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
    void create() throws Exception {
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
    void update() throws Exception {
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
}

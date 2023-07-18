package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    private final User user = new User(1, "user_name", "test@test.ru");
    private final User secondUser = new User(2, "user_name", "test@test.ru");

    private final Item item = new Item(1, "item_name", "item_description", true, null, user);

    private final ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
            item.getAvailable(), null, item.getOwner().getId());

    private final Comment comment = new Comment(1, "comment_text", item, secondUser,
            LocalDateTime.of(2023, 6, 17, 11, 0, 1));

    private final CommentDto commentDto = CommentMapper.toCommentDto(comment);
    private final ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemWithBookingDto(item,
            List.of(CommentMapper.toCommentDto(comment)));
    private final Booking booking = new Booking(1,
            LocalDateTime.of(2023, 6, 17, 11, 0),
            LocalDateTime.of(2023, 6, 17, 11, 0)
            .plusDays(1), item, secondUser, BookingStatus.WAITING);
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

    @MockBean
    UserService userService;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void getItemByIdTest() throws Exception {
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(itemService.getCommentsByItemId(Mockito.anyLong())).thenReturn(List.of(comment));
        Mockito.doReturn(booking).when(bookingService).getLastBookingByItem(Mockito.anyLong());
        itemWithBookingDto.setLastBooking(BookingMapper.toBookingShortDto(booking));
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBookingDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemWithBookingDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.lastBooking.id", is(itemWithBookingDto.getLastBooking()
                        .getItemId()), Long.class));
    }

    @Test
    void getUserItemsTest() throws Exception {
        Mockito.when(itemService.getUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(item));
        Mockito.when(itemService.getCommentsByItemId(Mockito.anyLong())).thenReturn(List.of(comment));
        Mockito.doReturn(booking).when(bookingService).getLastBookingByItem(Mockito.anyLong());
        itemWithBookingDto.setLastBooking(BookingMapper.toBookingShortDto(booking));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingDto.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(itemWithBookingDto.getOwner()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemWithBookingDto.getLastBooking()
                        .getItemId()), Long.class));
    }

    @Test
    void searchTest() throws Exception {
        Mockito.when(itemService.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(item));
        mvc.perform(get("/items/search")
                        .param("text", "test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].owner", is(itemDto.getOwner()), Long.class));
    }

    @Test
    void addTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemService.add(Mockito.any(Item.class))).thenReturn(item);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
    }

    @Test
    void updateTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemService.update(Mockito.any(Item.class))).thenReturn(item);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
    }

    @Test
    void addCommentTest() throws Exception {
        Mockito.when(itemService.canUserComment(Mockito.anyLong(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(itemService.addComment(Mockito.any(Comment.class))).thenReturn(comment);
        commentDto.setCreated(LocalDateTime.of(2023, 6, 17, 11, 0, 1));
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }

    @Test
    void itemControllerThrowsCommentAccessViolationExceptionTest() throws Exception {
        Mockito.when(itemService.canUserComment(Mockito.anyLong(), Mockito.anyLong())).thenReturn(false);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        Mockito.when(itemService.addComment(Mockito.any(Comment.class))).thenReturn(comment);
        commentDto.setCreated(LocalDateTime.of(2023, 6, 17, 11, 0, 1));
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Only booker can comment")));
    }

    @Test
    void itemControllerThrowsCommentTextValidationExceptionTest() throws Exception {
        Mockito.when(itemService.canUserComment(Mockito.anyLong(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);
        commentDto.setText("");
        Mockito.when(itemService.addComment(Mockito.any(Comment.class))).thenReturn(comment);
        commentDto.setCreated(LocalDateTime.of(2023, 6, 17, 11, 0, 1));
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("text can't be blank")));
    }
}

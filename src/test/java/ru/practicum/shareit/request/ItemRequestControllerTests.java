package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {
    private final User user = new User(1, "user_name", "test@test.ru");
    private final ItemRequest itemRequest = new ItemRequest(1, "request_description",
            user, LocalDateTime.of(2023, 7, 17, 11, 0, 1));
    private final Item item = new Item(1, "item_name", "item_description", true, itemRequest, user);
    private final ItemForRequestDto itemForRequestDto = ItemMapper.toItemForRequestDto(item);
    private final ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    private final ItemRequestWithItemDto itemRequestWithItemDto = ItemRequestMapper.toItemRequestWithItemDto(itemRequest,
            List.of(itemForRequestDto));


    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @MockBean
    ItemRequestService itemRequestService;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void addTest() throws Exception {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemRequestService.add(Mockito.any(ItemRequest.class))).thenReturn(itemRequest);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
    }

    @Test
    void getByIdTest() throws Exception {
        Mockito.when(userService.exist(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestService.exist(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestService.getById(Mockito.anyLong())).thenReturn(itemRequest);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())));
    }

    @Test
    void getAllTest() throws Exception {
        Mockito.when(itemService.getRequestedItems()).thenReturn(List.of(item));
        Mockito.when(itemRequestService.getListOfAll(Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(itemRequest));
        Mockito.when(itemService.getItemsByRequestId(Mockito.anyLong())).thenReturn(List.of(item));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestWithItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestWithItemDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestWithItemDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestWithItemDto.getItems().get(0).getId()), Long.class));
    }

    @Test
    void getAllByRequestorTest() throws Exception {
        Mockito.when(userService.exist(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestService.getListByRequestorId(Mockito.anyLong())).thenReturn(List.of(itemRequest));
        Mockito.when(itemService.getItemsByRequestId(Mockito.anyLong())).thenReturn(List.of(item));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestWithItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestWithItemDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestWithItemDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestWithItemDto.getItems().get(0).getId()), Long.class));
    }

    @Test
    void itemRequestControllerThrowsItemRequestNotFoundExceptionTest() throws Exception {
        Mockito.when(userService.exist(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestService.exist(Mockito.anyLong())).thenReturn(false);
        Mockito.when(itemRequestService.getById(Mockito.anyLong())).thenReturn(itemRequest);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Item Request 1 not found")));
    }
}

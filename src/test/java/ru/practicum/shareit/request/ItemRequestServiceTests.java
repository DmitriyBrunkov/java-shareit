package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ItemRequestServiceTests {

    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    UserService userService;

    User user = new User(1, "test_name", "test@test.ru");
    User secondUser = new User(2, "test_name_2", "test2@test.ru");
    ItemRequest itemRequest = new ItemRequest(1, "test_description", secondUser, LocalDateTime.now().withNano(0));

    @BeforeEach
    void addData() throws UserNotFoundException {
        userService.add(user);
        userService.add(secondUser);
        itemRequestService.add(itemRequest);
    }

    @Test
    void getByIdTest() throws Exception {
        ItemRequest resultItemRequest = itemRequestService.getById(1L);
        resultItemRequest.setCreated(itemRequest.getCreated());
        Assertions.assertEquals(resultItemRequest, itemRequest);
    }

    @Test
    void getListByRequestorIdTest() {
        List<ItemRequest> resultItemRequests = itemRequestService.getListByRequestorId(2L);
        resultItemRequests.get(0).setCreated(itemRequest.getCreated());
        Assertions.assertEquals(resultItemRequests, List.of(itemRequest));
    }

    @Test
    void getListOfAllTest() {
        List<ItemRequest> resultItemRequests = itemRequestService.getListOfAll(0, 20);
        resultItemRequests.get(0).setCreated(itemRequest.getCreated());
        Assertions.assertEquals(resultItemRequests, List.of(itemRequest));
    }

    @Test
    void existTest() {
        Assertions.assertTrue(itemRequestService.exist(1L));
    }
}

package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ItemServiceTests {
    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @Autowired
    ItemRequestService itemRequestService;

    User user = new User(1, "test_name", "test@test.ru");
    User secondUser = new User(2, "test_name_2", "test2@test.ru");
    Item originalItem = new Item(1, "test_name", "test_description", true, null, user);
    LocalDateTime start = LocalDateTime.now().withNano(0);
    Booking booking = new Booking(1, start, start.plusDays(1), originalItem, secondUser, BookingStatus.APPROVED);
    Comment comment = new Comment(1, "test_text", originalItem, secondUser, start);

    @Test
    void itemUpdateOnlyNameTest() throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException {
        itemRepository.save(originalItem);
        Item resultItem = itemService.update(new Item(1, "new_name", null, null, null, user));
        Assertions.assertEquals(new Item(1, "new_name", "test_description", true, null, user), resultItem);
    }

    @Test
    void itemUpdateOnlyDescriptionTest() throws AccessViolationException, OwnerNotFoundException,
            ItemNotFoundException {
        itemRepository.save(originalItem);
        Item resultItem = itemService.update(new Item(1, null, "new_description", null, null, user));
        Assertions.assertEquals(new Item(1, "test_name", "new_description", true, null, user), resultItem);
    }

    @Test
    void itemUpdateOnlyAvailableTest() throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException {
        itemRepository.save(originalItem);
        Item resultItem = itemService.update(new Item(1, null, null, false, null, user));
        Assertions.assertEquals(new Item(1, "test_name", "test_description", false, null, user), resultItem);
    }

    @Test
    void itemUpdateTest() throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException,
            UserNotFoundException {
        userService.add(user);
        itemRepository.save(originalItem);
        Item resultItem = itemService.update(new Item(1, "new_name", "new_description", false, null, user));
        Assertions.assertEquals(new Item(1, "new_name", "new_description", false, null, user), resultItem);
    }

    @Test
    void itemUpdateThrowsItemNotFoundExceptionTest() {
        final ItemNotFoundException exception = Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.update(new Item(99, "test_name", "test_description", true, null, user)));
        Assertions.assertEquals("Item 99 not found", exception.getMessage());
    }

    @Test
    void itemUpdateThrowsOwnerNotFoundExceptionTest() throws UserNotFoundException {
        itemRepository.save(new Item(1, "test_name", "test_description", true, null,
                userService.add(new User(1, "test_name", "test@test.ru"))));
        final OwnerNotFoundException exception = Assertions.assertThrows(OwnerNotFoundException.class,
                () -> itemService.update(new Item(1, "test_name", "test_description", true, null,
                        new User(99, "test_name", "test@test.ru"))));
        Assertions.assertEquals("Owner 99 doesn't exist", exception.getMessage());
    }

    @Test
    void itemUpdateAccessViolationExceptionTest() throws UserNotFoundException {
        itemRepository.save(new Item(1, "test_name", "test_description", true, null,
                userService.add(new User(1, "test_name", "test@test.ru"))));
        User guestUser = userService.add(new User(2, "test_name_2", "test2@test.ru"));
        final AccessViolationException exception = Assertions.assertThrows(AccessViolationException.class,
                () -> itemService.update(new Item(1, "test_name", "test_description", true, null, guestUser)));
        Assertions.assertEquals("Access from user 2 to item 1 not granted", exception.getMessage());
    }

    @Test
    void getUserItemsPageTest() throws Exception {
        userService.add(user);
        itemService.add(originalItem);
        List<Item> resultItems = itemService.getUserItems(1L, 0, 20);
        Assertions.assertEquals(resultItems, List.of(originalItem));
    }

    @Test
    void getUserItemsTest() throws Exception {
        userService.add(user);
        itemService.add(originalItem);
        List<Item> resultItems = itemService.getUserItems(1L);
        Assertions.assertEquals(resultItems, List.of(originalItem));
    }

    @Test
    void getTest() throws Exception {
        userService.add(user);
        itemService.add(originalItem);
        Item resultItem =  itemService.get(1L);
        Assertions.assertEquals(resultItem, originalItem);
    }

    @Test
    void searchTest() throws Exception {
        userService.add(user);
        itemService.add(originalItem);
        List<Item> resultItems =  itemService.search("description", 0, 20);
        Assertions.assertEquals(resultItems, List.of(originalItem));
    }

    @Test
    void canUserCommentTest() throws Exception {
        userService.add(user);
        userService.add(secondUser);
        itemService.add(originalItem);
        bookingService.create(booking);
        Assertions.assertTrue(itemService.canUserComment(2L, 1L));
    }

    @Test
    void addComment() throws Exception {
        userService.add(user);
        userService.add(secondUser);
        itemService.add(originalItem);
        Comment resultComment = itemService.addComment(comment);
        resultComment.setCreated(start);
        Assertions.assertEquals(resultComment, comment);
    }

    @Test
    void getCommentsByItemIdTest() throws Exception {
        userService.add(user);
        userService.add(secondUser);
        itemService.add(originalItem);
        itemService.addComment(comment);
        List<Comment> resultComments = itemService.getCommentsByItemId(1L);
        resultComments.get(0).setCreated(start);
        Assertions.assertEquals(List.of(comment),  resultComments);
    }

    @Test
    void getItemsByRequestIdTest() throws Exception {
        userService.add(user);
        userService.add(secondUser);
        ItemRequest itemRequest = new ItemRequest(1, "test_description", secondUser, start);
        itemRequestService.add(itemRequest);
        originalItem.setRequest(itemRequest);
        itemService.add(originalItem);
        List<Item> resultItems = itemService.getItemsByRequestId(1L);
        resultItems.get(0).getRequest().setCreated(start);
        Assertions.assertEquals(List.of(originalItem), resultItems);
    }

    @Test
    void getRequestedItemsTest() throws Exception {
        userService.add(user);
        userService.add(secondUser);
        ItemRequest itemRequest = new ItemRequest(1, "test_description", secondUser, start);
        itemRequestService.add(itemRequest);
        originalItem.setRequest(itemRequest);
        itemService.add(originalItem);
        List<Item> resultItems = itemService.getRequestedItems();
        resultItems.get(0).getRequest().setCreated(start);
        Assertions.assertEquals(List.of(originalItem), resultItems);
    }
}

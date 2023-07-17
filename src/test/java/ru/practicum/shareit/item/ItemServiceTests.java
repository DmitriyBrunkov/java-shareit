package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
public class ItemServiceTests {
    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserService userService;

    @Test
    void testItemUpdateOnlyName() throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException,
            UserNotFoundException {
        User user = userService.add(new User(1, "test_name", "test@test.ru"));
        Item originalItem = new Item(1, "test_name", "test_description", true, null, user);
        itemRepository.save(originalItem);
        Item resultItem = itemService.update(new Item(1, "new_name", null, null, null, user));
        Assertions.assertEquals(new Item(1, "new_name", "test_description", true, null, user), resultItem);
    }

    @Test
    void testItemUpdateOnlyDescription() throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException,
            UserNotFoundException {
        User user = userService.add(new User(1, "test_name", "test@test.ru"));
        Item originalItem = new Item(1, "test_name", "test_description", true, null, user);
        itemRepository.save(originalItem);
        Item resultItem = itemService.update(new Item(1, null, "new_description", null, null, user));
        Assertions.assertEquals(new Item(1, "test_name", "new_description", true, null, user), resultItem);
    }

    @Test
    void testItemUpdateOnlyAvailable() throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException,
            UserNotFoundException {
        User user = userService.add(new User(1, "test_name", "test@test.ru"));
        Item originalItem = new Item(1, "test_name", "test_description", true, null, user);
        itemRepository.save(originalItem);
        Item resultItem = itemService.update(new Item(1, null, null, false, null, user));
        Assertions.assertEquals(new Item(1, "test_name", "test_description", false, null, user), resultItem);
    }

    @Test
    void testItemUpdate() throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException,
            UserNotFoundException {
        User user = userService.add(new User(1, "test_name", "test@test.ru"));
        Item originalItem = new Item(1, "test_name", "test_description", true, null, user);
        itemRepository.save(originalItem);
        Item resultItem = itemService.update(new Item(1, "new_name", "new_description", false, null, user));
        Assertions.assertEquals(new Item(1, "new_name", "new_description", false, null, user), resultItem);
    }

    @Test
    void testItemUpdateThrowsItemNotFoundException() throws UserNotFoundException {
        User user = userService.add(new User(1, "test_name", "test@test.ru"));
        final ItemNotFoundException exception = Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.update(new Item(99, "test_name", "test_description", true, null, user)));
        Assertions.assertEquals("Item 99 not found", exception.getMessage());
    }

    @Test
    void testItemUpdateThrowsOwnerNotFoundException() throws UserNotFoundException {
        itemRepository.save(new Item(1, "test_name", "test_description", true, null,
                userService.add(new User(1, "test_name", "test@test.ru"))));
        final OwnerNotFoundException exception = Assertions.assertThrows(OwnerNotFoundException.class,
                () -> itemService.update(new Item(1, "test_name", "test_description", true, null,
                        new User(99, "test_name", "test@test.ru"))));
        Assertions.assertEquals("Owner 99 doesn't exist", exception.getMessage());
    }

    @Test
    void testItemUpdateAccessViolationException() throws UserNotFoundException {
        itemRepository.save(new Item(1, "test_name", "test_description", true, null,
                userService.add(new User(1, "test_name", "test@test.ru"))));
        User guestUser = userService.add(new User(2, "test_name_2", "test2@test.ru"));
        final AccessViolationException exception = Assertions.assertThrows(AccessViolationException.class,
                () -> itemService.update(new Item(1, "test_name", "test_description", true, null, guestUser)));
        Assertions.assertEquals("Access from user 2 to item 1 not granted", exception.getMessage());
    }
}

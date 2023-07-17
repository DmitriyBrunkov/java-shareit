package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    void testUserUpdateOnlyName() throws UserNotFoundException {
        User originalUser = new User(1, "test_name", "test@test.ru");
        userRepository.save(originalUser);
        User updatedUser = new User(1, "new_name", null);
        User resultUser = userService.update(updatedUser);
        Assertions.assertEquals(new User(1, "new_name", "test@test.ru"), resultUser);
    }

    @Test
    void testUserUpdateOnlyEmail() throws UserNotFoundException {
        User originalUser = new User(1, "test_name", "test@test.ru");
        userRepository.save(originalUser);
        User updatedUser = new User(1, null, "new@new.ru");
        User resultUser = userService.update(updatedUser);
        Assertions.assertEquals(new User(1, "test_name", "new@new.ru"), resultUser);
    }

    @Test
    void testUserUpdate() throws UserNotFoundException {
        User originalUser = new User(1, "test_name", "test@test.ru");
        userRepository.save(originalUser);
        User updatedUser = new User(1, "new_name", "new@new.ru");
        User resultUser = userService.update(updatedUser);
        Assertions.assertEquals(new User(1, "new_name", "new@new.ru"), resultUser);
    }

    @Test
    void testUserUpdateThrowsUserNotFoundException() {
        User originalUser = new User(99, "test_name", "test@test.ru");
        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.update(originalUser));
        Assertions.assertEquals("User 99 not found", exception.getMessage());
    }

    @Test
    void testUserDeleteThrowUserNotFoundException() {
        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.delete(99L));
        Assertions.assertEquals("User 99 not found", exception.getMessage());
    }
}

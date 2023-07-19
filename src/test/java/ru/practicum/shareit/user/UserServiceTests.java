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

    User originalUser = new User(1, "test_name", "test@test.ru");


    @Test
    void userUpdateOnlyNameTest() throws UserNotFoundException {
        userRepository.save(originalUser);
        User updatedUser = new User(1, "new_name", null);
        User resultUser = userService.update(updatedUser);
        Assertions.assertEquals(new User(1, "new_name", "test@test.ru"), resultUser);
    }

    @Test
    void userUpdateOnlyEmailTest() throws UserNotFoundException {
        userRepository.save(originalUser);
        User updatedUser = new User(1, null, "new@new.ru");
        User resultUser = userService.update(updatedUser);
        Assertions.assertEquals(new User(1, "test_name", "new@new.ru"), resultUser);
    }

    @Test
    void userUpdateTest() throws UserNotFoundException {
        userRepository.save(originalUser);
        User updatedUser = new User(1, "new_name", "new@new.ru");
        User resultUser = userService.update(updatedUser);
        Assertions.assertEquals(new User(1, "new_name", "new@new.ru"), resultUser);
    }

    @Test
    void userUpdateThrowsUserNotFoundExceptionTest() {
        originalUser.setId(99);
        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.update(originalUser));
        Assertions.assertEquals("User 99 not found", exception.getMessage());
    }

    @Test
    void userDeleteThrowUserNotFoundExceptionTest() {
        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.delete(99L));
        Assertions.assertEquals("User 99 not found", exception.getMessage());
    }

    @Test
    void getAllTest() throws Exception {
        userService.add(originalUser);
        Assertions.assertTrue(userService.getAll().contains(originalUser));
    }

    @Test
    void getTest() throws Exception {
        userService.add(originalUser);
        Assertions.assertEquals(originalUser, userService.get(1L));
    }

    @Test
    void existTest() throws Exception {
        userService.add(originalUser);
        Assertions.assertTrue(userService.exist(1L));
    }
}

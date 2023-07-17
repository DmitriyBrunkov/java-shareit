package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User get(Long id) throws UserNotFoundException;

    User add(User user) throws UserNotFoundException;

    User update(User user) throws UserNotFoundException;

    void delete(Long id) throws UserNotFoundException;

    boolean exist(Long id);
}

package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.exception.EmailNotUnique;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User get(Long id) throws UserNotFoundException;

    User add(User user) throws UserNotFoundException, EmailNotUnique;

    User update(User user) throws UserNotFoundException, EmailNotUnique;

    void delete(Long id) throws UserNotFoundException;
}

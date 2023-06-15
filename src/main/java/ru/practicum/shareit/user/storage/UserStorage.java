package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.exception.EmailNotUnique;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User get(Long id) throws UserNotFoundException;

    void add(User user) throws EmailNotUnique;

    void update(User user) throws UserNotFoundException, EmailNotUnique;

    void delete(Long id) throws UserNotFoundException;
}

package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.EmailNotUnique;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User get(Long id) throws UserNotFoundException {
        return userStorage.get(id);
    }

    @Override
    public User add(User user) throws UserNotFoundException, EmailNotUnique {
        userStorage.add(user);
        return userStorage.get(user.getId());
    }

    @Override
    public User update(User user) throws UserNotFoundException, EmailNotUnique {
        userStorage.update(user);
        return userStorage.get(user.getId());
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        userStorage.delete(id);
    }
}

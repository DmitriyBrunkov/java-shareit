package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.EmailNotUnique;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private static long availableId = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> emails = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(Long id) throws UserNotFoundException {
        if (!containsId(id)) {
            throw new UserNotFoundException("User " + id + " not found");
        }
        return users.get(id);
    }

    @Override
    public void add(User user) throws EmailNotUnique {
        if (containsEmail(user.getEmail())) {
            throw new EmailNotUnique("Email " + user.getEmail() + " is not unique");
        }
        user.setId(getAvailableId());
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user);
    }

    @Override
    public void update(User user) throws UserNotFoundException, EmailNotUnique {
        if (!containsId(user.getId())) {
            throw new UserNotFoundException("User " + user.getId() + " not found");
        }
        emails.remove(users.get(user.getId()).getEmail());
        if (!(user.getName() == null)) {
            users.get(user.getId()).setName(user.getName());
        }
        if (!(user.getEmail() == null)) {
            if (containsEmail(user.getEmail())) {
                throw new EmailNotUnique("Email " + user.getEmail() + " is not unique");
            }
            users.get(user.getId()).setEmail(user.getEmail());
        }
        emails.put(users.get(user.getId()).getEmail(), users.get(user.getId()));
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        if (!containsId(id)) {
            throw new UserNotFoundException("User " + id + " not found");
        }
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public boolean exist(Long id) {
        return users.containsKey(id);
    }

    private boolean containsId(Long id) {
        return users.containsKey(id);
    }

    private boolean containsEmail(String email) {
        return emails.containsKey(email);
    }

    private long getAvailableId() {
        availableId++;
        while (users.containsKey(availableId)) {
            availableId++;
        }
        return availableId;
    }
}

package ru.practicum.shareit.item.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    private static long availableId = 0;
    private final UserStorage userStorage;
    private final Map<Long, Item> items = new HashMap<>();

    @Autowired
    public InMemoryItemStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<Item> getUserItems(Long userId) throws UserNotFoundException {
        if (!(userStorage.exist(userId))) {
            throw new UserNotFoundException("User " + userId + " not found");
        }
        return items.values().stream().filter(i -> i.getOwner().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Item get(Long id) throws ItemNotFoundException {
        if (!containsId(id)) {
            throw new ItemNotFoundException("Item " + id + " not found");
        }
        return items.get(id);
    }

    @Override
    public List<Item> search(String searchString) {
        if (searchString == null || searchString.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream().filter(item -> (item.getName().toLowerCase()
                .contains(searchString.toLowerCase()) || item.getDescription().toLowerCase()
                .contains(searchString.toLowerCase())) && item.getAvailable()).collect(Collectors.toList());
    }

    @Override
    public void add(Item item) throws OwnerNotFoundException {
        if (!userStorage.exist(item.getOwner().getId())) {
            throw new OwnerNotFoundException("Owner " + item.getOwner() + " doesn't exist");
        }
        item.setId(getAvailableId());
        items.put(item.getId(), item);
    }

    @Override
    public void update(Item item) throws OwnerNotFoundException, AccessViolationException, ItemNotFoundException {
        if (!containsId(item.getId())) {
            throw new ItemNotFoundException("Item " + item.getId() + " not found");
        }
        if (!item.getOwner().equals(items.get(item.getId()).getOwner())) {
            throw new AccessViolationException("Access from user " + item.getOwner() + " to item " + item.getId() + " not granted");
        }
        if (!userStorage.exist(item.getOwner().getId())) {
            throw new OwnerNotFoundException("Owner " + item.getOwner() + " doesn't exist");
        }
        if (!(item.getName() == null)) {
            items.get(item.getId()).setName(item.getName());
        }
        if (!(item.getDescription() == null)) {
            items.get(item.getId()).setDescription(item.getDescription());
        }
        if (!(item.getAvailable() == null)) {
            items.get(item.getId()).setAvailable(item.getAvailable());
        }
    }

    private boolean containsId(Long id) {
        return items.containsKey(id);
    }

    private long getAvailableId() {
        availableId++;
        while (items.containsKey(availableId)) {
            availableId++;
        }
        return availableId;
    }
}

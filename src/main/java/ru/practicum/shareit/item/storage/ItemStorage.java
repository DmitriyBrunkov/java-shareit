package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface ItemStorage {
    List<Item> getUserItems(Long userId) throws UserNotFoundException;

    Item get(Long id) throws ItemNotFoundException;

    List<Item> search(String searchString);

    void add(Item item) throws UserNotFoundException, OwnerNotFoundException;

    void update(Item item) throws OwnerNotFoundException, AccessViolationException, ItemNotFoundException;
}

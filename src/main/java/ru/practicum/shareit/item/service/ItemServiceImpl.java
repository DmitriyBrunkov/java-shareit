package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public List<Item> getUserItems(Long userId) throws UserNotFoundException {
        return itemStorage.getUserItems(userId);
    }

    @Override
    public Item get(Long id) throws ItemNotFoundException {
        return itemStorage.get(id);
    }

    @Override
    public List<Item> search(String searchString) {
        return itemStorage.search(searchString);
    }

    @Override
    public Item add(Item item) throws ItemNotFoundException, OwnerNotFoundException, UserNotFoundException {
        itemStorage.add(item);
        return itemStorage.get(item.getId());
    }

    @Override
    public Item update(Item item) throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException {
        itemStorage.update(item);
        return itemStorage.get(item.getId());
    }
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface ItemService {
    List<Item> getUserItems(Long userId, int from, int size) throws UserNotFoundException;

    List<Item> getUserItems(Long userId) throws UserNotFoundException;

    Item get(Long id) throws ItemNotFoundException;

    List<Item> search(String searchString, int from, int size);

    Item add(Item item) throws ItemNotFoundException, UserNotFoundException, OwnerNotFoundException;

    Item update(Item item) throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException;

    boolean canUserComment(Long userId, Long itemId);

    Comment addComment(Comment comment);

    List<Comment> getCommentsByItemId(Long itemId);

    List<Item> getItemsByRequestId(Long requestId);


    List<Item> getRequestedItems();
}

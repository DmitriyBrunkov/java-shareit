package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<Item> getUserItems(Long userId) throws UserNotFoundException {
        return itemRepository.findItemsByOwner(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found")));
    }

    @Override
    public Item get(Long id) throws ItemNotFoundException {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item " + id + " not found"));
    }

    @Override
    public List<Item> search(String searchString) {
        if (searchString == null || searchString.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findItemsByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailable(searchString,
                searchString, true);
    }

    @Override
    public Item add(Item item) throws ItemNotFoundException, OwnerNotFoundException, UserNotFoundException {
        if (!userRepository.existsById(item.getOwner().getId())) {
            throw new OwnerNotFoundException("Owner " + item.getOwner() + " doesn't exist");
        }
        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item) throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException {
        Item originalItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new ItemNotFoundException("Item " + item.getId() + " not found"));
        if (!userRepository.existsById(item.getOwner().getId())) {
            throw new OwnerNotFoundException("Owner " + item.getOwner() + " doesn't exist");
        }
        if (!item.getOwner().equals(originalItem.getOwner())) {
            throw new AccessViolationException("Access from user " + item.getOwner() + " to item "
                    + item.getId() + " not granted");
        }
        if (!(item.getName() == null)) {
            originalItem.setName(item.getName());
        }
        if (!(item.getDescription() == null)) {
            originalItem.setDescription(item.getDescription());
        }
        if (!(item.getAvailable() == null)) {
            originalItem.setAvailable(item.getAvailable());
        }
        return itemRepository.save(originalItem);
    }

    @Override
    public boolean canUserComment(Long userId, Long itemId) {
        return bookingRepository.existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now());
    }

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.findCommentsByItem_Id(itemId);
    }
}

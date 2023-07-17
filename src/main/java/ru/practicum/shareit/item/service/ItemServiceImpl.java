package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<Item> getUserItems(Long userId, int from, int size) throws UserNotFoundException {
        return itemRepository.findItemsByOwner(userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User " + userId + " not found")),
                PageRequest.of(from > 0 ? from / size : 0, size,
                        Sort.by("id").descending())).toList();
    }

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
    public List<Item> search(String searchString, int from, int size) {
        if (searchString == null || searchString.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findItemsByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailable(searchString,
                searchString, true, PageRequest.of(from > 0 ? from / size : 0, size,
                        Sort.by("id").ascending())).toList();
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
            throw new OwnerNotFoundException("Owner " + item.getOwner().getId() + " doesn't exist");
        }
        if (!item.getOwner().equals(originalItem.getOwner())) {
            throw new AccessViolationException("Access from user " + item.getOwner().getId() + " to item "
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

    @Override
    public List<Item> getItemsByRequestId(Long requestId) {
        return itemRepository.findItemsByRequest_Id(requestId);
    }

    @Override
    public List<Item> getRequestedItems() {
        return itemRepository.findItemsByRequestIsNotNull();
    }
}

package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemValidationGroups.Add;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService, BookingService bookingService,
                          ItemRequestService itemRequestService) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.itemRequestService = itemRequestService;
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("id") Long id) throws ItemNotFoundException {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " itemId: " + id);
        ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemWithBookingDto(itemService.get(id),
                itemService.getCommentsByItemId(id).stream()
                        .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        if (userId.equals(itemService.get(id).getOwner().getId())) {
            Booking lastBooking = bookingService.getLastBookingByItem(id);
            if (lastBooking != null) {
                itemWithBookingDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
            }
            Booking nextBooking = bookingService.getNextBookingByItem(id);
            if (nextBooking != null) {
                itemWithBookingDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
            }
        }
        return itemWithBookingDto;
    }

    @GetMapping
    public List<ItemWithBookingDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size)
            throws UserNotFoundException {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " from: " + from + " size: " + size);
        return itemService.getUserItems(userId, from, size).stream().map(item -> {
            ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemWithBookingDto(item,
                    itemService.getCommentsByItemId(item.getId()).stream()
                            .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
            Booking lastBooking = bookingService.getLastBookingByItem(item.getId());
            if (lastBooking != null) {
                itemWithBookingDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
            }
            Booking nextBooking = bookingService.getNextBookingByItem(item.getId());
            if (nextBooking != null) {
                itemWithBookingDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
            }
            return itemWithBookingDto;
        }).sorted(Comparator.comparingLong(ItemWithBookingDto::getId)).collect(Collectors.toList());
    }


    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String searchString,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "20") int size) {
        log.info(this.getClass().getSimpleName() + ": GET: text: " + searchString + " from: " + " size: " + size);
        return itemService.search(searchString, from, size).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Add.class) ItemDto itemDto)
            throws ItemNotFoundException, UserNotFoundException, OwnerNotFoundException, ItemRequestNotFoundException {
        log.info(this.getClass().getSimpleName() + ": POST: userId: " + userId + " item: " + itemDto);
        itemDto.setOwner(userId);
        User user = userService.get(userId);
        return ItemMapper.toItemDto(itemService.add(ItemMapper.toItem(itemDto, user,
                itemDto.getRequestId() == null ? null : itemRequestService.getById(itemDto.getRequestId()))));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long id,
                          @RequestBody @Validated ItemDto itemDto)
            throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException, UserNotFoundException {
        log.info(this.getClass()
                .getSimpleName() + ": PATCH: userId: " + userId + " itemId: " + id + " item: " + itemDto);
        itemDto.setId(id);
        itemDto.setOwner(userId);
        User user = userService.get(userId);
        return ItemMapper.toItemDto(itemService.update(ItemMapper.toItem(itemDto, user, null)));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemId") Long itemId, @RequestBody CommentDto commentDto)
            throws ItemNotFoundException, UserNotFoundException, CommentAccessViolationException,
            CommentTextValidationException {
        log.info(this.getClass().getSimpleName() + ": POST: userId: " + userId + " itemId: " + itemId + " comment: "
                + commentDto);
        if (!itemService.canUserComment(userId, itemId)) {
            throw new CommentAccessViolationException("Only booker can comment");
        }
        if (commentDto.getText().isBlank()) {
            throw new CommentTextValidationException("text can't be blank");
        }
        Comment comment = new Comment();
        comment.setItem(itemService.get(itemId));
        comment.setAuthor(userService.get(userId));
        comment.setText(commentDto.getText());
        return CommentMapper.toCommentDto(itemService.addComment(comment));
    }
}

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

    @Autowired
    public ItemController(ItemService itemService, UserService userService, BookingService bookingService) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("id") Long id) throws ItemNotFoundException {
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
    public List<ItemWithBookingDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId)
            throws UserNotFoundException {
        return itemService.getUserItems(userId).stream().map(item -> {
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
    public List<ItemDto> search(@RequestParam(name = "text") String searchString) {
        return itemService.search(searchString).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Add.class) ItemDto itemDto)
            throws ItemNotFoundException, UserNotFoundException, OwnerNotFoundException {
        itemDto.setOwner(userId);
        User user = userService.get(userId);
        return ItemMapper.toItemDto(itemService.add(ItemMapper.toItem(itemDto, user)));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long id,
                          @RequestBody @Validated ItemDto itemDto)
            throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException, UserNotFoundException {
        itemDto.setId(id);
        itemDto.setOwner(userId);
        User user = userService.get(userId);
        return ItemMapper.toItemDto(itemService.update(ItemMapper.toItem(itemDto, user)));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemId") Long itemId, @RequestBody CommentDto commentDto)
            throws ItemNotFoundException, UserNotFoundException, CommentAccessViolationException,
            CommentTextValidationException {
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

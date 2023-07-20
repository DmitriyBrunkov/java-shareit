package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("id") Long id) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " itemId: " + id);
        return itemClient.get(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " from: " + from + " size: " + size);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "text") String searchString,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info(this.getClass().getSimpleName() + ": GET: text: " + searchString + " from: " + " size: " + size);
        return itemClient.search(userId, searchString, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody @Validated(ItemValidationGroups.Add.class) ItemDto itemDto) {
        log.info(this.getClass().getSimpleName() + ": POST: userId: " + userId + " item: " + itemDto);
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long id,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.info(this.getClass()
                .getSimpleName() + ": PATCH: userId: " + userId + " itemId: " + id + " item: " + itemDto);
        return itemClient.update(userId, id, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info(this.getClass().getSimpleName() + ": POST: userId: " + userId + " itemId: " + itemId + " comment: "
                + commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}

package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.AccessViolationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.ItemValidationGroups.Add;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable("id") Long id) throws ItemNotFoundException {
        return ItemMapper.toItemDto(itemService.get(id));
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) throws UserNotFoundException {
        return itemService.getUserItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String searchString) {
        return itemService.search(searchString).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Validated(Add.class) ItemDto itemDto) throws ItemNotFoundException, UserNotFoundException, OwnerNotFoundException {
        itemDto.setOwner(userId);
        return ItemMapper.toItemDto(itemService.add(ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long id,
                          @RequestBody @Validated ItemDto itemDto) throws AccessViolationException, OwnerNotFoundException, ItemNotFoundException {
        itemDto.setId(id);
        itemDto.setOwner(userId);
        return ItemMapper.toItemDto(itemService.update(ItemMapper.toItem(itemDto)));
    }
}

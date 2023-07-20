package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;


    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService, UserService userService, ItemService itemService) {
        this.itemRequestService = itemRequestService;
        this.userService = userService;
        this.itemService = itemService;
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Long requestorId, @RequestBody ItemRequestDto itemRequestDto) throws
            UserNotFoundException {
        log.info(this.getClass()
                .getSimpleName() + ": POST: userId: " + requestorId + " itemRequest: " + itemRequestDto);
        User requestor = userService.get(requestorId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.add(ItemRequestMapper.toItemRequest(itemRequestDto, requestor)));
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("requestId") Long requestId) throws
            ItemRequestNotFoundException, UserNotFoundException {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " requestId: " + requestId);
        if (!userService.exist(userId)) {
            throw new UserNotFoundException("User " + userId + " not found");
        }
        if (!itemRequestService.exist(requestId)) {
            throw new ItemRequestNotFoundException("Item Request " + requestId + " not found");
        }
        return ItemRequestMapper.toItemRequestWithItemDto(itemRequestService.getById(requestId), itemService.getItemsByRequestId(requestId)
                .stream().map(ItemMapper::toItemForRequestDto).collect(Collectors.toList()));
    }

    @GetMapping
    public List<ItemRequestWithItemDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) throws
            UserNotFoundException {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + requestorId);
        if (!userService.exist(requestorId)) {
            throw new UserNotFoundException("User " + requestorId + " not found");
        }
        return itemRequestService.getListByRequestorId(requestorId).stream()
                .map(i -> ItemRequestMapper.toItemRequestWithItemDto(i, itemService.getItemsByRequestId(i.getRequestor()
                        .getId()).stream().map(ItemMapper::toItemForRequestDto).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + userId + " from: " + from + " size: " + size);
        List<Long> validRequests = itemService.getRequestedItems().stream().filter(i -> i.getOwner().getId() != userId)
                .map(i -> i.getRequest().getId()).collect(Collectors.toList());
        return itemRequestService.getListOfAll(from, size).stream()
                .map(i -> ItemRequestMapper.toItemRequestWithItemDto(i, itemService.getItemsByRequestId(i.getRequestor()
                        .getId()).stream().map(ItemMapper::toItemForRequestDto).collect(Collectors.toList())))
                .filter(idto -> !validRequests.contains(idto.getId())).collect(Collectors.toList());
    }
}

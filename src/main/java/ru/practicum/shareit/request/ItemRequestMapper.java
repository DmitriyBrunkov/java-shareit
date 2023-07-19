package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), requestor, itemRequestDto.getCreated());
    }

    public static ItemRequestWithItemDto toItemRequestWithItemDto(ItemRequest itemRequest, List<ItemForRequestDto> items) {
        return new ItemRequestWithItemDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), items);
    }
}

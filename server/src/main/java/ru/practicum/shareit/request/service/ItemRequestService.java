package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest add(ItemRequest itemRequest);

    ItemRequest getById(Long itemRequestId) throws ItemRequestNotFoundException;

    List<ItemRequest> getListByRequestorId(Long requestorId);

    List<ItemRequest> getListOfAll(int from, int size);

    boolean exist(Long requestId);
}

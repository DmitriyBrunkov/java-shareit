package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequest add(ItemRequest itemRequest) {
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequest getById(Long itemRequestId) throws ItemRequestNotFoundException {
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Request " + itemRequestId + " not found"));
    }

    @Override
    public List<ItemRequest> getListByRequestorId(Long requestorId) {
        return itemRequestRepository.findItemRequestsByRequestor_IdOrderByCreatedDesc(requestorId);
    }

    @Override
    public List<ItemRequest> getListOfAll(int from, int size) {
        return itemRequestRepository.findAll(PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by("created").descending())).toList();
    }

    @Override
    public boolean exist(Long requestId) {
        return itemRequestRepository.existsById(requestId);
    }
}

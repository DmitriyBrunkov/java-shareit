package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findItemsByOwner(User owner, Pageable pageable);

    List<Item> findItemsByOwner(User owner);

    Page<Item> findItemsByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailable(String name,
                                                                                            String description,
                                                                                            Boolean available,
                                                                                            Pageable pageable);

    List<Item> findItemsByRequest_Id(Long requestId);

    List<Item> findItemsByRequestIsNotNull();

}

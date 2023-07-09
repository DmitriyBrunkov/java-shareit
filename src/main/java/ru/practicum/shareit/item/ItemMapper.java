package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner().getId());
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), user);
    }

    public static ItemWithBookingDto toItemWithBookingDto(Item item, List<CommentDto> comments) {
        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getOwner().getId());
        itemWithBookingDto.setComments(comments);
        return itemWithBookingDto;
    }
}

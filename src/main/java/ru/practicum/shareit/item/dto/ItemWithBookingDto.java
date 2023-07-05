package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.ItemValidationGroups;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemWithBookingDto {
    private long id;
    @NotBlank(groups = ItemValidationGroups.Add.class)
    private String name;
    @NotBlank(groups = ItemValidationGroups.Add.class)
    private String description;
    @NotNull(groups = ItemValidationGroups.Add.class)
    @AssertTrue(groups = ItemValidationGroups.Add.class)
    private Boolean available;
    private Long owner;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;

    public ItemWithBookingDto(long id, String name, String description, Boolean available, Long owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}

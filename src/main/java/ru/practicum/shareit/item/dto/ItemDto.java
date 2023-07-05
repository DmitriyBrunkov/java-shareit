package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.ItemValidationGroups.Add;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(groups = Add.class)
    private String name;
    @NotBlank(groups = Add.class)
    private String description;
    @NotNull(groups = Add.class)
    @AssertTrue(groups = Add.class)
    private Boolean available;
    private Long owner;
}

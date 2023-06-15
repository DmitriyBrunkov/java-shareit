package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.UserValidationGroups.Add;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = Add.class)
    private String name;
    @NotBlank(groups = Add.class)
    @Email
    private String email;
}

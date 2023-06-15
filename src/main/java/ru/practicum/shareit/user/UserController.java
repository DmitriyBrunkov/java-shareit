package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailNotUnique;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable("id") Long id) throws UserNotFoundException {
        return UserMapper.toUserDto(userService.get(id));
    }

    @PostMapping
    public UserDto add(@RequestBody @Validated(UserValidationGroups.Add.class) UserDto userDto) throws UserNotFoundException, EmailNotUnique {
        return UserMapper.toUserDto(userService.add(UserMapper.toUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long id,
                          @RequestBody @Validated UserDto userDto) throws UserNotFoundException, EmailNotUnique {
        userDto.setId(id);
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) throws UserNotFoundException {
        userService.delete(id);
    }
}

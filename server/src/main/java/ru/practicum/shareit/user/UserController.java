package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info(this.getClass().getSimpleName() + ": GET: all");
        return userService.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable("id") Long id) throws UserNotFoundException {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + id);
        return UserMapper.toUserDto(userService.get(id));
    }

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto)
            throws UserNotFoundException {
        log.info(this.getClass().getSimpleName() + ": POST: user: " + userDto);
        return UserMapper.toUserDto(userService.add(UserMapper.toUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long id, @RequestBody UserDto userDto)
            throws UserNotFoundException {
        log.info(this.getClass().getSimpleName() + ": PATCH: user: " + userDto);
        userDto.setId(id);
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) throws UserNotFoundException {
        log.info(this.getClass().getSimpleName() + ": DELETE: userId: " + id);
        userService.delete(id);
    }
}

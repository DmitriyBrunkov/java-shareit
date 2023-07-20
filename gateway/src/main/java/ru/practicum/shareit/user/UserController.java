package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info(this.getClass().getSimpleName() + ": GET: all");
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable("id") Long id) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: " + id);
        return userClient.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Validated(UserValidationGroups.Add.class) UserDto userDto) {
        log.info(this.getClass().getSimpleName() + ": POST: user: " + userDto);
        return userClient.add(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long id, @RequestBody @Valid UserDto userDto) {
        log.info(this.getClass().getSimpleName() + ": PATCH: user: " + userDto);
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        log.info(this.getClass().getSimpleName() + ": DELETE: userId: " + id);
        return userClient.delete(id);
    }
}

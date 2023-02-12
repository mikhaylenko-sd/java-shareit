package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> findAllUsers() {
        return userService.getAll();
    }

    @GetMapping(value = "/{userId}")
    public UserDto findUserById(@PathVariable("userId") int userid) {
        return userService.getById(userid);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping(value = "/{userId}")
    public UserDto patchUser(@PathVariable("userId") int userid, @RequestBody UserDto userDto) {
        userDto.setId(userid);
        return userService.update(userDto);
    }

    @DeleteMapping(value = "/{userId}")
    public void removeUserById(@PathVariable("userId") int userId) {
        userService.deleteById(userId);
    }
}

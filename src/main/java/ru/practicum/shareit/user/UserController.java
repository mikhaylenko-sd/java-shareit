package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserValidationService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserValidationService userValidationService;

    private static final String USER_ID_PATH_VARIABLE = "userId";

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.getAll();
    }

    @GetMapping(value = "/{" + USER_ID_PATH_VARIABLE + "}")
    public UserDto findUserById(@PathVariable(USER_ID_PATH_VARIABLE) int userid) {
        return userService.getById(userid);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        userValidationService.validateUserCreate(userDto);
        return userService.create(userDto);
    }

    @PatchMapping(value = "/{" + USER_ID_PATH_VARIABLE + "}")
    public UserDto patchUser(@PathVariable(USER_ID_PATH_VARIABLE) int userid, @RequestBody UserDto userDto) {
        userDto.setId(userid);
        userValidationService.validateUserUpdate(userDto);
        return userService.update(userDto);
    }

    @DeleteMapping(value = "/{" + USER_ID_PATH_VARIABLE + "}")
    public void removeUserById(@PathVariable(USER_ID_PATH_VARIABLE) int userId) {
        userService.deleteById(userId);
    }
}

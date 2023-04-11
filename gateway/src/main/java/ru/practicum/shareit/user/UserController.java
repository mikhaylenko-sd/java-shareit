package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private static final String USER_ID_PATH_VARIABLE = "userId";
    private final UserClient userClient;
    private final UserValidationService userValidationService;

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Получен запрос к эндпоинту: {} {}", "GET", "/users");
        return userClient.getAll();
    }

    @GetMapping(value = "/{" + USER_ID_PATH_VARIABLE + "}")
    public ResponseEntity<Object> findUserById(@PathVariable(USER_ID_PATH_VARIABLE) long userId) {
        log.info("Получен запрос к эндпоинту: {} /users/{}", "GET", userId);
        return userClient.getById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: {} {}", "POST", "/users");
        userValidationService.validateUserCreate(userDto);
        return userClient.create(userDto);
    }

    @PatchMapping(value = "/{" + USER_ID_PATH_VARIABLE + "}")
    public ResponseEntity<Object> patchUser(@PathVariable(USER_ID_PATH_VARIABLE) long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту: {} /users/{}", "PATCH", userId);
        userValidationService.validateUserUpdate(userDto);
        userDto.setId(userId);
        return userClient.update(userDto);
    }

    @DeleteMapping(value = "/{" + USER_ID_PATH_VARIABLE + "}")
    public void removeUserById(@PathVariable(USER_ID_PATH_VARIABLE) long userId) {
        log.info("Получен запрос к эндпоинту: {} /users/{}", "DELETE", userId);
        userClient.deleteById(userId);
    }
}

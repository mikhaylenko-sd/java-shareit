package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(int userId);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(UserDto userDto);

    void deleteById(int userId);
}

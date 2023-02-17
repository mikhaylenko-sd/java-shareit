package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(int userId) {
        User user = userRepository.getById(userId);
        if (user != null) {
            return UserMapper.toUserDto(user);
        } else {
            throw new UserNotFoundException(userId);
        }
    }

    @Override
    public UserDto create(UserDto userDto) {
        User userFromDto = UserMapper.toUser(userDto);
        User user = userRepository.create(userFromDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User updatedUser = userRepository.update(user);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userRepository.delete(user);
    }

    @Override
    public void deleteById(int userId) {
        userRepository.deleteById(userId);
    }
}

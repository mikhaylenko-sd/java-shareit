package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return UserMapper.toUserDto(user.orElseThrow(() -> new UserNotFoundException(userId)));
    }

    @Override
    public UserDto create(UserDto userDto) {
        User userFromDto = UserMapper.toUser(userDto);
        User user = userRepository.save(userFromDto);
        return UserMapper.toUserDto(user);
    }

    public UserDto update(UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        Long userId = newUser.getId();
        if (userId == null) {
            throw new IllegalArgumentException("Вы пытаетесь обновить несуществующего пользователя.");
        }

        User oldUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        merge(oldUser, newUser);
        userRepository.save(oldUser);
        return UserMapper.toUserDto(oldUser);
    }

    @Override
    public void delete(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userRepository.delete(user);
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

    private void merge(User oldUser, User newUser) {
        String name = newUser.getName();
        String email = newUser.getEmail();

        if (name != null) {
            oldUser.setName(name);
        }
        if (email != null) {
            oldUser.setEmail(email);
        }
    }
}

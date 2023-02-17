package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.GeneratorId;
import ru.practicum.shareit.exception.InvalidFieldException;
import ru.practicum.shareit.user.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final GeneratorId generatorId = new GeneratorId();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int userId) {
        return users.get(userId);
    }

    @Override
    public User create(User user) {
        checkUniqueEmail(user);
        int userId = generatorId.generate();
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("Вы пытаетесь обновить несуществующего пользователя.");
        }
        User oldUser = users.get(userId);
        if (!oldUser.getEmail().equals(user.getEmail())) {
            checkUniqueEmail(user);
        }
        merge(oldUser, user);
        users.put(userId, oldUser);
        return oldUser;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public void deleteById(int userId) {
        users.remove(userId);
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

    private void checkUniqueEmail(User user) {
        if (users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            throw new InvalidFieldException("Пользователь с данной электронной почтой уже существует.");
        }
    }
}

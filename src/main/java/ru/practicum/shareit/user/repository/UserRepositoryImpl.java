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
        if (isUserEmailUnique(user)) {
            int userId = generatorId.generate();
            user.setId(userId);
            if (!users.containsKey(userId)) {
                users.put(userId, user);
            } else {
                throw new IllegalArgumentException("Вы пытаетесь создать существующего User'а.");
            }
            return user;
        } else {
            throw new InvalidFieldException("This email already exists");
        }
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("Вы пытаетесь обновить несуществующего User'а.");
        } else {
            User oldUser = users.get(userId);
            if (!isUserEmailUnique(user)) {
                throw new InvalidFieldException("This email already exists");
            } else {
                merge(oldUser, user);
                users.put(userId, oldUser);
            }
            return oldUser;
        }
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

    private boolean isUserEmailUnique(User user) {
        return users.values().stream()
                .map(User::getEmail)
                .noneMatch(email -> email.equals(user.getEmail()));
    }
}

package ru.practicum.shareit.user.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.GeneratorId;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users;
    private final GeneratorId generatorId;

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
        if (isValid(user)) {
            user.setId(generatorId.generate());
            createIfContainsKey(user);
            return user;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            if (user.getEmail() != null && user.getName() != null) {
                users.put(userId, user);
                return user;
            }

            User oldUser = users.get(userId);
            if (user.getEmail() == null) {
                oldUser.setName(user.getName());
                users.put(userId, oldUser);
                return oldUser;
            } else if (user.getName() == null && isValid(user)) {
                oldUser.setEmail(user.getEmail());
                users.put(userId, oldUser);
                return oldUser;
            } else {
                throw new IllegalArgumentException("Вы пытаетесь обновить несуществующего User'а.");
            }
        } else {
            throw new IllegalArgumentException("Вы пытаетесь обновить несуществующего User'а.");
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

    private void createIfContainsKey(User user) {
        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new IllegalArgumentException("Вы пытаетесь создать существующего User'а.");
        }
    }

    private boolean isValid(User user) {
        long count = users.values().stream()
                .map(User::getEmail)
                .filter(email -> email.equals(user.getEmail()))
                .count();
        if (count == 0) {
            return true;
        } else {
            return false;
        }
    }
}

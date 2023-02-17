package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    User getById(int userId);

    User create(User user);

    User update(User user);

    void delete(User user);

    void deleteById(int userId);

}

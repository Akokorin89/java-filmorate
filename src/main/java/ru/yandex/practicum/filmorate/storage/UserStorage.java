package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;


public interface UserStorage {

    User createUser(User user);

    Collection<User> getAllUsers();

    User updateUser(User user);

    User getUser(int id);

    HashMap<Integer, User> getUsers();

    void deleteUser(int id);
        }

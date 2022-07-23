package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        if ((userId > 0 && friendId > 0) || ((userStorage.getUser(userId) != null
                && userStorage.getUser(friendId) != null))) {
            userStorage.getUser(userId).getFriends().add(friendId);
            userStorage.getUser(friendId).getFriends().add(userId);
            log.info("Пользователь {} добавил в друзья {}", userId, friendId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", userStorage.getUser(userId).getId()));
        }
    }

    public Collection<Integer> getMutualFriends(User user, User user2) {
        Collection<Integer> mutualFriends = new HashSet<>();
        for (int u : user.getFriends()) {
            for (int u2 : user2.getFriends()) {
                if (u == u2) {
                    mutualFriends.add(u);
                }
            }
        }
        return mutualFriends;
    }

    public void deleteFriend(User user, User user2) {
        if (!user.getFriends().contains(user2.getId())) {
            //прокинуть нужное исключение
            throw new NoSuchElementException();
        }
        user.getFriends().remove(user2.getId());
    }

    public Collection<User> getAllFriends(int id) {
        Set<Integer> friendsId = userStorage.getUser(id).getFriends();
        return friendsId.stream().map(r -> {
            log.debug("Передан список  друзей пользователя {}", id);
            return userStorage.getUser(r);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }
}

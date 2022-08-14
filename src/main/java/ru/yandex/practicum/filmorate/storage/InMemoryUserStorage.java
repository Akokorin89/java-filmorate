package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer id;

    @Override
    public User createUser(User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("Указана неверная почта", "id", String.valueOf(user.getId()));
            } else if (users.containsValue(user)) {
                throw new ValidationException("Пользователь с такой почтой уже существует", "id", String.valueOf(user.getId()));
            } else if (user.getLogin() == null || user.getLogin().isBlank()) {
                throw new ValidationException("Логин пустой", "id", String.valueOf(user.getId()));
            } else if (user.getName() == null || user.getName().isBlank()) {
                user.setId(makeId());
                setName(user);
                System.out.println("Имя отсутствует, используем логин");
                users.put(user.getId(), user);
                log.info("User добавлен");
            } else {
                user.setId(makeId());
                users.put(user.getId(), user);
                log.info("User добавлен");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }

        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Получен запрос User.");
        return users.values();
    }

    @Override
    public User updateUser(User user) {
        // Проверить
        int result = user.getId();
        if (result != -1) {
            users.put(result, user);
            return users.get(result);
        }
        return user;
    }

    @Override
    public User getUser(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с id %d не найден", id));
        }
    }

    private User findUser(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с id %d не найден", id));
        }
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User deleteUser(int id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void removeAll() {
        users.clear();
    }

    private void setName(User user) {
        if (user.getName() == null) {
            log.warn("Имя не введено");
            user.setName(user.getLogin());
        } else if (user.getName().isBlank()) {
            log.warn("Введено пустое имя");
            user.setName(user.getLogin());
        }
    }

    private Integer makeId() {
        if (id == null) {
            id = 1;
        } else if (id < 0) {
            throw new ValidationException("Некорректный id", "id", String.valueOf(users.get(getUser(id)).getId()));
        } else {
            id++;
        }
        return id;
    }

    public void addFriend(int userId, int friendId) {
        if ((userId > 0 && friendId > 0) || ((getUser(userId) != null
                && getUser(friendId) != null))) {
            findUser(userId).getFriends().add(friendId);
            findUser(friendId).getFriends().add(userId);
            log.info("Пользователь {} добавил в друзья {}", userId, friendId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", findUser(userId).getId()));
        }
    }

    @Override
    public List<User> getMutualFriends(int firstUserId, int secondUserId) {
        return null;
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

    @Override
    public void deleteFriend(int firstUserId, int secondUserId) {
        User user = getUser(firstUserId);
        User user2 = getUser(secondUserId);
        if (!user.getFriends().contains(user2.getId())) {
            //прокинуть нужное исключение
            throw new NoSuchElementException();
        }
        user.getFriends().remove(user2.getId());
    }

    public Collection<User> getAllFriends(int id) {
        Set<Integer> friendsId = findUser(id).getFriends();
        return friendsId.stream().map(r -> {
            log.debug("Передан список  друзей пользователя {}", id);
            return findUser(r);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}

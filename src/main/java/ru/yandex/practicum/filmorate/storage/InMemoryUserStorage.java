package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer id;

    @Override
    public User createUser(User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("Указана неверная почта");
            } else if (users.containsValue(user)) {
                throw new ValidationException("Пользователь с такой почтой уже существует");
            } else if (user.getLogin() == null || user.getLogin().isBlank()) {
                throw new ValidationException("Логин пустой");
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
        if (user.getId() != null) {
            if (users.containsKey(user.getId())) {
                setName(user);
                users.put(user.getId(), user);
                log.info("Обновлена информация о пользователе {}, id={}", user.getName(), user.getId());
                return user;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с id %d не найден", user.getId()));
            }
        } else {
            createUser(user);
            return user;
        }
    }

    @Override
    public User getUser(int id) {
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
    public void deleteUser(int id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
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
            throw new ValidationException("Некорректный id");
        } else {
            id++;
        }
        return id;
    }
}

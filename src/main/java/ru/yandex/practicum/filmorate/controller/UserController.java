package ru.yandex.practicum.filmorate.controller;


import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private HashMap<Integer, User> userList = new HashMap<>();
    private Integer idCount = 0;

    @GetMapping
    public Collection<User> getUser() {
        log.info("Получен запрос User.");
        return userList.values();
    }


    //Разобраться с исключениями
    @PostMapping
    public User createUser(@Valid @RequestBody User user)  {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("Указана неверная почта");
            } else if (userList.containsValue(user)) {
                throw new ValidationException("Пользователь с такой почтой уже существует");
            } else if (user.getLogin() == null || user.getLogin().isBlank()) {
                throw new ValidationException("Логин пустой");
            } else if (user.getName() == null || user.getName().isBlank()) {
                user.setId(++idCount);
                user.setName(user.getLogin());
                System.out.println("Имя отсутствует, используем логин");
                userList.put(user.getId(), user);
                log.info("User добавлен");
            } else {
                user.setId(++idCount);
                userList.put(user.getId(), user);
                log.info("User добавлен");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Указана неверная почта");
        } else if ((user.getId() == null) || (user.getId() <= 0)) {
            throw new ValidationException("Некорректный id");
        } else {
            userList.put(user.getId(), user);
            log.info("User обновлен");
            return user;
        }
    }


}

package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
        return user;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        userService.addFriend(id, friendId);
        return userService.getUser(id);
    }


    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        userService.deleteFriend(getUser(id), getUser(friendId));
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriend(@PathVariable("id") int id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    private Collection<User> commonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer otherId) {
        try {
            var user1 = userService.getUser(userId);
            var user2 = userService.getUser(otherId);
            return userService.getMutualFriends(user1, user2).stream().map(userService::getUser).collect(Collectors.toSet());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}




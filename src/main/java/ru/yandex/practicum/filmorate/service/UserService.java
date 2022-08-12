package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.storage = storage;
    }


    public void addFriend(int firstUserId, int secondUserId) {
         storage.addFriend(firstUserId, secondUserId);
    }

    public void removeFriends(int firstUserId, int secondUserId) {
         storage.deleteFriend(firstUserId, secondUserId);
    }

    public Collection<User> getMutualFriends(int firstUserId, int secondUserId) {
        return storage.getMutualFriends(firstUserId,secondUserId);
    }

    public Collection<User> getAllUsers() { return storage.getAllUsers(); }

    public User getUserById(int id) {
        return storage.getUser(id);
    }

    public Collection<User> getAllFriends(int id) {
        return storage.getAllFriends(id);
    }

    public User createUser(User user) { return storage.createUser(user); }

    public User updateUser(int id, User user) {
        return storage.updateUser(user);
    }
}

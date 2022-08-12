package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;


public interface UserStorage {

    User createUser(User user);

    Collection<User> getAllUsers();

    User updateUser(User user);

    User getUser(int userId);

    User deleteUser(int id);

    void removeAll();

    void addFriend(int userId, int friendId);

    List<User> getMutualFriends(int firstUserId, int secondUserId);

    void deleteFriend(int firstUserId, int secondUserId);

    public Collection<User> getAllFriends(int id);


}

package ru.yandex.practicum.filmorate.storage.implement.dao;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;
@Slf4j
@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("users")
                .usingColumns("user_login", "user_email", "user_name", "user_birthday")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKeyHolder(Map.of("user_login", user.getLogin(),
                        "user_email", user.getEmail(),
                        "user_name", user.getName(),
                        "user_birthday", Date.valueOf(user.getBirthday())))
                .getKeys();
        user.setId((Integer) keys.get("user_id"));
        return user;
    }


    @Override
    public User deleteUser(int userId) {
        String sqlQuerySearch = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
        Optional<User> result = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToUser, userId));
        if (result.isEmpty()) return null;
        String sqlQuery = "DELETE FROM users where user_id = ?";
        jdbcTemplate.update(sqlQuery, userId);
        return result.get();
    }

    @Override
    public void removeAll() {
        String sqlQuery = "DELETE FROM users";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public User updateUser(User user) {
        try {
            String sqlQuerySearch = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
            User result = jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToUser, user.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", user.getId()));
        }
        String sqlQuery = "UPDATE users SET user_email = ?, user_login = ?, user_name = ?, user_birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        String sqlQuerySecondSearch = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuerySecondSearch, this::mapRowToUser, user.getId());
    }

    @Override
    public User getUser(int userId) {
        try {
            String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с id %d не найден", userId));
        }
    }

    @Override
    public Collection<User> getAllFriends(int userId) {
        try {
            String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users WHERE user_id IN " +
                    "(SELECT to_id FROM friendships WHERE from_id = ? AND is_approved = true)";
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            //???
            return null;
        }
    }


    public List<User> getUsers(int limit, int offset) {
        String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, limit, offset);
    }

    @Override
    public Collection<User> getAllUsers() {
        String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public void addFriend(int firstUserId, int secondUserId) {
        if ((firstUserId > 0 && secondUserId > 0) || ((getUser(firstUserId) != null
                && getUser(secondUserId) != null))) {
            String sqlQuery = "INSERT INTO friendships VALUES (?, ?, true)";
            jdbcTemplate.update(sqlQuery, firstUserId, secondUserId);
            log.info("Пользователь {} добавил в друзья {}", firstUserId, secondUserId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id %d не найден", firstUserId));
        }
    }

@Override
    public void deleteFriend(int firstUserId, int secondUserId) {
        String sqlQuery = "DELETE FROM friendships WHERE (from_id, to_id) IN ((?, ?)) AND is_approved = true";
        jdbcTemplate.update(sqlQuery, firstUserId, secondUserId);
    }

@Override
    public List<User> getMutualFriends(int firstUserId, int secondUserId) {
        List<User> mutualFriends = new ArrayList<>();
        try {
            String sqlQuery = "SELECT user_id, user_email, user_login, user_name, user_birthday " +
                    "FROM users WHERE user_id IN (SELECT to_id FROM friendships WHERE from_id = ? AND is_approved = true " +
                    "INTERSECT SELECT to_id FROM friendships WHERE from_id = ? AND is_approved = true)";
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, firstUserId, secondUserId);
        } catch (EmptyResultDataAccessException e) {
            return mutualFriends;
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User result = new User();
        result.setId(resultSet.getInt("user_id"));
        result.setEmail(resultSet.getString("user_email"));
        result.setLogin(resultSet.getString("user_login"));
        result.setName(resultSet.getString("user_name"));
        result.setBirthday(resultSet.getDate("user_birthday").toLocalDate());
        return result;
    }
}

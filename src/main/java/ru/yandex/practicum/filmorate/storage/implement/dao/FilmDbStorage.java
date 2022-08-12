package ru.yandex.practicum.filmorate.storage.implement.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Slf4j
@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;


    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films").usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());
        if (film.getGenres() != null) {
            SimpleJdbcInsert secondSimpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film_genres").usingColumns("film_id", "genre_id");
            film.getGenres().forEach(g -> secondSimpleJdbcInsert.execute(Map.of("film_id", film.getId(), "genre_id", g.getId())));
        }
        return film;
    }

    public void removeFilm(int filmId) {
        String sqlQuerySearch = "SELECT film_id, film_name, film_description, film_release_date, film_duration " + "FROM films WHERE film_id = ?";
        Optional<Film> result = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToFilm, filmId));
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public void removeAll() {
        String sqlQuery = "DELETE FROM films";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() > 0) {
            String sqlQuerySearch = "SELECT film_id, film_name, film_description, film_release_date, film_duration, mpa_id " + "FROM films WHERE film_id = ?";
            jdbcTemplate.queryForObject(sqlQuerySearch, this::mapRowToFilm, film.getId());

            String sqlQuery = "UPDATE films SET film_name = ?, film_description = ?, film_release_date = ?, film_duration = ?, mpa_id = ? WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

            if (film.getGenres() != null) {
                String sqlQueryGenresRemove = "DELETE FROM film_genres WHERE film_id = ?";
                jdbcTemplate.update(sqlQueryGenresRemove, film.getId());
                SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film_genres").usingColumns("film_id", "genre_id");
                film.getGenres().forEach(g -> simpleJdbcInsert.execute(Map.of("film_id", film.getId(), "genre_id", g.getId())));
            }
            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
                    genres.addAll(film.getGenres());
                    film.setGenres(genres);
                }
            }
            return film;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Фильм с id %d не найден", film.getId()));
        }
    }

    @Override
    public Film getFilm(int filmId) {
        if (filmId > 0) {
            String sqlQuery = "SELECT film_id, film_name, film_description, film_release_date, film_duration, mpa_id " + "FROM films WHERE film_id = ?";
            Optional<Film> result = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId));
            Film film = result.get();
            if (result.isPresent()) {

                String sqlQueryGenres = "SELECT g.genre_id, g.genre_name FROM film_genres AS fg " + "JOIN genres AS g ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
                List<Genre> genres = jdbcTemplate.query(sqlQueryGenres, this::mapRowToGenre, filmId);
                if (genres.size() > 0) {
                    film.setGenres(new HashSet<>());
                    genres.forEach(g -> film.getGenres().add(g));
                }
            }
            String sqlQueryRating = "SELECT mpa_id, mpa_name FROM mpa WHERE mpa_id = ?";
            Optional<Mpa> rating = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQueryRating, this::mapRowToRating, film.getMpa().getId()) //??
            );
            rating.ifPresent(film::setMpa);
            return film;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Фильм с id %d не найден", filmId));
        }

    }


    public List<Film> getFilms(int limit, int offset) {
        String sqlQuery = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, limit, offset);
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT film_id, film_name, film_description, film_release_date, film_duration FROM films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public void addLike(int id, int userId) {
        if (id > 0 && userId > 0) {
            log.info("Пользователь id {} добавил лайк фильму id {}", userId, id);
            String sqlQuery = "INSERT INTO films_liked (user_id, film_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Фильм или пользователь с id не найден"));
        }
    }

    @Override
    public void deleteLike(int id, int userId) {
        if (id > 0 && userId > 0) {
            log.info("Пользователь id {} удалил лайк фильму id {}", userId, id);
            String sqlQuery = "DELETE FROM films_liked WHERE (user_id, film_id) IN ((?, ?))";
            jdbcTemplate.update(sqlQuery, userId, id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Фильм или пользователь с id не найден"));
        }
    }

    public List<Film> getTopFilms(int amount) {
        String sqlQuery = "SELECT f.film_id, film_name, film_description, film_release_date, film_duration, mpa_id " + "FROM films AS f " + "LEFT JOIN  films_liked AS fl " + "ON f.film_id = fl.film_id " + "GROUP BY f.film_id " + "ORDER BY COUNT(DISTINCT fl.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, amount);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film result = new Film();
        result.setId(resultSet.getInt("film_id"));
        result.setName(resultSet.getString("film_name"));
        result.setDescription(resultSet.getString("film_description"));
        result.setReleaseDate(resultSet.getDate("film_release_date").toLocalDate());
        result.setDuration(resultSet.getDouble("film_duration"));
        Mpa rating = new Mpa();
        rating.setId(resultSet.getInt("mpa_id"));
        result.setMpa(rating);
        return result;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre result = new Genre();
        result.setId(resultSet.getInt("genre_id"));
        result.setName(resultSet.getString("genre_name"));
        return result;
    }

    private Mpa mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa result = new Mpa();
        result.setId(resultSet.getInt("mpa_id"));
        result.setName(resultSet.getString("mpa_name"));
        return result;
    }
}
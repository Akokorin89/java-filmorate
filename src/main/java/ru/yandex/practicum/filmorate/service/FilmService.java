package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    public Collection<Film> getMostPopular(int count) {
        log.info("Передан список из {} популярных фильмов", count);
        return filmStorage.getAllFilms().stream().sorted(Comparator.comparingInt(r -> -r.getLikes().size())).limit(count).collect(Collectors.toList());
    }

    public void addLike(int id, int userId) {
        if (filmStorage.getFilm(id) != null) {
            filmStorage.getFilm(id).getLikes().add(userId);
            filmStorage.updateFilm(getFilm(id));
            log.info("Пользователь id {} добавил лайк фильму id {}", userId, id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Фильм или пользователь с id не найден"));
        }
    }

    public void deleteLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film.getLikes().contains(userId) || (id > 0 && userId > 0)) {
            film.getLikes().remove(userId);
            log.info("Пользователь id {} удалил лайк фильму id {}", userId, id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Фильм или пользователь с id не найден"));
        }
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public void deleteFilm(int filmId) {
        filmStorage.getFilm(filmId);
    }
}

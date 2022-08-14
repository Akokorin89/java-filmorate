package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private Integer id;

    @Override
    public Film addFilm(Film film) {
        film.setId(makeId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм {}, id={}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        //throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
        return films.values();

    }

    @Override
    public void removeFilm(int filmId) {
        films.remove(filmId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() != null) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Обновлена информация о фильме {}, id={}", film.getName(), film.getId());
                return film;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Фильм с id %d не найден", film.getId()));
            }
        } else {
            film.setId(makeId());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм {}, id={}", film.getName(), film.getId());
            return film;
        }
    }

    @Override
    public Film getFilm(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Фильм с id %d не найден", id));
        }
    }

    private Integer makeId() {
        if (id == null) {
            id = 1;
        } else if (id < 0) {
            throw new ValidationException("Некорректный id", "id", String.valueOf(getFilm(id).getId()));
        } else {
            id++;
        }
        return id;
    }

    public List<Film> getTopFilms(int amount) {
        log.info("Передан список из {} популярных фильмов", amount);
        return getAllFilms().stream().sorted(Comparator.comparingInt(r -> -r.getLikes().size())).limit(amount).collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilms(int limit, int offset) {
        return null;
    }

    @Override
    public void removeAll() {
        films.clear();
    }

    @Override
    public void addLike(int id, int userId) {
        if (getFilm(id) != null) {
            getFilm(id).getLikes().add(userId);
            updateFilm(getFilm(id));
            log.info("Пользователь id {} добавил лайк фильму id {}", userId, id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Фильм или пользователь с id не найден"));
        }
    }

    @Override
    public void deleteLike(int id, int userId) {
        Film film = getFilm(id);
        if (film.getLikes().contains(userId) || (id > 0 && userId > 0)) {
            film.getLikes().remove(userId);
            log.info("Пользователь id {} удалил лайк фильму id {}", userId, id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Фильм или пользователь с id не найден"));
        }
    }
}

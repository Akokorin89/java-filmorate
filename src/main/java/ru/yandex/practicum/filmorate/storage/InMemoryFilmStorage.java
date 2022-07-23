package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void deleteFilm(int filmId) {

        films.remove(filmId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Override
    public HashMap<Integer, Film> getFilms() {
        return  films;
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
            id = 1;}
            else if(id < 0) {
            throw new ValidationException("Некорректный id");
        } else {
            id++;
        }
        return id;
    }
}

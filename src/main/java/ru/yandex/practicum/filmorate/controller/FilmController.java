package ru.yandex.practicum.filmorate.controller;


import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private HashMap<Integer, Film> filmList = new HashMap<>();

    private Integer idCount = 0;

    //получение всех фильмов
    @GetMapping
    public Collection<Film> getAllFilm() {
        log.info("Получен запрос. Film");
        return filmList.values();

    }

    //добавление фильма
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film)  {
        try {
            if (film.getName() == null) {
                throw new ValidationException("Фильм должен как-то называться");
            } else {
                film.setId(++idCount);
                filmList.put(film.getId(), film);
                log.info("Film добавлен");
            }
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return film;
    }

    //обновление фильма
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getName() == null) {
            throw new ValidationException("Фильм должен как-то называться");
        } else if ((film.getId() == null) || (film.getId() <= 0)) {
            throw new ValidationException("Некорректный id");
        } else {
            filmList.put(film.getId(), film);
            log.info("Film обновлен");
            return film;
        }
    }
}

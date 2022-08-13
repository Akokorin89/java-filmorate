package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film){
        filmService.addFilm(film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") int id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) throws NotFoundException {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@Valid @RequestParam(defaultValue = "10") int count) {
        return filmService.showTopFilms(count);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@Valid @PathVariable("filmId") int filmId) {
        filmService.removeFilm(filmId);
    }

}

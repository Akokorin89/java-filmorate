package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;


@Component
public interface FilmStorage {


    Film addFilm(Film film);

    HashMap<Integer, Film> getFilms();

    Film updateFilm(Film film);

    Film getFilm(int id);

    Collection<Film> getAllFilms();

    void deleteFilm(int filmId);

}

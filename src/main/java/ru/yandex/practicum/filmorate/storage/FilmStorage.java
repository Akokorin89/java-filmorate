package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;


@Component
public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(int filmId);

    Collection<Film> getAllFilms();

      void deleteLike(int id, int userId);

    void addLike(int id,int userId);

    List<Film> getTopFilms(int amount);

    void removeFilm(int filmId);

    List<Film> getFilms(int limit, int offset);

    void removeAll();


}

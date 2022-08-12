package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;


    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }
    public void addLike(int id, int userId) {
      filmStorage.addLike(id,userId);
    }
    public void deleteLike(int id, int userId) {
        filmStorage.deleteLike(id,userId);
    }

    public List<Film> showTopFilms(int amount) {
        return filmStorage.getTopFilms(amount);
    }

    public List<Film> getAllFilms() { return (List<Film>) filmStorage.getAllFilms(); }

    public Film getFilmById(int id) {
        return filmStorage.getFilm(id);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addFilm(Film film) throws NotFoundException { return filmStorage.addFilm(film); }

    public void removeFilm(int filmId){
         filmStorage.removeFilm(filmId);
    }

}

package ru.yandex.practicum.filmorate.storage.implement.dao;


import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public interface GenreDao {
    Genre findGenreById(Integer genreId) throws NotFoundException;

    Collection<Genre> findAllGenre();

    Genre createGenre(ResultSet rs) throws SQLException;
}
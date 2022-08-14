package ru.yandex.practicum.filmorate.storage.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.implement.dao.GenreDao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreDaoImp implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre findGenreById(Integer genreId) throws NotFoundException {
        if (genreId < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Mpa_id не может быть отрицательный", genreId));
        } else {
            String sql = "SELECT * FROM GENRES WHERE GENRE_ID =?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> createGenre(rs), genreId).stream().findAny()
                    .orElseThrow(() -> new NotFoundException("Жанр не найден", "id", String.valueOf(genreId)));
        }
    }
    @Override
    public Collection<Genre> findAllGenre() {
        String sql = "SELECT * FROM GENRES ORDER BY GENRE_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createGenre(rs));
    }

    public Genre createGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("GENRE_ID");
        String name = rs.getString("GENRE_NAME");
        return new Genre(id, name);
    }
}

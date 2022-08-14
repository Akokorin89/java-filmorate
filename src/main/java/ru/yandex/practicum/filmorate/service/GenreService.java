package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.implement.dao.GenreDao;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreDao genreDao;

    public Collection<Genre> getAll() {
        log.info("Передан список Всех Жанров");
        return genreDao.findAllGenre();
    }

    public Genre getById(int id) throws NotFoundException {
        log.info("Запрошенный жанр id = {}",id);
        return genreDao.findGenreById(id);
    }
}

package ru.yandex.practicum.filmorate.storage.implement.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDao {
    Mpa findMpaById(Integer mpaId);
    Collection<Mpa> findAllMpa();
}

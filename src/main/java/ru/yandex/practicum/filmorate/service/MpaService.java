package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.implement.dao.MpaDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaDao mpaDao;

    public Collection<Mpa> getAll() {
        log.info("Передан Список Всех MPA");
        return mpaDao.findAllMpa();
    }

    public Mpa getById(int id) {
        log.info("Передан Mpa id = {}", id);
        return mpaDao.findMpaById(id);
    }
}

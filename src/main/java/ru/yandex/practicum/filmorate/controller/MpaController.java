package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;


import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/mpa")
    public Collection<Mpa> getAll() {
        return mpaService.getAll();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getById(@PathVariable @Min(1) int id) throws NotFoundException {
        return mpaService.getById(id);
    }
}

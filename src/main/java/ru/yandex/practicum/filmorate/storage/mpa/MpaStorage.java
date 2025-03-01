package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {

    Collection<Mpa> getMpas();

    Optional<Mpa> getMpaById(Long id);
}
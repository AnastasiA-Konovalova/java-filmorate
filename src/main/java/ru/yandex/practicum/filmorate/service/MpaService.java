package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mapping.MpaMapperToDto;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;
    private final MpaMapperToDto mpaMapperToDto;

    public Collection<MpaDto> getMpas() {
        List<Mpa> mpas = (List<Mpa>) mpaDbStorage.getMpas();

        return mpas.stream()
                .map(mpa -> mpaMapperToDto.toDto(mpa))
                .toList();
    }

    public MpaDto getMpaById(Long id) {
        Mpa mpa = mpaDbStorage.getMpaById(id);
        if (mpa == null) {
            log.warn("Неправильно введен id рейтинга");
            throw new NotFoundException("Рейтинг с таким id отсутствует");
        }
        return mpaMapperToDto.toDto(mpa);
    }
}
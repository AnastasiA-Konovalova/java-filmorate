package ru.yandex.practicum.filmorate.service.mapping;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@Service
public class MpaMapperToDto {
    public static MpaDto toDto(Mpa mpa) {
        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(mpa.getId());
        mpaDto.setName(mpa.getName());

        return mpaDto;
    }
}
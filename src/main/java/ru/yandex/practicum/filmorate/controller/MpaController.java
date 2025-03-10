package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor

public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaDto> getMpa() {
        return mpaService.getMpas();
    }

    @GetMapping("/{id}")
    public MpaDto getMpaById(@PathVariable Long id) {
        return mpaService.getMpaById(id);
    }
}
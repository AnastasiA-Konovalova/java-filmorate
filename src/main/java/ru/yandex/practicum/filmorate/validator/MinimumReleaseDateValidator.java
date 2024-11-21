package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.annotation.MinimumReleaseDate;

import java.time.LocalDate;

@Slf4j
public class MinimumReleaseDateValidator implements ConstraintValidator<MinimumReleaseDate, LocalDate> {

    private final LocalDate releaseDateMin = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            log.error("Ошибка. Неверная дата релиза", new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года"));
            return true;
        }
        return !localDate.isBefore(releaseDateMin);
    }
}


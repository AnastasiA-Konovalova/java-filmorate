package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mapping.UserMapperToDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class UserRowMapper implements RowMapper<User> {
    private final String id = "id";
    private final String login = "login";
    private final String name = "name";
    private final String email = "email";
    private final String birthday = "birthday";

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong(id));
        user.setLogin(resultSet.getString(login));
        user.setName(resultSet.getString(name));
        user.setEmail(resultSet.getString(email));
        Timestamp birthdayDate = resultSet.getTimestamp(birthday);
        user.setBirthday(birthdayDate.toLocalDateTime().toLocalDate());

        return user;
    }
}

package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_IDS_QUERY = "SELECT * FROM users WHERE id IN (%s)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users(id, email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, " +
            "name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_ALL = "DELETE FROM users";

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;


    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query(FIND_ALL_QUERY, userRowMapper);
    }

    @Override
    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, userRowMapper, id);
    }

    @Override
    public List<User> getUserByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList(); // Если `ids` пуст, возвращаем пустой список.
        }

        StringBuilder builderHolder = new StringBuilder();
        for (Long id : ids) {
            if (!builderHolder.isEmpty()) {
                builderHolder.append(", ");
            }
            builderHolder.append("?");
        }

        String placeholders = builderHolder.toString();
        String query = String.format(FIND_BY_IDS_QUERY, placeholders);
        // Преобразуем `ids` в массив параметров
        Object[] params = ids.toArray();

        return jdbcTemplate.query(query, userRowMapper, params);
    }

    @Override
    public User createUser(User user) {
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        jdbcTemplate.update(connection -> {
//            PreparedStatement ps = connection.prepareStatement(
//                    INSERT_QUERY,
//                    new String[]{"id"} // Укажите, что хотите вернуть сгенерированный идентификатор
//            );
//            ps.setString(1, user.getEmail());
//            ps.setString(2, user.getLogin());
//            ps.setString(3, user.getName());
//            ps.setDate(4, Date.valueOf(user.getBirthday()));
//            return ps;
//        }, keyHolder);
//
//        Long id = keyHolder.getKey().longValue(); // Получение сгенерированного идентификатора
//        user.setId(id);
//        return user;

//        Long id = jdbcTemplate.queryForObject("SELECT NEXTVAL('user_id_seq')", Long.class);
//        user.setId(id);

        //String insertQuery = "INSERT INTO users (id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)";
//        jdbcTemplate.update(INSERT_QUERY, id, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
//        return user;

        jdbcTemplate.update(INSERT_QUERY,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        jdbcTemplate.update(UPDATE_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId());
        return newUser;
    }

    @Override
    public void deleteUsers() {
        jdbcTemplate.update(DELETE_ALL);
    }
}
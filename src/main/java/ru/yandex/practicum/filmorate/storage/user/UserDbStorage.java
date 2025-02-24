package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT u.id, u.email, u.login, u.name, u.birthday, " +
            "f.friend_id, f.status FROM users u " +
            "LEFT JOIN friends f ON u.id = f.user_id " +
            "LEFT JOIN users u1 ON u1.id = f.friend_id " +
            "WHERE u.id = ?";
    private static final String FIND_BY_IDS_QUERY = "SELECT u.id, u.email, u.login, u.name, u.birthday, " +
            "f.friend_id, f.status FROM users u " +
            "LEFT JOIN friends f ON u.id = f.user_id " +
            "LEFT JOIN users u1 ON u1.id = f.friend_id " +
            " WHERE u.id IN (%s)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_FRIENDS = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
    private static final String DELETE_FRIENDS = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, " +
            "name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_ALL = "DELETE FROM users";
    private static final String UPDATE_FRIEND_STATUS_QUERY = "UPDATE friend SET status = ? " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String CHECK_FRIENDSHIP_EXISTS_QUERY =
            "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query(FIND_ALL_QUERY, (resultSet, rowNum) -> {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setLogin(resultSet.getString("login"));
            user.setName(resultSet.getString("name"));
            user.setEmail(resultSet.getString("email"));
            Timestamp birthdayDate = resultSet.getTimestamp("birthday");
            user.setBirthday(birthdayDate.toLocalDateTime().toLocalDate());

            return user;
        });
    }

    @Override
    public User getUserById(Long id) {
        List<User> users = jdbcTemplate.query(FIND_BY_ID_QUERY, userRowMapper, id);

        if (users == null || users.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }

        return users.getFirst();
    }

    @Override
    public List<User> getUserByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
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
        Object[] params = ids.toArray();

        return jdbcTemplate.query(query, userRowMapper, params);
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_QUERY,
                    new String[]{"id"}
            );
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());
        } else {
            throw new RuntimeException("Ошибка при сохранении фильма: не удалось получить ID");
        }
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

    @Override
    public void addFriend(Long id, Long friendId, FriendStatus friendStatus) {
        jdbcTemplate.update(INSERT_FRIENDS, id, friendId, friendStatus.name());
        User user = getUserById(id);
        if (user != null) {
            user.getFriends().add(getUserById(friendId));
        }
    }

    public Friend getFriend(Long friendId, Long id) {

    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        jdbcTemplate.update(DELETE_FRIENDS, id, friendId);
    }

    @Override
    public void updateFriendStatus(Long id, Long friendId, FriendStatus newStatus) {
        jdbcTemplate.update(UPDATE_FRIEND_STATUS_QUERY, newStatus.name(), id, friendId);
    }

    @Override
    public boolean isFriendshipExists(Long id, Long friendId) {
        Integer count = jdbcTemplate.queryForObject(CHECK_FRIENDSHIP_EXISTS_QUERY, Integer.class, id, friendId);
        return count != null && count > 0;
    }
}
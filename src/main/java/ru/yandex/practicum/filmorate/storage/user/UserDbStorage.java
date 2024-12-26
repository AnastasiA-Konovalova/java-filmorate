package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseDbStorage<User> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_IDS_QUERY = "SELECT * FROM users WHERE id IN (%s)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users(id, email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, " +
            "name = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_ALL = "SELECT * FROM users";

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper, User.class);
    }

    public Optional<User> findById(Long userId) {
        return findById(FIND_BY_ID_QUERY, userId);
    }

    public List<User> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    public User saveUser(User user) {
        Long id = insert(
                INSERT_QUERY,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay())

        //Timestamp.from(Instant.from(user.getBirthday()))
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.from(Instant.from(user.getBirthday()))
        );
        return user;
    }

    public boolean delete(Long userId) {
        return delete(DELETE_ALL, userId);
    }

    public List<User> findByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
//        StringBuilder placeholders = new StringBuilder();
//        for (int i = 0; i < ids.size(); i++) {
//            placeholders.append("?");
//            if (i < ids.size() - 1) {
//                placeholders.append(", ");
//            }
//        }
//        String result = placeholders.toString();
//        String find_by_ids = FIND_BY_IDS_QUERY + result + ")";
//
//        return findAll(find_by_ids);
        //рабочий вар, но с лямбдой
        //String placeholders = String.join(", ", ids.stream().map(id -> "?").toArray(String[]::new));
        //String query = String.format(FIND_BY_IDS_QUERY, placeholders);


        StringBuilder placeholdersBuilder = new StringBuilder();
        int size = ids.size();
        int index = 0;
        for (Long id : ids) {
            placeholdersBuilder.append("?");
            if (index < size - 1) {
                placeholdersBuilder.append(", ");
            }
            index++;
        }
        String placeholders = placeholdersBuilder.toString();
        String query = String.format(FIND_BY_IDS_QUERY, placeholders);

        return jdbc.query(query, ids.toArray(), new UserRowMapper());
    }
}

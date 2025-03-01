package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRowMapper implements ResultSetExtractor<List<User>> {
    private static final String ID = "id";
    private static final String LOGIN = "login";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String BIRTHDAY = "birthday";
    private static final String FRIEND_ID_COLUMN_NAME = "friend_id";

    @Override
    public List<User> extractData(ResultSet rs) throws SQLException {
        Map<Long, User> usersMap = new LinkedHashMap<>();

        while (rs.next()) {
            long userId = rs.getLong(ID);
            User user = usersMap.get(userId);

            if (user == null) {
                user = new User();
                user.setId(userId);
                user.setLogin(rs.getString(LOGIN));
                user.setName(rs.getString(NAME));
                user.setEmail(rs.getString(EMAIL));
                Timestamp birthdayDate = rs.getTimestamp(BIRTHDAY);
                user.setBirthday(birthdayDate.toLocalDateTime().toLocalDate());
                user.setFriends(new HashSet<>());

                usersMap.put(userId, user);
            }

            long friendId = rs.getLong(FRIEND_ID_COLUMN_NAME);
            if (!rs.wasNull()) {
                Friend friend = new Friend();
                friend.setId(friendId);
                user.getFriends().add(friend);
            }
        }

        return new ArrayList<>(usersMap.values());
    }
}
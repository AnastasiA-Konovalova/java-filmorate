package ru.yandex.practicum.filmorate.service.mapping;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.stream.Collectors;

@Service
public class UserMapperToDto {

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setLogin(user.getLogin());
        userDto.setEmail(user.getEmail());
        userDto.setBirthday(user.getBirthday());
        userDto.setFriends(
                user.getFriends().stream().map(FriendMapperToDto::toDto).collect(Collectors.toSet())
        );
        return userDto;
    }
}
package ru.yandex.practicum.filmorate.service.mapping;

import ru.yandex.practicum.filmorate.dto.FriendDto;
import ru.yandex.practicum.filmorate.model.Friend;

public class FriendMapperToDto {

    public static FriendDto toDto(Friend friend) {
        FriendDto friendDto = new FriendDto();

        friendDto.setId(friend.getId());
        friendDto.setStatus(friend.getStatus().name());

        return friendDto;
    }
}
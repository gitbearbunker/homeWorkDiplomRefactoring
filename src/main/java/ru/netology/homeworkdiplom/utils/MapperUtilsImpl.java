package ru.netology.homeworkdiplom.utils;

import ru.netology.homeworkdiplom.dto.UserDto;
import ru.netology.homeworkdiplom.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class MapperUtilsImpl implements MapperUtils {

    @Override
    public UserEntity toUserEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(userDto.getLogin());
        userEntity.setPassword(userDto.getPassword());
        return userEntity;
    }

    @Override
    public UserDto toUserDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setLogin(userEntity.getLogin());
        userDto.setPassword(userEntity.getPassword());
        return userDto;
    }
}
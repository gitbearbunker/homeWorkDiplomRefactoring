package ru.netology.homeworkdiplom.utils;

import ru.netology.homeworkdiplom.dto.UserDto;
import ru.netology.homeworkdiplom.entity.UserEntity;

public interface MapperUtils {

    UserEntity toUserEntity(UserDto userDto);

    UserDto toUserDto(UserEntity userEntity);

}
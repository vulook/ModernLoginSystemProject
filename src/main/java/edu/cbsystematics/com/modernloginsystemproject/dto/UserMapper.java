package edu.cbsystematics.com.modernloginsystemproject.dto;

import edu.cbsystematics.com.modernloginsystemproject.model.User;

public class UserMapper {

    // Convert User JPA Entity into UserDTO
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword()
        );
    }


    // Convert UserDTO into User JPA Entity
    public static User mapToUser(UserDto userDto) {
        return new User(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword()
        );
    }

}
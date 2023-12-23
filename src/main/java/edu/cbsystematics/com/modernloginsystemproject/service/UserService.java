package edu.cbsystematics.com.modernloginsystemproject.service;

import edu.cbsystematics.com.modernloginsystemproject.dto.UserDto;
import edu.cbsystematics.com.modernloginsystemproject.dto.UserRegistrationDto;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    // Create a new user.
    void createUser(User user);

    // Create a new user based on the provided UserDto.
    UserDto createUserDto(UserDto userDto);

    // Update an existing user by their ID.
    @Transactional
    void updateUser(Long id, User updatedUser);

    // Delete a user by their ID.
    void deleteUser(Long id);

    // Get a list of users by their role.
    List<User> getUserListByRole(String role);

    // Returns the total number of users.
    long countUsers();

    // Save a new user based on the provided registration information.
    @Transactional
    void saveUserRegistration(UserRegistrationDto registration);

    // Update the password for a user identified by their ID.
    void updatePassword(Long id, String password);

    // Get a user by their ID.
    Optional<User> getUserById(Long id);

    // Get a list of all users.
    List<User> getAllUsers();

    // Find a user by their email address.
    User findByEmail(String email);

    // Get a paged list of all users with sorting options.
    // - sortColumn: Column to sort by.
    // - sortDirection: Sorting direction (ASC or DESC).
    // - pageable: Pagination information.
    Page<User> getAllUsersPaged(String sortColumn, String sortDirection, Pageable pageable);

}


package edu.cbsystematics.com.modernloginsystemproject.service;

import edu.cbsystematics.com.modernloginsystemproject.dto.UserDto;
import edu.cbsystematics.com.modernloginsystemproject.dto.UserMapper;
import edu.cbsystematics.com.modernloginsystemproject.dto.UserRegistrationDto;
import edu.cbsystematics.com.modernloginsystemproject.exception.EmailAlreadyExistsException;
import edu.cbsystematics.com.modernloginsystemproject.model.Role;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.repository.RoleRepository;
import edu.cbsystematics.com.modernloginsystemproject.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(User user) {
        // Check if a user with the specified email already exists
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        if (optionalUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists, try a new one");
        }

        // Encode the user's password before saving it to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDto createUserDto(UserDto userDto) {
        // Convert UserDto to User JPA Entity
        User user = UserMapper.mapToUser(userDto);

        // Check if a user with the specified email already exists
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(user.getEmail()));
        if(optionalUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists, try a new one");
        }

        // Encode the user's password before saving it to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        User savedUser = userRepository.save(user);

        // Convert the User JPA Entity to UserDto
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        return optionalUser;
    }

    @Override
    @Transactional
    public void updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            // Existing user found in the repository by ID.
            userRepository.updateUser(
                    id,
                    updatedUser.getFirstName(),
                    updatedUser.getLastName(),
                    updatedUser.getEmail(),
                    updatedUser.getPassword()
            );
        } else {
            // User with the specified ID isn't found.
            throw new EntityNotFoundException("User with ID " + id + " not found.");
        }
        logger.info("Profile updated for user with email: {} and added role: {}", existingUserOptional.get().getEmail(), existingUserOptional.get().getRoles());
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isPresent()) {
            // Existing user found in the repository by ID.
            userRepository.deleteById(id);
        } else {
            // User with the specified ID isn't found.
            throw new EntityNotFoundException("User with ID " + id + " not found.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        if (allUsers.isEmpty()) {
            // No users found.
            throw new EntityNotFoundException("No users found.");
        }

        return allUsers;
    }

    @Override
    public List<User> getUserListByRole(String role) {
        List<User> userListByRole = userRepository.findByRole(role);
        if (userListByRole.isEmpty()) {
            // No users found.
            throw new EntityNotFoundException("No users found.");
        }

        return userListByRole;
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }


    @Override
    @Transactional
    public void saveUserRegistration(UserRegistrationDto registration) {
        User user = new User();
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));

        // Check if the role "ROLE_STUDENT" already exists
        Optional<Role> roleStudent = roleRepository.findByRoleName("ROLE_STUDENT");
        if (roleStudent.isEmpty()) {
            // Role doesn't exist, create and save it
            user.setRoles(List.of(new Role("ROLE_STUDENT")));
        } else {
            // Role exists, use it
            user.setRoles(new HashSet<>(List.of(roleStudent.get())));
        }

        userRepository.save(user);
        logger.info("Profile created for user with email: {} and added role: {}", user.getEmail(), user.getRoles());
    }

    @Override
    public void updatePassword(Long id, String password) {
        userRepository.updatePassword(id, password);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("Error login");
            throw new UsernameNotFoundException("Invalid email or password.");
        }

        logger.info("Username: {}", user.getEmail());
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Page<User> getAllUsersPaged(String sortColumn, String sortDirection, Pageable pageable) {
        Sort.Direction direction = "asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sorted = switch (sortColumn) {
            case "fullName" -> Sort.by(direction, "firstName", "lastName");
            case "email" -> Sort.by(direction, "email");
            case "roles" -> Sort.by(direction, "roles.roleName");
            default -> Sort.by(direction, "id");
        };

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorted);
        return userRepository.findAll(sortedPageable);
    }


}

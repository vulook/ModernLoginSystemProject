package edu.cbsystematics.com.modernloginsystemproject.service;

import edu.cbsystematics.com.modernloginsystemproject.dto.UserDto;
import edu.cbsystematics.com.modernloginsystemproject.dto.UserMapper;
import edu.cbsystematics.com.modernloginsystemproject.model.Role;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.repository.RoleRepository;
import edu.cbsystematics.com.modernloginsystemproject.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void updateRole(Long id, Role updatedRole) {
        Optional<Role> existingRole = roleRepository.findById(id);

        if (existingRole.isPresent()) {
            // Existing user found in the repository by ID.
            roleRepository.updateRole(
                    id,
                    updatedRole.getRoleName()
            );
        } else {
            // User with the specified ID isn't found.
            throw new EntityNotFoundException("Role with ID " + id + " not found.");
        }
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long roleId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        // Check if the role already exists
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
            logger.info("Profile created for user with email: {} and added role: {}", user.getEmail(), user.getRoles());
        }
    }

    @Override
    @Transactional
    public UserDto updateUserDto(Long userId, Long roleId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Check if there are changes in the fields
        if (!Objects.equals(user.getFirstName(), userDto.getFirstName())) {
            user.setFirstName(userDto.getFirstName());
        }

        if (!Objects.equals(user.getLastName(), userDto.getLastName())) {
            user.setLastName(userDto.getLastName());
        }

        if (!Objects.equals(user.getEmail().trim(), userDto.getEmail().trim())) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                // If the password has changed, encrypt it
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
        }

        // Getting a role to update
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        // Check if the role has not already been added to the user
        if (!user.getRoles().contains(role)) {
            user.getRoles().clear();
            user.getRoles().add(role);
        }

        User updatedUser = userRepository.save(user);
        logger.info("Profile updated for user with email: {} and added role: {}", updatedUser.getEmail(), updatedUser.getRoles());
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
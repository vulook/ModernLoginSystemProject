package edu.cbsystematics.com.modernloginsystemproject.service;

import edu.cbsystematics.com.modernloginsystemproject.dto.UserDto;
import edu.cbsystematics.com.modernloginsystemproject.model.Role;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    // Create a new role.
    void createRole(Role role);

    // Update a role by its ID.
    @Transactional
    void updateRole(Long id, Role updatedRole);

    // Update a UserDto.
    // - userId: ID of the user to be updated.
    // - roleId: ID of the role associated with the user.
    // - userDto: Updated UserDto information.
    // Returns the updated UserDto.
    @Transactional
    UserDto updateUserDto(Long userId, Long roleId, UserDto userDto);

    // Delete a role by its ID.
    void deleteRole(Long id);

    // Assign Role to User.
    // - roleId: ID of the role to be assigned.
    // - userId: ID of the user to whom the role will be assigned.
    @Transactional
    void assignRoleToUser(Long roleId, Long userId);

    // Get a role by its ID.
    // Returns an Optional containing the retrieved role, or empty if not found.
    Optional<Role> getRoleById(Long id);

    // Get a list of all roles.
    List<Role> getAllRoles();

}
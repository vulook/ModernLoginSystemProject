package edu.cbsystematics.com.modernloginsystemproject.controller;

import edu.cbsystematics.com.modernloginsystemproject.dto.UserDto;
import edu.cbsystematics.com.modernloginsystemproject.dto.UserMapper;
import edu.cbsystematics.com.modernloginsystemproject.model.Role;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.service.RoleService;
import edu.cbsystematics.com.modernloginsystemproject.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Controller
@Secured("ADMIN")
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;

    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "id") String sortColumn,
                            @RequestParam(defaultValue = "asc") String sortDirection,
                            @RequestParam(defaultValue = "0") int page) {

        final int PAGE_SIZE = 10; // Page size

        // Getting a list of users
        Page<User> userPage = userService.getAllUsersPaged(sortColumn, sortDirection, PageRequest.of(page, PAGE_SIZE));

        logger.info("Sort Column: {}", sortColumn);
        logger.info("Sort Direction: {}", sortDirection);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("sortColumn", sortColumn);
        model.addAttribute("sortDirection", sortDirection);

        return "admin/users-list";
    }

    // Show the page to create a new user
    @GetMapping("/list/create")
    public String showCreateUserForm(Model model) {
        // Retrieve a list of all roles
        List<Role> roles = roleService.getAllRoles();

        model.addAttribute("roles", roles);
        model.addAttribute("user", new UserDto());
        return "admin/create-user";
    }

    @PostMapping("/list/create")
    public String createUser(@ModelAttribute("user") @Valid UserDto userDto, BindingResult result,
                             @RequestParam(name = "selectedRole", required = false) Long selectedRoleId,
                             HttpServletRequest request, Model model) {

        // Obtain the CSRF token from the HttpServletRequest attributes
        getCsrfToken(request, model);

        // Check for validation errors
        User existing = userService.findByEmail(userDto.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (selectedRoleId == null) {
            return "redirect:/admin/list/create?role_error";
        }

        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result);
            List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("error", " ");
            model.addAttribute("globalError", "Error creating user!");

            return "admin/create-user";

        } else {
            // Assign the selected role to the created user
            UserDto createdUser = userService.createUserDto(userDto);
            roleService.assignRoleToUser(selectedRoleId, createdUser.getId());

            return "redirect:/admin/list?create_success";
        }

    }

    // Show the page to edit a user
    @GetMapping("/list/{userId}/edit")
    public String showEditUserForm(@PathVariable Long userId, Model model) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Convert User to UserDto
        UserDto userDto = UserMapper.mapToUserDto(user);
        List<Role> roles = roleService.getAllRoles();
        Collection<Role> userRoles = user.getRoles();

        model.addAttribute("roles", roles);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("user", userDto);
        return "admin/edit-user";
    }

    // Handle the update of a user
    @PostMapping("/list/{userId}/edit")
    public String updateUser(@PathVariable Long userId,
                             @ModelAttribute("user") @Valid UserDto updatedUser,
                             BindingResult result, Model model, HttpServletRequest request,
                             @RequestParam(name = "selectedRole", required = false) Long selectedRoleId) {

        // Obtain the CSRF token from the HttpServletRequest attributes
        getCsrfToken(request, model);

        // Check for validation errors
        User existing = userService.findByEmail(updatedUser.getEmail());
        if (existing != null && !Objects.equals(existing.getId(), userId)) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            updatedUser.setId(userId);
            logger.error("Validation errors: {}", result);
            List<Role> roles = roleService.getAllRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("error", " ");
            model.addAttribute("globalError", "Error updating user!");
            return "admin/edit-user";

        } else {

            roleService.updateUserDto(userId, selectedRoleId, updatedUser);
            return "redirect:/admin/list?update_success";
        }

    }

    // Delete a user
    @GetMapping("/list/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must be specified when deleting a user");
        }

        userService.deleteUser(userId);
        return "redirect:/admin/list?delete_success";
    }


    // Method to obtain the CSRF token from the HttpServletRequest attributes
    private void getCsrfToken(HttpServletRequest request, Model model) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token == null) {
            logger.error("CSRF Token not found");
            model.addAttribute("error", "CSRF Token not found");
            return;
        }

        String csrfParameterName = token.getParameterName();
        String csrfTokenValue = token.getToken();

        logger.info("CSRF Parameter Name: {}", csrfParameterName);
        logger.info("CSRF Token Value: {}", csrfTokenValue);

    }

}

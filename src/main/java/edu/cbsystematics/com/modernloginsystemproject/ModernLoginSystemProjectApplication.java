package edu.cbsystematics.com.modernloginsystemproject;

import edu.cbsystematics.com.modernloginsystemproject.model.Role;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.service.RoleService;
import edu.cbsystematics.com.modernloginsystemproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FileName: ModernLoginSystemProjectApplication
 * Author:   Andriy Vulook
 * Date:     01.12.2023 01:41
 * Description: ModernLoginSystemProjectApplication
 */

@SpringBootApplication
public class ModernLoginSystemProjectApplication {

    private final UserService userService;

    private final RoleService roleService;

    @Autowired
    public ModernLoginSystemProjectApplication(edu.cbsystematics.com.modernloginsystemproject.service.UserService userService, edu.cbsystematics.com.modernloginsystemproject.service.RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Creating default users for display on the login page
    public static final List<List<String>> defaultUsersList = new ArrayList<>();


    public static void main(String[] args) {
        SpringApplication.run(ModernLoginSystemProjectApplication.class, args);
    }


    @PostConstruct
    public void createAndSaveRolesAndUsers() {

        // Creating and Saving the roles in the database
        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleModerator = new Role("ROLE_MODERATOR");
        Role roleStudent = new Role("ROLE_STUDENT");

        roleService.createRole(roleAdmin);
        roleService.createRole(roleModerator);
        roleService.createRole(roleStudent);

        // Creating and Saving users with roles
        User admin = new User("Admin", "Admin", "admin@example.com", "123");
        User moderator = new User("Moderator", "Moderator", "moderator@example.com", "123");
        User student = new User("Student", "Student", "student@example.com", "123");

        // Creating default users for display on the login page
        List<String> adminData = Arrays.asList(admin.getFirstName(), admin.getEmail(), admin.getPassword());
        List<String> moderatorData = Arrays.asList(moderator.getFirstName(), moderator.getEmail(), moderator.getPassword());
        List<String> studentData = Arrays.asList(student.getFirstName(), student.getEmail(), student.getPassword());

        defaultUsersList.add(adminData);
        defaultUsersList.add(moderatorData);
        defaultUsersList.add(studentData);

        userService.createUser(admin);
        userService.createUser(moderator);
        userService.createUser(student);

        roleService.assignRoleToUser(roleAdmin.getId(), admin.getId());
        roleService.assignRoleToUser(roleModerator.getId(), moderator.getId());
        roleService.assignRoleToUser(roleStudent.getId(), student.getId());

    }

}







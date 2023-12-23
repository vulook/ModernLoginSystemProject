package edu.cbsystematics.com.modernloginsystemproject.controller;

import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;

    @Autowired
    public StudentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String studentList(Model model) {
        String role = "ROLE_STUDENT";
        List<User> students = userService.getUserListByRole(role);
        model.addAttribute("students", students);

        return "student/student-list";
    }

}
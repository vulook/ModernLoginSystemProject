package edu.cbsystematics.com.modernloginsystemproject.controller;

import edu.cbsystematics.com.modernloginsystemproject.model.Role;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


@Controller
public class AccessDeniedController {

    private final UserService userService;

    @Autowired
    public AccessDeniedController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model, HttpSession session) {

        // Retrieve the URI from the session attribute
        String uri = (String) session.getAttribute("accessDeniedUri");

        // Remove the session attribute to avoid using it again
        session.removeAttribute("accessDeniedUri");

        // Get the current authentication information
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            User currentUser = userService.findByEmail(auth.getName());

            if (currentUser != null) {
                model.addAttribute("date", getCurrentDateAndTime());
                model.addAttribute("username", currentUser.getFirstName() + " " + currentUser.getLastName());
                model.addAttribute("role", getRoles(currentUser));
                model.addAttribute("uri", uri);
                model.addAttribute("message", "AccessDeniedException");

            } else {
                model.addAttribute("date", getCurrentDateAndTime());
                model.addAttribute("username", "Anonymous");
                model.addAttribute("uri", uri);
                model.addAttribute("message", "AccessDeniedException");
            }
        }

        return "error/access-denied";
    }

    // Gets the current date and time formatted as "dd.MM.yyyy HH:mm:ss".
    private static String getCurrentDateAndTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return now.format(formatter);
    }

    // Gets the roles of the given user as a concatenated string
    private String getRoles(User currentUser) {
        return currentUser.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.joining(" "));
    }

}
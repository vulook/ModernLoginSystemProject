package edu.cbsystematics.com.modernloginsystemproject.controller;

import edu.cbsystematics.com.modernloginsystemproject.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static edu.cbsystematics.com.modernloginsystemproject.ModernLoginSystemProjectApplication.defaultUsersList;


@Controller
@RequestMapping("/")
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String showPublicPage(Model model) {
        long userCount = userService.countUsers();
        model.addAttribute("userCount", userCount);
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        List<List<String>> defaultUsers = defaultUsersList;
        model.addAttribute("defaultUsers", defaultUsers);
        return "logging/login";
    }

    // Retrieving the home page for authenticated users with roles USER, ADMIN, MODERATOR
    @GetMapping("/authorized")
    @Secured({"ROLE_USER","ROLE_MODERATOR","ROLE_ADMIN"})
    public String getAuthorizedRoles(Model model, Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        logger.info("Action role: {}", role);
        model.addAttribute("role", role);
        return "logging/authorized";
    }

}

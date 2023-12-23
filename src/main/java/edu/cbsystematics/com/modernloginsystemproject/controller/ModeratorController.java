package edu.cbsystematics.com.modernloginsystemproject.controller;

import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/moderator")
public class ModeratorController {

    private final UserService userService;

    @Autowired
    public ModeratorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public String moderatorList(Model model) {
        String role = "ROLE_MODERATOR";
        List<User> moderators = userService.getUserListByRole(role);
        model.addAttribute("moderators", moderators);

        return "moderator/moderator-list";
    }

}
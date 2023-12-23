package edu.cbsystematics.com.modernloginsystemproject.controller;

import edu.cbsystematics.com.modernloginsystemproject.dto.UserRegistrationDto;
import edu.cbsystematics.com.modernloginsystemproject.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class);

    private final UserService userService;

    @Autowired
    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "logging/registration";
    }

    @PostMapping
    public String registerNewUser(@ModelAttribute("user") @Valid UserRegistrationDto userRegistrationDto,
                                  BindingResult result) {

        // Check if the user with the given email already exists
        if (userAlreadyExists(userRegistrationDto.getEmail())) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        // If there are validation errors, return to the registration page
        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result);
            return "logging/registration";
        }

        // Save the user registration details if everything is valid
        userService.saveUserRegistration(userRegistrationDto);
        return "redirect:/registration?success";
    }

    private boolean userAlreadyExists(String email) {
        return userService.findByEmail(email) != null;
    }

}
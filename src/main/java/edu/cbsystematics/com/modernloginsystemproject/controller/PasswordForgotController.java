package edu.cbsystematics.com.modernloginsystemproject.controller;

import edu.cbsystematics.com.modernloginsystemproject.dto.PasswordForgotDto;
import edu.cbsystematics.com.modernloginsystemproject.model.Mail;
import edu.cbsystematics.com.modernloginsystemproject.model.PasswordResetToken;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.repository.PasswordResetTokenRepository;
import edu.cbsystematics.com.modernloginsystemproject.service.EmailService;
import edu.cbsystematics.com.modernloginsystemproject.service.PasswordResetTokenService;
import edu.cbsystematics.com.modernloginsystemproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/forgot-password")
public class PasswordForgotController {

    private final UserService userService;
    private final PasswordResetTokenService tokenService;
    private final EmailService emailService;

    @Autowired
    public PasswordForgotController(UserService userService, PasswordResetTokenService tokenService, EmailService emailService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    // Create a new instance of PasswordForgotDto
    @ModelAttribute("forgotPasswordForm")
    public PasswordForgotDto forgotPasswordDto() {
        return new PasswordForgotDto();
    }

    @GetMapping
    public String displayForgotPasswordPage() {
        return "logging/forgot-password";
    }

    // Handles POST requests to "/forgot-password"
    @PostMapping
    public String processForgotPasswordForm(@ModelAttribute("forgotPasswordForm") @Valid PasswordForgotDto form,
                                            BindingResult result, HttpServletRequest request) {

        // Find the user by email
        User user = userService.findByEmail(form.getEmail());
        if (user == null) {
            result.rejectValue("email", null, "We don't have an account associated with that email address.");
        }

        if (result.hasErrors()) {
            return "logging/forgot-password";
        }

        // Create a new password reset token
        PasswordResetToken token = tokenService.createPasswordResetToken(user);

        if (token == null) {
            // Handle the case when the token is not created
            result.rejectValue("email", null, "Error creating password reset token.");
        }

        // Build an email with the password reset instructions
        Mail mail = tokenService.buildPasswordResetEmail(user, token, request);

        // Send the email containing the password reset link
        emailService.sendEmail(mail);

        // Redirect to the forgot password page with a success message
        return "redirect:/forgot-password?success";
    }

}
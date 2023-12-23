package edu.cbsystematics.com.modernloginsystemproject.service;

import edu.cbsystematics.com.modernloginsystemproject.model.Mail;
import edu.cbsystematics.com.modernloginsystemproject.model.PasswordResetToken;
import edu.cbsystematics.com.modernloginsystemproject.model.User;
import edu.cbsystematics.com.modernloginsystemproject.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    @Autowired
    public PasswordResetTokenService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Creates a new password reset token for the specified user
    public PasswordResetToken createPasswordResetToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(180); // time while the token is valid (minutes)
        return tokenRepository.save(token);
    }

    // Builds and returns an email with instructions for password reset
    public Mail buildPasswordResetEmail(User user, PasswordResetToken token, HttpServletRequest request) {
        Mail mail = new Mail();
        mail.setFrom("no-reply@modern-login-system-project.com");
        mail.setTo(user.getEmail());
        mail.setSubject("Password reset request");

        // Populate the email template with necessary data
        Map<String, Object> modelAttributes = new HashMap<>();
        // Add the token object to the model
        modelAttributes.put("token", token);
        // Add the user object to the model
        modelAttributes.put("user", user);
        // Add a signature to the model
        modelAttributes.put("signature", "Modern Login System Project by @Vulook. ");
        // Build the reset URL and add it to the model
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        modelAttributes.put("resetUrl", url + "/reset-password?token=" + token.getToken());
        // Adding expiry date to the model
        modelAttributes.put("resetTokenExpiry", token.getExpiryDate());
        // Set the model for the email
        mail.setModel(modelAttributes);

        return mail;
    }

}
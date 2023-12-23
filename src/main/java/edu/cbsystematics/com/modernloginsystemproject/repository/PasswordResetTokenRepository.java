package edu.cbsystematics.com.modernloginsystemproject.repository;

import edu.cbsystematics.com.modernloginsystemproject.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Get token
    PasswordResetToken findByToken(String token);

}

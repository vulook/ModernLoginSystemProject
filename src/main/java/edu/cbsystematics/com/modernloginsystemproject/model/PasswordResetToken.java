package edu.cbsystematics.com.modernloginsystemproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Entity
@Data
@DynamicUpdate
@NoArgsConstructor
@Table(name = "password_reset")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, columnDefinition = "varchar(100)")
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDateTime;

    // Sets the expiry date for the password reset token.
    public void setExpiryDate(int minutes) {
        this.expiryDateTime = LocalDateTime.now().plusMinutes(minutes);
    }

    // Returns a formatted representation of the expiry date.
    public String getExpiryDate() {
        // Creates a DateTimeFormatter object with the specified pattern.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        return formatter.format(expiryDateTime);
    }

    // Checks if the password reset token has expired.
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDateTime);
    }

}



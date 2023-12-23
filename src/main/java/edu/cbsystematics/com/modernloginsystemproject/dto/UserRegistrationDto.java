package edu.cbsystematics.com.modernloginsystemproject.dto;

import edu.cbsystematics.com.modernloginsystemproject.constraint.FieldMatch;
import edu.cbsystematics.com.modernloginsystemproject.constraint.ValidEmail;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.*;


@Data
@FieldMatch.List({
        @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"),
        @FieldMatch(first = "email", second = "confirmEmail", message = "The email fields must match")
})
public class UserRegistrationDto {

    @NotNull(message = "First Name is required")
    @Size(min = 3, max = 50, message = "First Name should be between 3 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = "^[\\p{Alpha} ]*$", message = "First Name should contain only alphabets and space"),
            @Pattern(regexp = "^[^\\s].*$", message = "First Name should not start with space"),
            @Pattern(regexp = "^.*[^\\s]$", message = "First Name should not end with space"),
            @Pattern(regexp = "^((?!  ).)*$", message = "First Name should not contain consecutive spaces"),
            @Pattern(regexp = "^[^a-z].*$", message = "First Name should not start with a lowercase character")
    })
    @Column(name = "first_name", nullable = false, columnDefinition = "varchar(50)")
    private String firstName;

    @NotNull(message = "Last Name is required")
    @Size(min = 3, max = 50, message = "Last Name should be between 3 and 50 characters")
    @Pattern.List({
            @Pattern(regexp = "^[\\p{Alpha} ]*$", message = "Last Name should contain only alphabets and space"),
            @Pattern(regexp = "^[^\\s].*$", message = "Last Name should not start with space"),
            @Pattern(regexp = "^.*[^\\s]$", message = "Last Name should not end with space"),
            @Pattern(regexp = "^((?!  ).)*$", message = "Last Name should not contain consecutive spaces"),
            @Pattern(regexp = "^[^a-z].*$", message = "Last Name should not start with a lowercase character")
    })
    private String lastName;

    @NotNull(message = "Email is required")
    @Email(message = "Email address should be valid")
    @ValidEmail
    private String email;
    private String confirmEmail;

    @NotNull(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters")
    @Pattern.List({
            @Pattern(regexp = ".*[0-9].*", message = "Password should contain at least one digit"),
            @Pattern(regexp = ".*[a-z].*", message = "Password should contain at least one lowercase letter"),
            @Pattern(regexp = ".*[A-Z].*", message = "Password should contain at least one uppercase letter"),
            @Pattern(regexp = ".*[!@#&()\\[\\]{}:;',?/*~$^+=<>].*", message = "Password should contain at least one special character"),
            @Pattern(regexp = "^[\\S]+$", message = "Password must not contain spaces")
    })
    private String password;
    private String confirmPassword;

    @AssertTrue
    private Boolean checkbox;

}

package edu.cbsystematics.com.modernloginsystemproject.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Data
@NotNull(message = "Email is required")
public class PasswordForgotDto {

    @Email
    private String email;

}

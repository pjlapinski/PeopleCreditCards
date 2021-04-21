package com.github.pjlapinski.people.models;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class UserDTO {
    private long id;
    @NotNull(message = "Field required")
    @NotEmpty(message = "Please enter the username")
    @Pattern(regexp = "[\\w\\d]+", message = "Username can only consist of letters and numbers")
    @Length(min = 2, max = 32, message = "Username must be between 2 and 32 characters")
    private String username;

    @NotNull(message = "Field required")
    @NotEmpty(message = "Please enter the email")
    @Pattern(regexp = "[^\\s@]+@[^\\s@]+\\.[^\\s@]+", message = "Must be a valid email")
    private String email;

    @NotNull(message = "Field required")
    @NotEmpty(message = "Please enter the password")
    @Pattern(regexp = "[\\w\\d]+", message = "Password can only consist of letters and numbers")
    @Length(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    private String password;
    private String matchingPassword;

}

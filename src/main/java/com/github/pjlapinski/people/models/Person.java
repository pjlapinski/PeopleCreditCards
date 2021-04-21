package com.github.pjlapinski.people.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@NoArgsConstructor
@Data
public class Person {
    @Id
    @GeneratedValue
    private long id;

    @NotNull(message = "Field required")
    @NotEmpty(message = "Please enter first name")
    private String firstName;

    @NotNull(message = "Field required")
    @NotEmpty(message = "Please enter last name")
    private String lastName;

    @NotNull(message = "Field required")
    @Pattern(regexp = "[^\\s@]+@[^\\s@]+\\.[^\\s@]+", message = "Must be a valid email")
    @NotEmpty(message = "Please enter email")
    private String email;

    @NotNull(message = "Field required")
    @Pattern(regexp = "Male|Female")
    private String gender;

    @NotNull(message = "Field required")
    @NotEmpty(message = "Please enter credit card type")
    private String creditCardType;

    @NotNull(message = "Field required")
    @CreditCardNumber(message = "Must be a valid credit card number")
    private String creditCardNumber;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    public Person(String firstName, String lastName, String email,
                  String gender, String creditCardType, String creditCardNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.creditCardType = creditCardType;
        this.creditCardNumber = creditCardNumber;
    }

    public String getValueFromPropertyName(String name) {
        return switch (name) {
            case "firstName" -> getFirstName();
            case "lastName" -> getLastName();
            case "email" -> getEmail();
            case "gender" -> getGender();
            case "creditCardType" -> getCreditCardType();
            case "creditCardNumber" -> getCreditCardNumber();
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }
}

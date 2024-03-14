package org.example.validation;

import org.example.domain.Entity;
import org.example.domain.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator implements Validator<User>{
    public UserValidator(){};

    @Override
    public void validate(User entity) throws ValidationException {
        String message = new String();

        // Check if first name or last name is null or empty
        if (entity.getFirstName() == null || entity.getFirstName().isEmpty() ||
                entity.getLastName() == null || entity.getLastName().isEmpty()) {
            message += "Null or empty name!\n";
        }

        // Check if ID is negative or zero
        if (entity.getId() < 1) {
            message += "Id is negative or zero!\n";
        }

        // Check if password contains symbols and numbers
        if (!containsSymbol(entity.getPassword()) || !containsNumber(entity.getPassword())) {
            message += "Password must contain at least one symbol and one number!\n";
        }

        // Check if email has a valid format
        if (!isValidEmail(entity.getEmail())) {
            message += "Invalid email format!\n";
        }

        // If any validation errors, throw exception
        if (!message.isEmpty()) {
            throw new ValidationException(message);
        }
    }

    // Helper method to check if a string contains at least one symbol
    private boolean containsSymbol(String input) {
        Pattern pattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    // Helper method to check if a string contains at least one number
    private boolean containsNumber(String input) {
        return input.matches(".*\\d.*");
    }

    // Helper method to check if the email has a valid format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

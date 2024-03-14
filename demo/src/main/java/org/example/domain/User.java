package org.example.domain;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String fullName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User(String firstName, String lastName, String username, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.fullName = firstName+" "+lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User U)) return false;
        return getFirstName().equals(U.getFirstName()) &&
                getLastName().equals(U.getLastName());
    }

    @Override
    public String toString() {
        return "User{ID: "+ this.getId() + "FirstName='" + firstName + '\'' + ", LastName='" + lastName + '\'' + '}';
    }
}


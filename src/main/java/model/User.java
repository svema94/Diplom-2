package model;

import static constant.RandomData.*;

public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User allField() {
        return new User(System.currentTimeMillis() + EMAIL, PASSWORD + System.currentTimeMillis(), NAME + System.currentTimeMillis());
    }

    public static User withoutEmail() {
        return new User(EMPTY, PASSWORD, NAME);
    }

    public static User withoutPassword() {
        return new User(EMAIL, EMPTY, NAME);
    }

    public static User withoutName() {
        return new User(EMAIL, PASSWORD, EMPTY);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

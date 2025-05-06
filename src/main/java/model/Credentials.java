package model;

public class Credentials {
    private String email;
    private String password;
    private String name;

    public Credentials(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static Credentials fromUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null.");
        }
        return new Credentials(user.getEmail(), user.getPassword(), user.getName());
    }

    public static Credentials withOtherEmail(User user, String newEmail) {
        Credentials credentials = fromUser(user);
        return new Credentials(newEmail, credentials.getPassword(), credentials.getName());
    }

    public static Credentials withOtherPassword(User user, String newPassword) {
        Credentials credentials = fromUser(user);
        return new Credentials(credentials.getEmail(), newPassword, credentials.getName());
    }

    public static Credentials withOtherName(User user, String newName) {
        Credentials credentials = fromUser(user);
        return new Credentials(credentials.getEmail(), credentials.getPassword(), newName);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package model;

public abstract class User {

    private String userId;
    private String name;
    private String username;
    private String password;
    private String role;

    public User(String userId, String name, String username,
                String password, String role) {

        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void displayUserDetails() {
        System.out.println("User ID : " + userId);
        System.out.println("Name    : " + name);
        System.out.println("Username: " + username);
        System.out.println("Role    : " + role);
    }
}
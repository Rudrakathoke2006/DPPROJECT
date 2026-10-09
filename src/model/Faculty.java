package model;

public class Faculty extends User {

    public Faculty(String userId, String name,
                   String username, String password) {

        super(userId, name, username, password, "FACULTY");
    }
}
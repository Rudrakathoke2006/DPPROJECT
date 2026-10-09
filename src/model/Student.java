package model;

public class Student extends User {

    public Student(String userId, String name,
                   String username, String password) {

        super(userId, name, username, password, "STUDENT");
    }
}
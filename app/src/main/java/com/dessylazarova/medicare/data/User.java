package com.dessylazarova.medicare.data;

public class User {
    private String id;
    private String email;

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }


    public String getId() {
        return id;
    }

}

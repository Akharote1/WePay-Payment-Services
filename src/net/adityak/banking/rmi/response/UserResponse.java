package net.adityak.banking.rmi.response;

import net.adityak.banking.models.User;

import java.io.Serializable;

public class UserResponse implements Serializable {
    public int status;
    public User user;

    public UserResponse(int status, User user) {
        this.status = status;
        this.user = user;
    }
}

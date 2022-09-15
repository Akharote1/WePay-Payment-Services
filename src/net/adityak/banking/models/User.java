package net.adityak.banking.models;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User implements Serializable {
    private String name;
    private String id;
    private String phoneNumber;
    private int balance;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", balance=" + balance +
                '}';
    }

    public static User fromResultSet(ResultSet result) {
        User user = new User();

        try {
            user.id = result.getString("user_id");
            user.name = result.getString("name");
            user.phoneNumber = result.getString("phone_number");
            user.balance = result.getInt("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}

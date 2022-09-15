package net.adityak.banking.rmi.response;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    public int status;
    public String message;
    public String sessionToken;

    public AuthResponse(int status, String sessionToken, String message) {
        this.status = status;
        this.sessionToken = sessionToken;
        this.message = message;
    }

    public AuthResponse(int status, String sessionToken) {
        this(status, sessionToken, null);
    }
}

package net.adityak.banking.rmi.response;

import java.io.Serializable;

public class PaymentResponse implements Serializable {
    public int status;
    public String message;

    public PaymentResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public PaymentResponse(int status) {
        this(status, "");
    }
}

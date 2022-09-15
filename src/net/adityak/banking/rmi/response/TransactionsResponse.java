package net.adityak.banking.rmi.response;

import net.adityak.banking.models.Transaction;

import java.io.Serializable;
import java.util.ArrayList;

public class TransactionsResponse implements Serializable {
    public int status;
    public String message;
    public ArrayList<Transaction> transactions;

    public TransactionsResponse(int status, ArrayList<Transaction> transactions, String message) {
        this.status = status;
        this.message = message;
        this.transactions = transactions;
    }
}

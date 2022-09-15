package net.adityak.banking.models;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Transaction implements Serializable {
    int amount;
    String senderId, senderName;
    String receiverId, receiverName;
    String transactionId, status;
    Timestamp timestamp;

    public static Transaction fromResultSet(ResultSet res) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.amount = res.getInt("amount");
        transaction.transactionId = res.getString("transaction_id");
        transaction.senderId = res.getString("sender_id");
//        transaction.senderName = res.getString("sender_name");
        transaction.receiverId = res.getString("receiver_id");
//        transaction.receiverName = res.getString("receiver_name");
        transaction.timestamp = res.getTimestamp("timestamp");
        transaction.status = res.getString("status");
        return transaction;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

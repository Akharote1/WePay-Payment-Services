package net.adityak.banking.rmi;

import net.adityak.banking.models.Transaction;
import net.adityak.banking.models.User;
import net.adityak.banking.rmi.response.AuthResponse;
import net.adityak.banking.rmi.response.PaymentResponse;
import net.adityak.banking.rmi.response.TransactionsResponse;
import net.adityak.banking.rmi.response.UserResponse;
import net.adityak.banking.utils.Database;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static net.adityak.banking.utils.Utils.formatPhoneNumber;

public class PaymentImpl extends UnicastRemoteObject implements PaymentInterface {
    public PaymentImpl() throws RemoteException {
        super();
    }

    @Override
    public String getUserName(String phoneNumber) throws RemoteException {
        try {
            PreparedStatement statement = Database.get()
                    .prepareStatement("SELECT name, phone_number FROM User WHERE phone_number = ?");
            statement.setString(1, formatPhoneNumber(phoneNumber));
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public AuthResponse login(String phoneNumber, String passcode) throws RemoteException {
        try {
            PreparedStatement statement = Database.get()
                    .prepareStatement("""
                            SELECT user_id, passcode_hashed FROM User WHERE phone_number = ?""");
            statement.setString(1, formatPhoneNumber(phoneNumber));

            ResultSet result = statement.executeQuery();

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (result.next()) {
                if (encoder.matches(passcode, result.getString("passcode_hashed"))) {
                    String sessionToken = UUID.randomUUID().toString();

                    statement = Database.get()
                            .prepareStatement("""
                            UPDATE User SET session_token = ? WHERE phone_number = ?""");

                    statement.setString(1, sessionToken);
                    statement.setString(2, phoneNumber);
                    statement.execute();

                    return new AuthResponse(Responses.SUCCESS, sessionToken);
                }

                return new AuthResponse(Responses.ERROR, null, "Incorrect passcode");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new AuthResponse(Responses.ERROR, null, "An error occurred");
    }

    @Override
    public AuthResponse register(String name, String phoneNumber, String passcode, String email)
            throws RemoteException {
        try {
            PreparedStatement statement = Database.get()
                    .prepareStatement("""
                            INSERT INTO User (name, phone_number, user_id,
                                session_token, passcode_hashed, email)
                            VALUES (?, ?, ?, ?, ?, ?)""");

            String sessionToken = UUID.randomUUID().toString();

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            statement.setString(1, name);
            statement.setString(2, formatPhoneNumber(phoneNumber));
            statement.setString(3, UUID.randomUUID().toString());
            statement.setString(4, sessionToken);
            statement.setString(5, encoder.encode(passcode));
            statement.setString(6, email);

            statement.execute();

            return new AuthResponse(Responses.SUCCESS, sessionToken);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new AuthResponse(Responses.ERROR, null, "An error occurred");
    }

    @Override
    public UserResponse getUserData(String sessionToken) throws RemoteException {
        try {
            PreparedStatement statement = Database.get()
                    .prepareStatement("SELECT * FROM User WHERE session_token = ?");
            statement.setString(1, sessionToken);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return new UserResponse(Responses.SUCCESS, User.fromResultSet(result));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public PaymentResponse initiatePayment(String sessionToken, int amount, String receiverPhone,
                               String note) throws RemoteException {
        Connection conn = Database.get();

        try {
            conn.setAutoCommit(false);

            PreparedStatement statement1 = conn
                    .prepareStatement("SELECT user_id, balance FROM User WHERE session_token = ?");
            statement1.setString(1, sessionToken);
            ResultSet sender = statement1.executeQuery();

            PreparedStatement statement2 = conn
                    .prepareStatement("SELECT user_id, balance FROM User WHERE phone_number = ?");
            statement2.setString(1, receiverPhone);
            ResultSet receiver = statement2.executeQuery();

            if (!sender.next()) {
                return new PaymentResponse(Responses.ERROR, "Invalid session");
            }

            if (!receiver.next()) {
                return new PaymentResponse(Responses.ERROR, "Could not find beneficiary account");
            }

            if (sender.getString("user_id").equals(receiver.getString("user_id"))) {
                return new PaymentResponse(Responses.ERROR, "You can not make a payment to yourself");
            }

            if (sender.getInt("balance") < amount) {
                return new PaymentResponse(Responses.ERROR, "You do not have enough funds " +
                        "to perform this transaction");
            }

            PreparedStatement statement3 = conn
                    .prepareStatement("UPDATE User SET balance = ? WHERE user_id = ?");
            statement3.setInt(1, sender.getInt("balance") - amount);
            statement3.setString(2, sender.getString("user_id"));
            statement3.execute();

            PreparedStatement statement4 = conn
                    .prepareStatement("UPDATE User SET balance = ? WHERE user_id = ?");
            statement4.setInt(1, receiver.getInt("balance") + amount);
            statement4.setString(2, receiver.getString("user_id"));
            statement4.execute();

            PreparedStatement statement5 = conn
                    .prepareStatement("""
                        INSERT INTO Transaction (
                            transaction_id, sender_id, receiver_id, amount, timestamp, status
                        ) VALUES (?, ?, ?, ?, ?, ?)
                    """);
            statement5.setString(1, UUID.randomUUID().toString());
            statement5.setString(2, sender.getString("user_id"));
            statement5.setString(3, receiver.getString("user_id"));
            statement5.setInt(4, amount);
            statement5.setTimestamp(5, Timestamp.from(Instant.now()));
            statement5.setString(6, "success");
            statement5.execute();

            conn.commit();

            return new PaymentResponse(Responses.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return new PaymentResponse(Responses.ERROR, "An error occurred");
    }

    @Override
    public TransactionsResponse getTransactions(String sessionToken) throws RemoteException {
        try {
            PreparedStatement statement1 = Database.get()
                    .prepareStatement("SELECT * FROM User WHERE session_token = ?");
            statement1.setString(1, sessionToken);
            ResultSet user = statement1.executeQuery();

            if (!user.next()) {
                return new TransactionsResponse(Responses.ERROR, null, "Invalid session");
            }

            PreparedStatement statement2 = Database.get()
                    .prepareStatement("""
                        SELECT * FROM transaction
                        WHERE sender_id = ? OR receiver_id = ? 
                        ORDER BY timestamp DESC
                    """);
            statement2.setString(1, user.getString("user_id"));
            statement2.setString(2, user.getString("user_id"));
            ResultSet transactionsResult = statement2.executeQuery();

            ArrayList<Transaction> transactions = new ArrayList<>();

            while (transactionsResult.next()) {
                transactions.add(Transaction.fromResultSet(transactionsResult));
            }

            return new TransactionsResponse(Responses.SUCCESS, transactions, "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new TransactionsResponse(Responses.ERROR, null, "An error occurred");
    }

}

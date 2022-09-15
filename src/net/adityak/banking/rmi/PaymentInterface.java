package net.adityak.banking.rmi;

import net.adityak.banking.rmi.response.AuthResponse;
import net.adityak.banking.rmi.response.PaymentResponse;
import net.adityak.banking.rmi.response.TransactionsResponse;
import net.adityak.banking.rmi.response.UserResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PaymentInterface extends Remote {
    String getUserName(String phoneNumber) throws RemoteException;

    AuthResponse login(String phoneNumber, String passcode) throws RemoteException;

    AuthResponse register(String name,
                    String phoneNumber,
                    String passcode,
                    String email) throws RemoteException;

    UserResponse getUserData(String sessionToken) throws RemoteException;

    PaymentResponse initiatePayment(String sessionToken,
                                    int amount,
                                    String receiverPhone,
                                    String note) throws RemoteException;

    TransactionsResponse getTransactions(String sessionToken) throws RemoteException;
}

package net.adityak.banking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LogInterface extends Remote {
    void log(int sourceNode, String message) throws RemoteException;
}

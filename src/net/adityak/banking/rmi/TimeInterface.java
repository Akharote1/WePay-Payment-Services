package net.adityak.banking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TimeInterface extends Remote {
    long getSystemTime() throws RemoteException;
    void connectSlave(String rmiName) throws RemoteException;
    void syncSystemTime(long updatedTime) throws RemoteException;
}

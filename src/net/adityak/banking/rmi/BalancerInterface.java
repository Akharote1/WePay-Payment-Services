package net.adityak.banking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BalancerInterface extends Remote {
    int getNextNode() throws RemoteException;
}

package net.adityak.banking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ElectionInterface extends Remote {
    void electionPing(int fromNode) throws RemoteException;
    void onMasterUpdate(int newMasterNode) throws RemoteException;
}

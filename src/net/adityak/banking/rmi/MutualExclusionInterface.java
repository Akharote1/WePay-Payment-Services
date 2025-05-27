package net.adityak.banking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MutualExclusionInterface extends Remote {
    void criticalSectionRequest(int fromNode, long timestamp) throws RemoteException;
    void criticalSectionReply(int fromNode) throws RemoteException;
}

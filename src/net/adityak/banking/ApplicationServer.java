package net.adityak.banking;

import net.adityak.banking.rmi.PaymentImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {
    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(Config.RMI_PORT);
        registry.rebind(Config.RMI_NAME, new PaymentImpl());
        System.out.println("WePay RMI server running on port " + Config.RMI_PORT);
    }
}

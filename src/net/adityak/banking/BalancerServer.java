package net.adityak.banking;

import net.adityak.banking.rmi.BalancerImpl;
import net.adityak.banking.rmi.PaymentImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static net.adityak.banking.ApplicationServer.getNodeCount;
import static net.adityak.banking.ApplicationServer.shouldCreateRegistry;

public class BalancerServer {
    public static Registry registry;

    public static void main(String[] args) throws RemoteException {
        int nodeCount = getNodeCount(args);

        if (!shouldCreateRegistry(args)) {
            registry = LocateRegistry.getRegistry(Config.RMI_PORT);
        } else {
            registry = LocateRegistry.createRegistry(Config.RMI_PORT);
        }

        String rmiName = Config.RMI_NAME + "balancer";

        BalancerImpl impl = new BalancerImpl(nodeCount);
        registry.rebind(rmiName, impl);

        System.out.println("WePay RMI Load Balancer running on port " + Config.RMI_PORT);
    }
}

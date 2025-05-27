package net.adityak.banking;

import net.adityak.banking.rmi.PaymentImpl;
import net.adityak.banking.rmi.TimeInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {
    public static Registry registry;

    public static void main(String[] args) throws RemoteException {
        int nodeId = getNodeId(args);
        int nodeCount = getNodeCount(args);

        if (!shouldCreateRegistry(args)) {
            registry = LocateRegistry.getRegistry(Config.RMI_PORT);
        } else {
            registry = LocateRegistry.createRegistry(Config.RMI_PORT);
        }

        String rmiName = Config.RMI_NAME + nodeId;

        PaymentImpl impl = new PaymentImpl(nodeId, nodeCount);
        registry.rebind(rmiName, impl);

        System.out.println("WePay RMI server (Node " + nodeId + ") running on port " + Config.RMI_PORT);
        impl.electionController.startElection();
        impl.mutualExclusionController.showEntryMenu();
    }

    public static boolean shouldCreateRegistry(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--create-registry")) {
                return true;
            }
        }
        return false;
    }

    public static int getNodeId(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--id")) {
                if (args.length == i + 1) return -1;
                return Integer.parseInt(args[i + 1]);
            }
        }
        return -1;
    }

    public static int getNodeCount(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--nodes")) {
                if (args.length == i + 1) return -1;
                return Integer.parseInt(args[i + 1]);
            }
        }

        return 0;
    }
}

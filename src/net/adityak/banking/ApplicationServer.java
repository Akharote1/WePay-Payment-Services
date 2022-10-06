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
    public static void main(String[] args) throws RemoteException {
        // Slave ID if slave, -1 if master
        int slaveId = getSlaveId(args);
        int slaveCount = getSlaveCount(args);
        boolean isSlave = slaveId != -1;

        Registry registry;

        if (isSlave) {
            registry = LocateRegistry.getRegistry(Config.RMI_PORT);
        } else {
            registry = LocateRegistry.createRegistry(Config.RMI_PORT);
        }

        String rmiName = Config.RMI_NAME + (isSlave
                ? slaveId
                : "");

        registry.rebind(rmiName, new PaymentImpl(slaveId, slaveCount));

        System.out.println("WePay RMI server (" + (!isSlave ? "Master" : "Slave " + slaveId)
                + ") running on port " + Config.RMI_PORT);

        if (!isSlave) {
            System.out.println("Waiting for " + slaveCount + " slaves to connect.\n");
        } else {
            try {
                TimeInterface timeInterface = (TimeInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay");
                timeInterface.connectSlave(rmiName);
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getSlaveId(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--slave")) {
                if (args.length == i + 1) return -1;
                return Integer.parseInt(args[i + 1]);
            }
        }
        return -1;
    }

    public static int getSlaveCount(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--slaves")) {
                if (args.length == i + 1) return -1;
                return Integer.parseInt(args[i + 1]);
            }
        }

        return 0;
    }
}

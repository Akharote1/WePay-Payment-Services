package net.adityak.banking.utils;

import net.adityak.banking.Config;
import net.adityak.banking.rmi.BalancerInterface;
import net.adityak.banking.rmi.PaymentInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class PaymentManager {
    private static PaymentInterface paymentInterface;
    private static String sessionToken;

    public static PaymentInterface get() {
        if (paymentInterface == null) {
            try {
                BalancerInterface balancerInterface = (BalancerInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/" + Config.RMI_NAME + "balancer");

                int nodeId = balancerInterface.getNextNode();

                if (nodeId == -1) {
                    System.out.println("No active servers found");
                    return null;
                }

                paymentInterface = (PaymentInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/"
                                + Config.RMI_NAME + nodeId);

                System.out.println("Connected to Server " + nodeId);
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
                System.out.println("Unable to connect to load balancer");
                e.printStackTrace();
            }
        }
        return paymentInterface;
    }

    public static String getSessionToken() {
        return sessionToken;
    }

    public static void setSessionToken(String sessionToken) {
        PaymentManager.sessionToken = sessionToken;
    }
}

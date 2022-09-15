package net.adityak.banking.utils;

import net.adityak.banking.Config;
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
                paymentInterface = (PaymentInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay");
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
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

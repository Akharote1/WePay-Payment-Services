package net.adityak.banking;

import net.adityak.banking.utils.PaymentManager;
import net.adityak.banking.screens.ScreenManager;
import net.adityak.banking.screens.ui.AuthScreen;

public class Client {
    public static void main(String[] args) {
        if (PaymentManager.get() == null) {
            System.out.println("WePay servers are currently offline.");
            return;
        }

        ScreenManager.push(new AuthScreen());
    }
}

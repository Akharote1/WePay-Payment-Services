package net.adityak.banking.screens.ui;

import net.adityak.banking.models.User;
import net.adityak.banking.rmi.Responses;
import net.adityak.banking.rmi.response.UserResponse;
import net.adityak.banking.screens.Screen;
import net.adityak.banking.screens.ScreenManager;
import net.adityak.banking.utils.PaymentManager;

import java.rmi.RemoteException;
import java.util.Scanner;

public class HomeScreen extends Screen {
    Scanner scanner = new Scanner(System.in);
    User user;

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("\nLoading...\n");

        try {
            UserResponse response = PaymentManager.get().getUserData(PaymentManager.getSessionToken());
            if (response.status == Responses.SUCCESS) {
                user = response.user;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        System.out.println("Hello " + user.getName() + "!");
        System.out.printf("Your Balance: ₹ %.2f\n\n", user.getBalance() / 100.0);

        System.out.println("1] Make a payment");
        System.out.println("2] View transaction history");
        System.out.println("3] Logout & quit");

        System.out.print("> ");

        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            ScreenManager.push(new PaymentBeneficiaryScreen());
        } else if (choice == 2) {
            ScreenManager.push(new TransactionHistoryScreen(user));
        } else if (choice == 3) {
            // Exit
        }
    }
}

package net.adityak.banking.screens.ui;

import net.adityak.banking.models.User;
import net.adityak.banking.rmi.Responses;
import net.adityak.banking.rmi.response.PaymentResponse;
import net.adityak.banking.screens.Screen;
import net.adityak.banking.screens.ScreenManager;
import net.adityak.banking.utils.PaymentManager;
import net.adityak.banking.utils.Table;

import java.rmi.RemoteException;
import java.util.Scanner;

public class PaymentSummaryScreen extends Screen {
    User user;
    String recipientName;
    String recipientPhone;
    String note;
    int amount;
    Scanner scanner = new Scanner(System.in);

    public PaymentSummaryScreen(User user, String recipientName, String recipientPhone, int amount, String note) {
        this.user = user;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.amount = amount;
        this.note = note;
    }

    @Override
    public void render() {
        Table.create()
                .setTitle("Confirm Transaction")
                .setHeading("Detail", "Value")
                .addRow("Beneficiary Name", recipientName)
                .addRow("Beneficiary Phone No.", recipientPhone)
//                .addRow("Current Balance", String.format("₹ %.2f", user.getBalance() / 100.0f))
                .addRow("Amount", String.format("₹ %.2f", amount / 100.0f))
                .addRow("Note / Remarks", note)
                .print();

        System.out.println();
        System.out.println("1] Confirm Transaction");
        System.out.println("2] Cancel");

        System.out.print("> ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 1) {
            try {
                System.out.println("\nProcessing transaction...\n");
                PaymentResponse response = PaymentManager.get().initiatePayment(
                        PaymentManager.getSessionToken(), amount, recipientPhone, note);

                if (response.status == Responses.SUCCESS) {
                    System.out.println("Transaction Complete!");
                } else {
                    System.out.println("Error: " + response.message);
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ScreenManager.replace(new HomeScreen());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            ScreenManager.pop();
        }
    }
}

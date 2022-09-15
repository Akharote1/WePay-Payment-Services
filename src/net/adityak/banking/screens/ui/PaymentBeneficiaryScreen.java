package net.adityak.banking.screens.ui;

import net.adityak.banking.models.User;
import net.adityak.banking.rmi.Responses;
import net.adityak.banking.rmi.response.UserResponse;
import net.adityak.banking.screens.Screen;
import net.adityak.banking.screens.ScreenManager;
import net.adityak.banking.utils.PaymentManager;
import net.adityak.banking.utils.Utils;

import java.rmi.RemoteException;
import java.util.Scanner;

import static net.adityak.banking.utils.Utils.formatPhoneNumber;

public class PaymentBeneficiaryScreen extends Screen {
    Scanner scanner = new Scanner(System.in);
    String beneficiaryName, beneficiaryPhoneNumber, transactionNote;
    float amount = 0.0f;
    User user;

    @Override
    public void onStart() {
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
        try {
            handlePaymentFlow();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    public void handlePaymentFlow() throws RemoteException {
        System.out.println("======== Make a payment ========\n");

        while (true) {
            System.out.print("Beneficiary Phone No.: ");
            beneficiaryPhoneNumber = formatPhoneNumber(scanner.nextLine().trim());

            if (!Utils.isValidPhoneNumber(beneficiaryPhoneNumber)) {
                System.out.println("Invalid phone number \n");
            } else {
                beneficiaryName = PaymentManager.get().getUserName(beneficiaryPhoneNumber);

                if (beneficiaryName == null) {
                    System.out.println("That phone number is not associated with a WePay account. \n");
                } else {
                    System.out.println("Beneficiary Name: " + beneficiaryName);
                    break;
                }
            }
        }

        System.out.println();
        System.out.printf("Your Balance: ₹ %.2f \n", user.getBalance() / 100.0f);
        System.out.println();

        while (true) {
            System.out.print("Amount: ");

            try {
                amount = Float.parseFloat(scanner.nextLine().trim());

                if (amount <= 0) {
                    throw new NumberFormatException();
                }
//                else if ((int) amount * 100 > user.getBalance()) {
//                    System.out.println("You do not have enough funds to transfer that amount.\n");
//                }
                else break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount\n");
            }
        }

        System.out.print("Note (Optional): ");
        transactionNote = scanner.nextLine().trim();
        System.out.println();

        ScreenManager.replace(new PaymentSummaryScreen(
                user,
                beneficiaryName,
                beneficiaryPhoneNumber,
                (int) Math.floor(amount * 100),
                transactionNote
        ));
    }
}

package net.adityak.banking.screens.ui;

import net.adityak.banking.rmi.response.AuthResponse;
import net.adityak.banking.screens.ScreenManager;
import net.adityak.banking.utils.PaymentManager;
import net.adityak.banking.rmi.Responses;
import net.adityak.banking.screens.Screen;
import net.adityak.banking.utils.Utils;

import java.rmi.RemoteException;
import java.util.Scanner;

import static net.adityak.banking.utils.Utils.formatPhoneNumber;
import static net.adityak.banking.utils.Utils.isValidEmail;

public class AuthScreen extends Screen {
    Scanner scanner = new Scanner(System.in);
    String name, phoneNumber, passcode, confirmPasscode;

    @Override
    public void render() {
        try {
            handleAuthFlow();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void handleAuthFlow() throws RemoteException {
        while (true) {
            System.out.print("Phone Number: ");
            phoneNumber = formatPhoneNumber(scanner.nextLine().trim());

            if (!Utils.isValidPhoneNumber(phoneNumber)) {
                System.out.println("Invalid phone number \n");
            } else {
                break;
            }
        }

        String userName = PaymentManager.get().getUserName(phoneNumber);

        if (userName == null) {
            handleRegisterFlow(phoneNumber);
        } else {
            handleLoginFlow(phoneNumber);
        }
    }

    private void handleRegisterFlow(String phoneNumber) throws RemoteException {
        System.out.println("\nRegistering new user\n");

        String name, email, passcode, confirmPasscode;

        while (true) {
            System.out.print("Customer Name: ");
            name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Please enter a valid name \n");
            } else {
                break;
            }
        }

        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();

            if (!isValidEmail(email)) {
                System.out.println("Please enter a valid email \n");
            } else {
                break;
            }
        }

        while (true) {
            System.out.print("Passcode: ");
            passcode = Utils.readPassword(scanner);

            if (passcode.isEmpty()) {
                System.out.println("Please enter a valid passcode \n");
            } else {
                break;
            }
        }

        while (true) {
            System.out.print("Re-enter Passcode: ");
            confirmPasscode = scanner.nextLine().trim();

            if (!confirmPasscode.equals(passcode)) {
                System.out.println("Passcodes do not match \n");
            } else {
                break;
            }
        }

        AuthResponse registerResponse = PaymentManager.get().register(name, phoneNumber, passcode, email);

        if (registerResponse.status == Responses.SUCCESS) {
            System.out.println("Successfully registered!");
            PaymentManager.setSessionToken(registerResponse.sessionToken);
            ScreenManager.replace(new HomeScreen());
        } else {
            System.out.println("An error occurred while creating your account");
        }
    }

    private void handleLoginFlow(String phoneNumber) throws RemoteException {
        String passcode;

        while (true) {
            System.out.print("Passcode: ");
            passcode = Utils.readPassword(scanner);

            if (passcode.isEmpty()) {
                System.out.println("Please enter a valid passcode \n");
            } else {
                AuthResponse loginResponse = PaymentManager.get().login(phoneNumber, passcode);

                if (loginResponse.status == Responses.SUCCESS) {
                    System.out.println("Successfully logged in!\n");
                    PaymentManager.setSessionToken(loginResponse.sessionToken);
                    ScreenManager.replace(new HomeScreen());
                    break;
                } else if (loginResponse.status == Responses.ERROR) {
                    System.out.println(loginResponse.message + "\n");
                }
            }
        }
    }
}

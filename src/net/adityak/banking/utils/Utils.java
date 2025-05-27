package net.adityak.banking.utils;

import net.adityak.banking.Config;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Utils {
    public static String formatPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll(" ", "");
        if (phoneNumber.startsWith("+")) {
            phoneNumber = phoneNumber.substring(3);
        }
        return phoneNumber;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll(" ", "").matches("^(\\+\\d\\d)?\\d{10}$");
    }

    public static boolean isValidEmail(String email) {
        return email.trim().toLowerCase().matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}");
    }

    public static String readPassword(Scanner fallback) {
        if (System.console() != null) {
            return String.valueOf(System.console().readPassword()).trim();
        }
        return fallback.nextLine().trim();
    }

    public static void clearConsole() {
        final String os = System.getProperty("os.name");

        try {
            if (os.toLowerCase().contains("windows")) {
//                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

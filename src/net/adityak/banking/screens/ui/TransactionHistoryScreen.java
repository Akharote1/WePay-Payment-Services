package net.adityak.banking.screens.ui;

import net.adityak.banking.models.Transaction;
import net.adityak.banking.models.User;
import net.adityak.banking.rmi.Responses;
import net.adityak.banking.rmi.response.TransactionsResponse;
import net.adityak.banking.rmi.response.UserResponse;
import net.adityak.banking.screens.Screen;
import net.adityak.banking.screens.ScreenManager;
import net.adityak.banking.utils.PaymentManager;
import net.adityak.banking.utils.Table;

import java.rmi.RemoteException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Scanner;

public class TransactionHistoryScreen extends Screen {
    ArrayList<Transaction> transactions;
    User user;

    public TransactionHistoryScreen(User user) {
        this.user = user;
    }

    @Override
    public void onStart() {
        try {
            TransactionsResponse response =
                    PaymentManager.get().getTransactions(PaymentManager.getSessionToken());
            if (response.status == Responses.SUCCESS) {
                transactions = response.transactions;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        Table table  = Table.create()
                .setTitle("Transaction History")
                .setHeading("Timestamp", "Amount", "Type", "To / From", "Status");

        transactions.forEach(transaction -> table.addRow(
                transaction.getTimestamp().toLocalDateTime()
                        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                String.format("%.2f", transaction.getAmount() / 100.0f),
                transaction.getSenderId().equals(user.getId())
                        ? "Debit" : "Credit",
                transaction.getSenderId().equals(user.getId())
                        ? transaction.getReceiverId()
                        : transaction.getSenderId(),
                transaction.getStatus()
        ));

        table.print();

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        ScreenManager.pop();
    }
}

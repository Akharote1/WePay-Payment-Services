package net.adityak.banking.utils;

import net.adityak.banking.Config;
import net.adityak.banking.rmi.MutualExclusionInterface;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class MutualExclusionController {
    public int nodeCount;
    public int nodeId;
    public boolean isInCriticalSection = false;
    public boolean isRequesting = false;
    public long requestTime = 0;

    public Scanner scanner;

    public ArrayList<Integer> waitingForNodes = new ArrayList<>();
    public ArrayList<Integer> deferredNodes = new ArrayList<>();
    public AtomicBoolean canEnterCriticalSection = new AtomicBoolean(true);

    public MutualExclusionController(int nodeCount, int nodeId) {
        this.nodeCount = nodeCount;
        this.nodeId = nodeId;
        scanner = new Scanner(System.in);
    }

    public void requestCriticalSection() {
        waitingForNodes = new ArrayList<>();
        isRequesting = true;

        for (int i = 0; i < nodeCount; i++) {
            if (i == nodeId) continue;
            waitingForNodes.add(i);
        }

        for (int i = 0; i < nodeCount; i++) {
            if (i == nodeId) continue;
            try {
                MutualExclusionInterface node = (MutualExclusionInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay" + i);
                node.criticalSectionRequest(nodeId, System.currentTimeMillis());
            } catch (Exception e) {
                waitingForNodes.remove((Integer) i);
            }
        }

        if (waitingForNodes.size() == 0) {
            System.out.println("No other nodes are in critical section");
            runCriticalSection();
        } else {
            canEnterCriticalSection.set(false);
            while (!canEnterCriticalSection.get());
            runCriticalSection();
        }

        System.out.println();
    }

    public void handleRequest(int fromId, long timestamp) {
        if (isInCriticalSection || (isRequesting && timestamp >= requestTime)) {
            System.out.println("Request from node " + fromId + ". Adding to deferred list");
            deferredNodes.add(fromId);
        } else {
            System.out.println("Request from node " + fromId + ". Sending reply");
            new Thread(() -> {
                try {
                    MutualExclusionInterface node = (MutualExclusionInterface)
                            Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay" + fromId);
                    node.criticalSectionReply(nodeId);
                } catch (Exception e) { }
            }).start();
            System.out.println("Request from node " + fromId + ". Sent reply");
        }
    }

    public void handleReply(int fromId) {
        waitingForNodes.remove((Integer) fromId);
        System.out.println("Reply from " + fromId);

        if (waitingForNodes.size() == 0 && fromId != nodeId) {
            canEnterCriticalSection.set(true);
        }
    }

    public void runCriticalSection() {
        isInCriticalSection = true;
        isRequesting = false;
        waitingForNodes.clear();
        System.out.println("Critical section running\n");
        System.out.println("What would you like to do?");
        System.out.println("1) Leave critical section");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            isInCriticalSection = false;

            ArrayList<Integer> deferred = new ArrayList<>();
            deferred.addAll(deferredNodes);
            System.out.println("Leaving critical section");

            new Thread(() -> {
                try {
                    for (int i = 0; i < deferred.size(); i++) {
                        if (deferred.get(i) == nodeId) continue;
                        MutualExclusionInterface node = (MutualExclusionInterface)
                                Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay" + deferred.get(i));
                        node.criticalSectionReply(nodeId);
                    }
                } catch (Exception e) { }
            }).start();

            deferredNodes.clear();
            System.out.println("Left critical section");
        }
    }

    public void showEntryMenu() {
        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1) Attempt to enter critical section");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                requestCriticalSection();
            }
        }
    }
}

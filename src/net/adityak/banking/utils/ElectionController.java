package net.adityak.banking.utils;

import net.adityak.banking.Config;
import net.adityak.banking.rmi.ElectionInterface;

import java.rmi.Naming;

public class ElectionController {
    public int currentCoordinator = -1;
    public int nodeCount;
    public int nodeId;

    public ElectionController(int nodeCount, int nodeId) {
        this.nodeCount = nodeCount;
        this.nodeId = nodeId;
    }

    public void startElection() {
        System.out.println("[Election] Starting\n");
        boolean isHighest = true;

        for (int i = nodeId + 1; i < nodeCount; i++) {
            try {
                ElectionInterface node = (ElectionInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay" + i);
                node.electionPing(nodeId);

                System.out.println("[Election] Node " + i + " is online. That node will continue the election");
                isHighest = false;
            } catch (Exception e) {
                System.out.println("[Election] Node " + i + " is offline.");
            }
        }

        System.out.println();

        if (isHighest) {
            System.out.println("[Election] Appointing self as co-ordinator\n");
            broadcastCoordinator();
        }
    }

    public void broadcastCoordinator() {
        for (int i = 0; i < nodeCount; i++) {
            try {
                ElectionInterface node = (ElectionInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay" + i);
                node.onMasterUpdate(nodeId);
            } catch (Exception e) {}
        }
    }
}

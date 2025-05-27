package net.adityak.banking.rmi;

import net.adityak.banking.Config;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BalancerImpl extends UnicastRemoteObject implements BalancerInterface {
    public ArrayList<Integer> activeNodes = new ArrayList<>();
    public int requestCount = 0;
    public int nodeCount;

    public BalancerImpl(int nodeCount) throws RemoteException {
        super();

        this.nodeCount = nodeCount;
        Timer timer = new Timer();
        timer.schedule(new FetchActiveNodesTask(), 0, 5000);
    }

    public void fetchActiveNodes() {
        ArrayList<Integer> newActiveNodes = new ArrayList<>();

        for (int i = 0; i < nodeCount; i++) {
            try {
                PaymentInterface node = (PaymentInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay" + i);
                node.ping();
                newActiveNodes.add(i);
            } catch (Exception e) { }
        }

        System.out.println("Found active nodes: " + newActiveNodes.size());
        activeNodes.clear();
        activeNodes.addAll(newActiveNodes);
    }

    @Override
    public int getNextNode() throws RemoteException {
        if (activeNodes.size() == 0) return -1;

        requestCount = (requestCount + 1) % activeNodes.size();
        return activeNodes.get(requestCount);
    }

    class FetchActiveNodesTask extends TimerTask {

        @Override
        public void run() {
            fetchActiveNodes();
        }
    }
}

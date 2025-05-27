package net.adityak.banking.utils;

import net.adityak.banking.Config;
import net.adityak.banking.rmi.LogInterface;
import net.adityak.banking.rmi.MutualExclusionInterface;

import java.io.*;
import java.rmi.Naming;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class LogController {
    public int nodeCount;
    public int nodeId;

    private BufferedWriter writer;

    public LogController(int nodeCount, int nodeId) {
        this.nodeCount = nodeCount;
        this.nodeId = nodeId;

        File logFile = new File("logs/node-" + nodeId + ".log");

        try {
            logFile.getParentFile().mkdirs();
            if (!logFile.exists()) logFile.createNewFile();

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        handleLog(nodeId, message);

        for (int i = 0; i < nodeCount; i++) {
            if (i == nodeId) continue;
            try {
                LogInterface node = (LogInterface)
                        Naming.lookup("rmi://localhost:" + Config.RMI_PORT + "/wepay" + i);
                node.log(nodeId, message);
            } catch (Exception e) {}
        }
    }

    public void handleLog(int sourceNode, String message) {
        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
        String out = "[" + time + "] " + "[Node" + sourceNode + "] " + message;
        System.out.println(out);

        try {
            writer.write(out + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

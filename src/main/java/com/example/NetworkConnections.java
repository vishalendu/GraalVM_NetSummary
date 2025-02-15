package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class NetworkConnections {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Process ID (PID): ");
        String pid = scanner.nextLine();

        List<String[]> connections = getNetworkConnections(pid);

//        printTable(connections);
        displaySortedConnections(connections);
    }

    private static List<String[]> getNetworkConnections(String pid) {
        List<String[]> connections = new ArrayList<>();
        try {
            ProcessBuilder builder;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                builder = new ProcessBuilder("powershell", "-Command", "Get-NetTCPConnection | Where-Object { $_.OwningProcess -eq " + pid + " } | Format-Table -HideTableHeaders LocalAddress,LocalPort,RemoteAddress,RemotePort,State");
            } else {
                builder = new ProcessBuilder("bash", "-c", "lsof -i -n -P | grep " + pid);
            }

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Raw command output:");
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                String[] parts = parseConnectionLine(line);
                if (parts != null) {
                    connections.add(parts);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error fetching network connections: " + e.getMessage());
        }
        return connections;
    }

    private static String[] parseConnectionLine(String line) {
        String[] parts = line.trim().split("\\s+");

        // Ensure the output contains enough parts
        if (parts.length < 9) return null;

        // Extract protocol (TCP or UDP)
        String protocol = parts[7];

        // Extract local and foreign address
        String localAddress = parts[8];
        String foreignAddress = "-"; // Default if not connected
        String state = "-"; // Default state

        // Check if there is a "->" indicating a connection
        if (localAddress.contains("->")) {
            String[] addresses = localAddress.split("->");
            localAddress = addresses[0].trim(); // Local address before "->"
            foreignAddress = addresses[1].trim(); // Remote address after "->"
        }

        // Extract state from the last part of the line if enclosed in parentheses
        if (line.contains("(") && line.contains(")")) {
            int start = line.lastIndexOf("(") + 1;
            int end = line.lastIndexOf(")");
            if (start < end) {
                state = line.substring(start, end).trim();
            }
        }

        // Determine connection direction
        String direction;
        if ("ESTABLISHED".equalsIgnoreCase(state)) {
            direction = "Outgoing";
        } else if ("LISTEN".equalsIgnoreCase(state) || "BOUND".equalsIgnoreCase(state)) {
            direction = "Listening";
        } else if ("CLOSE_WAIT".equalsIgnoreCase(state) || "FIN_WAIT1".equalsIgnoreCase(state) ||
                "CLOSING".equalsIgnoreCase(state) || "LAST_ACK".equalsIgnoreCase(state) ||
                "FIN_WAIT_2".equalsIgnoreCase(state) || "TIME_WAIT".equalsIgnoreCase(state)) {
            direction = "Closing";
        } else if ("CLOSED".equalsIgnoreCase(state)){
            direction = "Closed";
        } else if("SYN_SENT".equalsIgnoreCase(state) || "SYN_RCDV".equalsIgnoreCase(state)){
            direction = "Connecting";
        } else if("IDLE".equalsIgnoreCase(state)){
            direction = "Idle";
        }
        else {
            direction = "-"; // Unknown state
        }

        return new String[]{protocol, localAddress, foreignAddress, state, direction};
    }

    public static void displaySortedConnections(List<String[]> connections) {
        // Sort by Protocol (TCP first, then UDP)
        Collections.sort(connections, Comparator.comparing(entry -> entry[0]));

        // Print table header
        System.out.printf("%-10s %-25s %-25s %-15s %-10s%n",
                "Protocol", "Local Address:Port", "Foreign Address:Port", "State", "Direction");
        System.out.println("--------------------------------------------------------------------------------");

        // Print each connection
        for (String[] connection : connections) {
            System.out.printf("%-10s %-25s %-25s %-15s %-10s%n",
                    connection[0], connection[1], connection[2], connection[3], connection[4]);
        }
    }

    private static void printTable(List<String[]> connections) {
        System.out.printf("%-10s %-25s %-25s %-15s %-10s%n", "Protocol", "Local Address:Port", "Foreign Address:Port", "State", "Direction");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (String[] row : connections) {
            System.out.printf("%-10s %-25s %-25s %-15s %-10s%n", row[0], row[1], row[2], row[3], row[4]);
        }
    }
}
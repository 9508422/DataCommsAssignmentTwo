package com.rhys.fileserver;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class FileServer {

    public static void main(String[] args) {
        int inputPort = 8888;
        ServerSocket serverSocket = null;

        if(args.length > 0) {
            try {
                inputPort = Integer.parseInt(args[0]);
                if (inputPort > 65535 || inputPort < 0) {
                    System.err.print("Port: " + inputPort + "is outside the valid port range (0 - 65535)");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("Argument " + args[0] + " must be an integer.");
                System.exit(1);
            }
        }

        try {
            serverSocket = new ServerSocket(inputPort);
        } catch (IOException e) {
            System.err.println("Critical error, must terminate. There may be another server bound to port " + inputPort);
            System.exit(1);
        }

        Socket connectionSocket;
        String requestMessageLine;
        String fileName;
        BufferedReader inFromClient;
        DataOutputStream outToClient;

        while (true) {
            try {
                System.out.println("Server status: waiting for a client");
                connectionSocket = serverSocket.accept();
                System.out.println("Server status: " + connectionSocket + " -> client connected");

                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                requestMessageLine = inFromClient.readLine();
                System.out.println("Server status: " + connectionSocket + " -> request string: " + requestMessageLine);

                StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

                if (tokenizedLine.nextToken().equals("Send")) {
                    fileName = tokenizedLine.nextToken();

                    if (fileName.startsWith("/")) {
                        fileName = fileName.substring(1);
                    }

                    if (isFilenameValid(fileName)) {
                        FileInputStream inFile = new FileInputStream("store/" + fileName);
                        outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                        byte[] buffer = new byte[1024];
                        long totalBytesSent = 0;
                        int read = inFile.read(buffer);

                        while (read != -1) {
                            outToClient.write(buffer, 0, read);
                            totalBytesSent += read;
                            read = inFile.read(buffer);
                        }

                        inFile.close();
                        System.out.println("Server status: " + connectionSocket + " -> finished serving: " + fileName + ", Bytes sent: " + totalBytesSent);
                    } else {
                        System.out.println("Server status: Unable to send" + fileName);
                    }
                } else {
                    System.out.println("Server status: Bad request message");
                }
                connectionSocket.close();
            } catch (IOException e) {
                System.err.println("Something has gone wrong with the connection.");
            }
        }
    }

    public static boolean isFilenameValid(String fileName) {
        File file = new File(fileName);
        try {
            file.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

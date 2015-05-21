package com.rhys.fileserver;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Rhys on 21/05/2015.
 */
class FileClient {

    public static void main(String[] args) {
        InetAddress serverIP = null;
        int serverPort = 0;
        String fileString = "";

        /*if (args.length == 3) {
            try {
                serverIP = InetAddress.getByName(args[0]);
            } catch (UnknownHostException e) {
                System.err.println("The IP '" + args[0] + "' is invalid");
                System.exit(1);
            }

            try {
                serverPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("The argument '" + args[1] + "' must be an integer.");
                System.exit(1);
            }

            if (!isFilenameValid(args[2])) {
                System.err.println("The file name '" + args[2] + "' is invalid");
                System.exit(1);
            }
        } else {
            System.out.println("Usage: <IP of UDP Server> <File name>");
            System.exit(1);
        }*/

        try {
            // BufferedReader initiated to read from console input
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            // user inputs server IP
            System.out.println("Server IP:");
            serverIP = InetAddress.getByName(inFromUser.readLine());

            // user inputs server port
            System.out.println("Server port:");
            serverPort = Integer.parseInt(inFromUser.readLine());

            System.out.println("File name:");
            fileString = inFromUser.readLine();
        } catch (IOException e) {
            System.err.println("Invalid input");
        }

        try {
            System.out.println("Client status: Attempting to connect to the server");
            Socket socket = new Socket(serverIP, serverPort);
            System.out.println("Client status: " + socket + " -> connected");

            String requestMessage = "Send " + fileString + "\r\n";
            BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Client status: " + socket + " -> writing message");
            toServer.write(requestMessage, 0, requestMessage.length());

            System.out.println("Client status: " + socket + " -> flushed message");
            toServer.flush();

            FileOutputStream toDisk = new FileOutputStream(new File(fileString));
            DataInputStream fromServer = new DataInputStream(socket.getInputStream());

            System.out.println("Client status:" + socket + " -> attempting network read");
            System.out.println("Client status: " + socket + " -> attempting disk write");
            byte[] buffer = new byte[1024];
            long totalBytesReceived = 0;
            int read = fromServer.read(buffer, 0, 1024);

            while (read != -1) {
                toDisk.write(buffer, 0, read);
                totalBytesReceived += read;
                read = fromServer.read(buffer, 0, 1024);
            }

            System.out.println("Client status: " + socket + " -> " + fileString + " written to disk. Total Bytes received: " + totalBytesReceived);

            toDisk.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Something has gone wrong with the connection");
        }
    }

    private static boolean isFilenameValid(String fileName) {
        File file = new File(fileName);
        try {
            file.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

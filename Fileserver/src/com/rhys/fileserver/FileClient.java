package com.rhys.fileserver;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Rhys Gevaux on 21/05/2015
 * @author Rhys Gevaux 9508422
 *
 * Allows the user to retrieve a file from a server.
 *
 * References:
 * https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 * http://www.cs.uic.edu/~troy/spring05/cs450/sockets/WebServer.java
 * http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 */
class FileClient {

    public static void main(String[] args) {
        InetAddress serverIP = null;
        int serverPort = 0;
        String fileString = "";

        try {
            // BufferedReader initiated to read from console input
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            // user inputs server IP
            System.out.println("Server IP:");
            serverIP = InetAddress.getByName(inFromUser.readLine());

            // user inputs server port
            System.out.println("Server port:");
            serverPort = Integer.parseInt(inFromUser.readLine());

            // user inputs desired filename
            System.out.println("File name:");
            String temp = inFromUser.readLine();

            // check that filename is valid
            isFilenameValid(temp);
            fileString = temp;
        } catch (IOException e) {
            System.err.println("Invalid input");
        }

        try {
            System.out.println("Client status: Attempting to connect to the server");
            Socket socket = new Socket(serverIP, serverPort); // connect to the server
            System.out.println("Client status: " + socket + " -> connected");

            String requestMessage = "Send " + fileString + "\r\n"; // create message to be sent to the server

            // BufferedWriter to send a stream to the server
            BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Send request to the server
            System.out.println("Client status: " + socket + " -> writing message");
            toServer.write(requestMessage, 0, requestMessage.length());

            System.out.println("Client status: " + socket + " -> flushed message");
            toServer.flush();

            // create outputStream to write the server transfer to disk
            FileOutputStream toDisk = new FileOutputStream(new File(fileString));

            // create inputStream to receive file from server
            DataInputStream fromServer = new DataInputStream(socket.getInputStream());

            // fill buffer
            System.out.println("Client status:" + socket + " -> attempting network read");
            System.out.println("Client status: " + socket + " -> attempting disk write");
            byte[] buffer = new byte[1024]; // creates buffer of 1KB
            long totalBytesReceived = 0;
            int read = fromServer.read(buffer, 0, 1024);

            // read file from server
            while (read != -1) {
                toDisk.write(buffer, 0, read);
                totalBytesReceived += read;
                read = fromServer.read(buffer, 0, 1024);
            }

            System.out.println("Client status: " + socket + " -> " + fileString +
                    " written to disk. Total Bytes received: " + totalBytesReceived);

            toDisk.close(); // close outputStream
            socket.close(); // close connection
        } catch (IOException e) {
            System.err.println("Something has gone wrong with the connection");
        }
    }

    /**
     * If exception is thrown (and caught), filename is not valid.
     * @param fileName client's desired file
     * @return if the filename is valid
     */
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

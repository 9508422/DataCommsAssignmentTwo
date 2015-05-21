package com.rhys.fileserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Rhys Gevaux on 21/05/2015
 * @author Rhys Gevaux 9508422
 *
 * Allows a client to get a certain file from the server.
 *
 * References:
 * https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 * http://www.cs.uic.edu/~troy/spring05/cs450/sockets/WebServer.java
 * http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 */
class FileServer {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        int inputPort = 8888; // default port required so that a port is defined
        ServerSocket serverSocket = null; // new server socket, initialised to avoid issues

        // check for arguments
        if (args.length > 0) {
            try {
                inputPort = Integer.parseInt(args[0]); // parse in port
            } catch (NumberFormatException e) {
                System.err.println("Argument " + args[0] + " must be an integer."); // output error if not an integer
                System.exit(1); // exit
            }
        }

        try {
            serverSocket = new ServerSocket(inputPort); // open socket on the defined port
        } catch (IOException e) {
            // exit if port already being used
            System.err.println("Critical error, must terminate. There may be another server bound to port '" +
                    inputPort + "'");
            System.exit(1);
        }

        Socket connectionSocket; // new connection socket to connect server and client together
        String requestMessageLine; // string of client's request
        String fileName; // requested filename for transfer
        BufferedReader inFromClient; // BufferedReader for client's input
        DataOutputStream outToClient; // DataOutputStream to send back the desired file

        while (true) {
            try {
                System.out.println("Server status: waiting for a client");
                connectionSocket = serverSocket.accept(); // accepts the connection from client
                System.out.println("Server status: " + connectionSocket + " -> client connected");

                // initialise BufferedReader, giving it the inputStream from the connection
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                // read in the line of input
                requestMessageLine = inFromClient.readLine();
                System.out.println("Server status: " + connectionSocket + " -> request string: " + requestMessageLine);

                // tokenizes the input
                StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

                // checks the first token
                if (tokenizedLine.nextToken().equals("Send")) {
                    fileName = tokenizedLine.nextToken();

                    // trimming the forward slash
                    if (fileName.startsWith("/")) {
                        fileName = fileName.substring(1);
                    }

                    // checks for valid filename
                    if (isFilenameValid(fileName)) {
                        FileInputStream inFile = new FileInputStream(fileName); // creates an inputStream for the file

                        // initialises the outputStream that will be used to send the file to the client
                        outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                        // fill buffer
                        byte[] buffer = new byte[1024]; // creates a buffer of 1KB
                        long totalBytesSent = 0;
                        int read = inFile.read(buffer);

                        // sends the inputStream through the outputStream to the client
                        while (read != -1) {
                            outToClient.write(buffer, 0, read);
                            totalBytesSent += read;
                            read = inFile.read(buffer);
                        }

                        inFile.close(); // close BufferedReader
                        System.out.println("Server status: " + connectionSocket + " -> finished serving: " + fileName +
                                ", Bytes sent: " + totalBytesSent);
                    } else {
                        System.err.println("Server status: unable to send" + fileName); // not a valid filename
                    }
                } else {
                    System.err.println("Server status: bad request message");
                }
                connectionSocket.close(); // close the connection
            } catch (IOException e) {
                System.err.println("Something has gone wrong with the connection");
            }
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

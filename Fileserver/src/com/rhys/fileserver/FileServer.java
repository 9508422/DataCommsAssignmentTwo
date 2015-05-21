package com.rhys.fileserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Rhys Gevaux on 21/05/2015
 * @author Rhys Gevaux 9508422
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
        int inputPort = 8888;
        ServerSocket serverSocket = null;

        if (args.length > 0) {
            try {
                inputPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument " + args[0] + " must be an integer.");
                System.exit(1);
            }
        }

        try {
            serverSocket = new ServerSocket(inputPort);
        } catch (IOException e) {
            System.err.println("Critical error, must terminate. There may be another server bound to port '" +
                    inputPort + "'");
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
                        FileInputStream inFile = new FileInputStream(fileName);
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
                        System.out.println("Server status: unable to send" + fileName);
                    }
                } else {
                    System.out.println("Server status: bad request message");
                }
                connectionSocket.close();
            } catch (IOException e) {
                System.err.println("Something has gone wrong with the connection");
            }
        }
    }

    /**
     *
     * @param fileName
     * @return
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

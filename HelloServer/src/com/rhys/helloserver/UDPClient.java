package com.rhys.helloserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by Rhys Gevaux on 21/05/2015.
 * @author Rhys Gevaux 9508422
 *
 * Reference: https://systembash.com/a-simple-java-udp-server-and-udp-client/
 */
class UDPClient {

    /**
     * Connects to a UDP server defined by user input, user is prompted to send the server a message.
     * @param args - unused
     */
    public static void main(String[] args) {
        // BufferedReader initiated to read from console input
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        String serverIP = null;
        int serverPort = 8888;
        DatagramSocket clientSocket = null;

        System.out.println("Server IP:"); // ask user for server IP

        // try to set server IP from user input
        try {
            serverIP = inFromUser.readLine(); // user inputs server IP
        } catch (IOException e) {

            System.exit(1);
        }

        System.out.println("Server port:"); // ask user for server port

        // try to ser server port from user input
        try {
            serverPort = Integer.parseInt(inFromUser.readLine()); // user inputs server port
        } catch (IOException e) {

            System.exit(1);
        }

        // try to create a new socket
        try {
            clientSocket = new DatagramSocket(); // new socket for request
        } catch (SocketException e) {
            System.err.println("Critical error, shutting down");
            System.exit(1);
        }

        byte[] sendData = new byte[1024]; // byte array to store the UDP request packet sent to the server
        byte[] receiveData = new byte[1024]; // byte array to store the UDP response packet received from the server

        // system asks user for the desired message to be sent to the server
        System.out.println("Send a message to the server:");

        //try to read in message from user
        try {
            sendData = inFromUser.readLine().getBytes(); // user inputs desired message to send to the server
        } catch (IOException e) {
            System.err.println();
        }

        // try to sent packet through socket
        try {
            // UDP request packet is created and sent through the previously defined socket to the server
            clientSocket.send(new DatagramPacket(sendData, sendData.length, InetAddress.getByName(serverIP),
                    serverPort));
        } catch (IOException e) {
            System.err.println("Something went wrong when sending the request");
        }

        // new packet to receive the UDP response from the server
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            clientSocket.receive(receivePacket); // UDP response packet received
        } catch (IOException e) {
            System.err.println("Something went wrong while receiving the response");
        }
        System.out.println("From server: " + new String(receivePacket.getData())); // output response to console
        clientSocket.close(); // disconnect from the server
    }
}

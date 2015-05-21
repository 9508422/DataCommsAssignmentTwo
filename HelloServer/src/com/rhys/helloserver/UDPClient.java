package com.rhys.helloserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // BufferedReader initiated to read from console input
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        // user inputs server IP
        System.out.println("Server IP:");
        String serverIP = inFromUser.readLine();

        // user inputs server port
        System.out.println("Server port:");
        String serverPort = inFromUser.readLine();

        DatagramSocket clientSocket = new DatagramSocket(); // new socket for request
        byte[] sendData; // byte array to store the UDP request packet sent to the server
        byte[] receiveData = new byte[1024]; // byte array to store the UDP response packet received from the server

        // user inputs desired message to send to the server
        System.out.println("Send a message to the server:");
        sendData = inFromUser.readLine().getBytes();

        // UDP request packet is created and sent through the previously defined socket to the server
        clientSocket.send(new DatagramPacket(sendData, sendData.length, InetAddress.getByName(serverIP),
                Integer.parseInt(serverPort)));

        // new packet to receive the UDP response from the server
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        clientSocket.receive(receivePacket); // UDP response packet received
        System.out.println("From server: " + new String(receivePacket.getData())); // output response to console
        clientSocket.close(); // disconnect from the server
    }
}

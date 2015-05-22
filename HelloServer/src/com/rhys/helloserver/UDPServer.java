package com.rhys.helloserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Rhys Gevaux on 21/05/2015.
 * @author Rhys Gevaux 9508422
 *
 * Reference: https://systembash.com/a-simple-java-udp-server-and-udp-client/
 */
class UDPServer {

    /**
     * Starts up a UDP server that response with my name and student ID when prompted.
     * @param args allows port input
     */
    public static void main(String[] args) {
        // checks for arguments
        if (args.length < 1) {
            // reports an error if no arguments are entered and exits
            System.err.println("No arguments entered!");
            System.exit(1);
        } else {
            // new socket to receive request through defined port
            DatagramSocket serverSocket = null;
            try {
                serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
            } catch (SocketException e) {
                System.err.println("Invalid port argument");
                System.exit(1);
            }

            byte[] receiveData = new byte[1024]; // byte array to store the UDP request packet received from the client
            byte[] sendData; // byte array to store the UDP response packet sent to the client

            // endless loop to keep server running
            while (true) {
                // new packet to receive UDP request from the client
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                // try to receive the packet
                try {
                    serverSocket.receive(receivePacket); // receive the packet
                } catch (IOException e) {
                    System.err.println("Something went wrong when receiving the request");
                }

                // output the request to console
                String sentence = new String(receivePacket.getData());
                System.out.println("Received: " + sentence);

                sendData = "Hello, my name is Rhys Gevaux and my ID is 9508422".getBytes(); // define the response

                // try to send response
                try {
                    serverSocket.send(new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(),
                            receivePacket.getPort())); // send the UDP response to the client
                } catch (IOException e) {
                    System.err.println("Something went wrong when sending the response");
                }
            }
        }
    }
}

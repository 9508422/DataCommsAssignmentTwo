package com.rhys.helloserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Rhys on 21/05/2015.
 */
class UDPServer {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("No arguments entered!");
            System.exit(1);
        } else {
            DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
            byte[] receiveData = new byte[1024];
            byte[] sendData;
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                System.out.println("Received: " + sentence);
                sendData = "Hello, my name is Rhys Gevaux and my ID is 9508422".getBytes();
                serverSocket.send(new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort()));
            }
        }
    }
}

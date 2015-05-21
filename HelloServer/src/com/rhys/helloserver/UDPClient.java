package com.rhys.helloserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Rhys on 21/05/2015.
 */
class UDPClient {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("No arguments entered!");
            System.exit(1);
        } else {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData;
            byte[] receiveData = new byte[1024];
            sendData = inFromUser.readLine().getBytes();
            clientSocket.send(new DatagramPacket(sendData, sendData.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1])));
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            System.out.println("From server: " + new String(receivePacket.getData()));
            clientSocket.close();
        }
    }
}

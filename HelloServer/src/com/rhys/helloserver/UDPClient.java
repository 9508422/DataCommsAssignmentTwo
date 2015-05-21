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
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Server IP: ");
        String serverIP = inFromUser.readLine();

        System.out.println("Server port: ");
        String serverPort = inFromUser.readLine();

        DatagramSocket clientSocket = new DatagramSocket();
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        sendData = inFromUser.readLine().getBytes();
        clientSocket.send(new DatagramPacket(sendData, sendData.length, InetAddress.getByName(serverIP), Integer.parseInt(serverPort)));
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        System.out.println("From server: " + new String(receivePacket.getData()));
        clientSocket.close();
    }
}

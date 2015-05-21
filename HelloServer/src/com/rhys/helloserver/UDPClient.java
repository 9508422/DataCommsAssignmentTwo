package com.rhys.helloserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Rhys on 21/05/2015.
 */
public class UDPClient {

    public static void main(String[] args) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        sendData = inFromUser.readLine().getBytes();
        clientSocket.send(new DatagramPacket(sendData, sendData.length, InetAddress.getByName(args[0]), Integer.parseInt(args[1])));
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        System.out.println("FROM SERVER: " + new String(receivePacket.getData()));
        clientSocket.close();
    }
}
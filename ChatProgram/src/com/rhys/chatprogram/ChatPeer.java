package com.rhys.chatprogram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

class ChatPeer {
    private static HashMap<InetAddress, HashMap<Integer, Peer>> validPeers = new HashMap<>();
    private static DatagramSocket fullDuplexUDPSocket = null;

    public static void main(String[] args) {
        int serverPort = 0;

        if(args.length == 2) {
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }

            try {
                validPeers = Config.load(args[1]);
            } catch (IOException e) {
                System.err.println("Unable to load in configuration file " + args[1] + " check that it is available");
                System.exit(1);
            }

        } else {
            System.out.println("Usage: <Server Port> <Configuration File>");
            System.exit(1);
        }

        System.out.println("------------------------ Imported valid peers -------------------------");
        for ( HashMap<Integer, Peer> portPeerSet: validPeers.values() )
            portPeerSet.values().forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------");

        try {
            fullDuplexUDPSocket = new DatagramSocket(serverPort);
        } catch (SocketException e) {
            System.err.println("Critical error, must terminate. Another server is operating on port '" + serverPort
                    + "'");
            System.exit(1);
        }

        ServerThread st = new ServerThread();
        ClientThread ct = new ClientThread();
        st.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ct.start();
    }

    static class ServerThread extends Thread {
        public ServerThread(){}

        public void run(){
            InetAddress IPAddress = null;
            int port = 0;
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket;
            String receivedMessageString;
            HashSet<InetAddress> unauthorisedPeers = new HashSet<>();

            while(true) {
                try{
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    fullDuplexUDPSocket.receive(receivePacket);
                    IPAddress = receivePacket.getAddress();
                    port = receivePacket.getPort();

                    if(validPeers.containsKey(IPAddress) && validPeers.get(IPAddress).containsKey(port)){
                        receivedMessageString = new String(receivePacket.getData());

                        System.out.println("\nServer Status -> " + validPeers.get(IPAddress).get(port) + " -> " + receivedMessageString.trim());
                    }

                    else  {
                        if(!unauthorisedPeers.contains(IPAddress)){
                            System.out.println("Server -> Unauthorized chat request from <" + IPAddress + ">");

                           unauthorisedPeers.add(IPAddress);
                        }
                    }

                    receiveData = new byte[1024];
                    ClientThread.UserInputPrompt();
                }
                catch (IOException e)
                {
                    System.out.println("Something has gone wrong with " + IPAddress + ":" + port);
                }
            }
        }
    }

    static class ClientThread extends Thread {
        public ClientThread(){}

        public void run(){
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramPacket sendPacket;
            byte[] sendData;
            String sentence;

            while(true) {
                try {
                    UserInputPrompt();
                    try {
                        sentence = inFromUser.readLine();
                    } catch (IOException e) {
                        System.err.println("Commandline input error, try again?");
                        continue;
                    }

                    sentence += "\r\n";
                    sendData = sentence.getBytes();

                    for (HashMap<Integer, Peer> portPeerSet : validPeers.values())
                        for (Peer peer : portPeerSet.values()) {

                            sendPacket = new DatagramPacket(sendData, sendData.length, peer.getPeerIP(), peer.getPeerPort());
                            fullDuplexUDPSocket.send(sendPacket);
                        }

                }
                catch (IOException e)
                {
                    System.out.println("Something has gone wrong sending the packet to the peers, continuing");
                }
            }
        }

        public static void UserInputPrompt(){
            System.out.print("Client status -> Send Message" + " -> ");
        }

    }
}
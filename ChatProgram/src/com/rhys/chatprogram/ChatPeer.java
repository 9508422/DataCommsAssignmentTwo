package com.rhys.chatprogram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Rhys Gevaux on 21/05/2015.
 * @author Rhys Gevaux 9508422
 *
 * Allows peer to talk to people over a network
 *
 * References:
 * https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 * http://www.cs.uic.edu/~troy/spring05/cs450/sockets/WebServer.java
 * http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 * http://www.cse.dmu.ac.uk/~bb/Teaching/NetWorks/Laboratory/UDPclientServer/UDPchat.java
 */
class ChatPeer {
    // stores all the valid peers loaded from the configuration file
    // allows multiple peers to communicate from the same IP
    private static HashMap<InetAddress, HashMap<Integer, Peer>> validPeers = new HashMap<>();

    // duplex socket to allow two way communication between all peers
    private static DatagramSocket fullDuplexUDPSocket = null;

    /**
     * starts project up with given arguments
     *
     * loads config files, sets up valid peers, opens socket
     *
     * @param args port and config
     */
    public static void main(String[] args) {
        int serverPort = 0;

        // checks for the correct number of arguments
        if (args.length == 2) {

            // try to set port from argument
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) { // if not a number
                System.err.println("Argument" + args[0] + " must be an integer");
                System.exit(1);
            }

            // try to set config file
            try {
                validPeers = Config.load(args[1]); // load config file
            } catch (IOException e) { // not a config file / not found
                System.err.println("Unable to load in configuration file " + args[1] + ", check that it is available");
                System.exit(1);
            }

        } else {
            System.out.println("Argument usage: <Server Port> <Configuration File>");
            System.exit(1);
        }

        // print out all valid peers
        System.out.println("------------------------ Imported valid peers -------------------------");
        for (HashMap<Integer, Peer> portPeerSet : validPeers.values())
            portPeerSet.values().forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------");

        // try to set a full duplex socket to allow two way communication
        try {
            fullDuplexUDPSocket = new DatagramSocket(serverPort); // set up duplex socket
        } catch (SocketException e) {
            System.err.println("Critical error, must terminate: another server is operating on port '" + serverPort
                    + "'.");
            System.exit(1);
        }

        ServerThread st = new ServerThread(); // sets up serverThread
        ClientThread ct = new ClientThread(); // sets up clientThread
        st.start(); // starts server

        // delays the client
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ct.start(); // starts client
    }

    /**
     * Waits for packets sent from other peers and displays them in the terminal
     */
    static class ServerThread extends Thread {
        public ServerThread() {
        }

        /**
         * Invoked when ServerThread is started
         */
        public void run() {
            InetAddress IPAddress = null;
            int port = 0;
            byte[] receiveData = new byte[1024]; // bytes to holed received data, set to 1KB
            DatagramPacket receivePacket; // Packet to store received packets
            String receivedMessageString; // String to store received messages

            // stores found unauthorised peers
            HashSet<InetAddress> unauthorisedPeers = new HashSet<>();

            while (true) {
                try {
                    // new packet to store recevied packet
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    // blocks until a chat message has been sent by any peers
                    fullDuplexUDPSocket.receive(receivePacket);

                    // stores ip address and port to check whether they are authorised
                    IPAddress = receivePacket.getAddress();
                    port = receivePacket.getPort();

                    // checks if peer is authorised
                    if (validPeers.containsKey(IPAddress) && validPeers.get(IPAddress).containsKey(port)) {
                        receivedMessageString = new String(receivePacket.getData()); // store packet into string

                        // prints message to the terminal
                        System.out.print("\nServer: " + validPeers.get(IPAddress).get(port) + " -> " +
                                receivedMessageString.trim());

                    // if they are not authorised and haven't been found before
                    } else if (!unauthorisedPeers.contains(IPAddress)) {
                        System.out.print("\nServer: Unauthorised chat request from <" + IPAddress + ">");

                        // add peer to unauthorised list so no output is created next time
                        unauthorisedPeers.add(IPAddress);
                    }

                    receiveData = new byte[1024]; // reset byte array to avoid printing issues
                    ClientThread.UserInputPrompt(); // prompts user that they can send a message
                } catch (IOException e) {
                    System.out.println("Something has gone wrong with '" + IPAddress + ":" + port + "'");
                }
            }
        }
    }

    /**
     * Takes user input and sends it to all peers
     */
    static class ClientThread extends Thread {
        public ClientThread() {
            UserInputPrompt(); // initial prompt to user to send a message
        }

        /**
         * prompts user to send a message
         * method required so that it can be called from ServerThread
         */
        public static void UserInputPrompt() {
            // prompts user that they can send a message
            System.out.print("\nClient: Send message" + " -> ");
        }

        /**
         * Invoked when ClientThread is started
         */
        public void run() {
            // BufferedReader to receive users inputStream
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            DatagramPacket sendPacket; // packet to store data to send
            byte[] sendData; // bytes to store desired message
            String sentence; // takes in users input

            while (true) {
                // try to read in user input into String
                try {
                    sentence = inFromUser.readLine(); // reads user input into String
                } catch (IOException e) {
                    System.err.println("Commandline input error, try again?");
                    continue; // attempts to take in more input
                }

                // try to take user input and put into a packet
                try {
                    sentence += "\r\n"; // adds a new line to the message
                    sendData = sentence.getBytes(); // convert String into bytes so it can be added to the packet

                    // loops over all peers
                    for (HashMap<Integer, Peer> portPeerSet : validPeers.values())
                        for (Peer peer : portPeerSet.values()) {

                            // create the packet to send to peers
                            sendPacket = new DatagramPacket(sendData, sendData.length, peer.getPeerIP(),
                                    peer.getPeerPort());
                            fullDuplexUDPSocket.send(sendPacket);
                        }
                } catch (IOException e) {
                    System.out.println("Something has gone wrong sending the packet to the peers, continuing");
                }
            }
        }
    }
}
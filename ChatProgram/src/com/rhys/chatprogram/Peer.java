package com.rhys.chatprogram;

import java.net.InetAddress;

/**
 * Created by Rhys Gevaux on 21/05/2015.
 * @author Rhys Gevaux 9508422
 *
 * Peer object to store IP, port, ID and name of each peer
 *
 * References:
 * https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 * http://www.cs.uic.edu/~troy/spring05/cs450/sockets/WebServer.java
 * http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 * http://www.cse.dmu.ac.uk/~bb/Teaching/NetWorks/Laboratory/UDPclientServer/UDPchat.java
 */
class Peer {
    private static int peerCount = 0; // stores number of peers in chat
    private final InetAddress peerIP; // stores IP of peer
    private final int peerPort; // stores port of peer
    private final int peerId; // stores ID of peer
    private final String peerName; // stores name of peer

    /**
     * constructs peer object
     * @param peerName peer name
     * @param peerIP peer IP
     * @param peerPort peer port
     */
    public Peer(String peerName, InetAddress peerIP, int peerPort) {
        this.peerName = peerName; // sets peer name
        this.peerIP = peerIP; // sets peer IP
        this.peerPort = peerPort; // sets peer port
        this.peerId = peerCount++; // increments peer count and sets it to peer ID
    }

    /**
     * gets peer IP
     * @return peer IP
     */
    public InetAddress getPeerIP() {
        return peerIP;
    }

    /**
     * gets peer port
     * @return peer port
     */
    public int getPeerPort() {
        return peerPort;
    }

    /**
     * converts peer ojbect into a readable string
     * contains ID, name, IP and port
     * @return string of peer
     */
    @Override
    public String toString() {
        return "Peer " + peerId + " " + peerName + "\t" + peerIP + ":" + peerPort + ">";
    }
}
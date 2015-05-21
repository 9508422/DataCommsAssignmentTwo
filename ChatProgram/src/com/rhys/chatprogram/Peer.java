package com.rhys.chatprogram;

import java.net.InetAddress;

/**
 * Created by Rhys on 21/05/2015.
 */
class Peer {
    private static int peerCount = 0;
    private final InetAddress peerIP;
    private final int peerPort;
    private final int peerId;
    private final String peerName;

    public Peer(String peerName, InetAddress peerIP, int peerPort) {
        this.peerName = peerName;
        this.peerIP = peerIP;
        this.peerPort = peerPort;

        this.peerId = peerCount++;
    }

    public InetAddress getPeerIP() {
        return peerIP;
    }

    public int getPeerPort() {
        return peerPort;
    }

    @Override
    public String toString() {
        return "Peer " + peerId + ", " + peerName + " \t<InetAddress = " + peerIP + ",  Port = " + peerPort + ">";
    }
}
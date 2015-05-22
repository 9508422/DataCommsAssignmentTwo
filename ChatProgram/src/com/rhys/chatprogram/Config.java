package com.rhys.chatprogram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rhys Gevaux on 21/05/2015.
 * @author Rhys Gevaux 9508422
 *
 * Config object used to set ports and valid peers
 *
 * References:
 * https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 * http://www.cs.uic.edu/~troy/spring05/cs450/sockets/WebServer.java
 * http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 * http://www.cse.dmu.ac.uk/~bb/Teaching/NetWorks/Laboratory/UDPclientServer/UDPchat.java
 */
class Config {
    /**
     * constructs config object
     */
    private Config() {
    }

    /**
     * loads config file
     * @param fileName config file name
     * @return valid peers
     * @throws IOException
     */
    public static HashMap<InetAddress, HashMap<Integer, Peer>> load(String fileName) throws IOException {
        // hashpmap to store valid peers from config
        HashMap<InetAddress, HashMap<Integer, Peer>> validPeers = new HashMap<>();

        // BufferedReader to read config file
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        // arraylist to store the lines of the config files
        ArrayList<String> fileLines = new ArrayList<>();

        // read in file to arrayList
        String lineRead = br.readLine();
        while (lineRead != null) {
            fileLines.add(lineRead);
            lineRead = br.readLine();
        }

        // for each line in the arrayList
        for (String line : fileLines) {
            String[] t = line.split(" "); // split line into parts

            String user = t[0]; // set peer name
            InetAddress inet = InetAddress.getByName(t[1]); // set peer IP
            Integer port = Integer.parseInt(t[2]); // set peer port

            // check if inet isn't already loaded into valid peers
            if (!validPeers.containsKey(inet)) {
                validPeers.put(inet, new HashMap<>()); // add inet to valid peers
            }

            // map IP and port to peer
            HashMap<Integer, Peer> IPtoPortMapping = validPeers.get(inet);
            IPtoPortMapping.put(port, new Peer(user, inet, port));
        }

        return validPeers;
    }

}

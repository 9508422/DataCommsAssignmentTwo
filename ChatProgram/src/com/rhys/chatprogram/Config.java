package com.rhys.chatprogram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rhys on 21/05/2015.
 */
class Config {
    private Config() {
    }

    public static HashMap<InetAddress, HashMap<Integer, Peer>> load(String fileName) throws IOException {
        HashMap<InetAddress, HashMap<Integer, Peer>> validPeers = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        ArrayList<String> fileLines = new ArrayList<>();

        String lineRead;
        while ((lineRead = br.readLine()) != null) {
            fileLines.add(lineRead);
        }

        for (String line : fileLines) {
            String[] t = line.split(" ");

            String user = t[0];
            InetAddress inet = InetAddress.getByName(t[1]);
            Integer port = Integer.parseInt(t[2]);

            if (!validPeers.containsKey(inet)) {
                validPeers.put(inet, new HashMap<>());
            }

            HashMap<Integer, Peer> IPtoPortMapping = validPeers.get(inet);

            IPtoPortMapping.put(port, new Peer(user, inet, port));
        }

        return validPeers;
    }

}

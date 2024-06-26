package com.zsc;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;


public class Main {
    public static final String linkPassword = "123456";
    public static final int port = 3010;
    public static Set<String> friend = new HashSet<>();
    public static void main(String[] args) throws IOException {

        InetAddress add = InetAddress.getByName("localhost");
        System.out.println(add.getHostAddress());


        ChatWindow chatWindow = new ChatWindow(friend,port);
        new PeerDiscovery(chatWindow,linkPassword,port,friend);
//        new Thread(new ClientSocketHandler(port,chatWindow,linkPassword)).start();
//        new Thread(new ServerSocketHandler(port,chatWindow,linkPassword)).start();

        new Thread(new TCPServer(port,chatWindow)).start();

    }
}
package com.zsc;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// P2P网络发现类
public class PeerDiscovery {
    // 实现局域网内对等方的发现
    private final ChatWindow chatWindow;
    private final String linkPassword;
    private final int port;
    Set<String> friends;
    public PeerDiscovery(ChatWindow chatWindow,String linkPassword,int port,Set<String> friends) throws SocketException, UnknownHostException {
        this.friends = friends;
        this.chatWindow = chatWindow;
        this.linkPassword = linkPassword;
        this.port = port;
        new Thread(new ServerSocketHandler(port,chatWindow,linkPassword)).start();

        new Thread(new ClientSocketHandler(port,chatWindow,linkPassword)).start();
        //new ClientSocketHandler(port,chatWindow,linkPassword);
    }
}

class ServerSocketHandler implements Runnable {
    private ServerSocket serverSocket;
    private final ChatWindow chatWindow;
    private final int port;
    private final String linkPassword;
    DatagramSocket ds;
    byte[] bytes = new byte[1024];
    DatagramPacket dp = new DatagramPacket(bytes,bytes.length);
    Set<String> friendHost = new HashSet<>();

    public ServerSocketHandler(int port, ChatWindow chatWindow,String linkPassword) throws SocketException {
        this.port = port;
        this.chatWindow = chatWindow;
        this.linkPassword = linkPassword;
        ds = new DatagramSocket(port);

        //this.run();
    }

    @Override
    public void run() {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            friendHost.clear();
            chatWindow.updateFriendList(new ArrayList<String>(friendHost) );
            System.out.println("每隔1分钟清空一次好友列表");
        };
        // 初始延迟为0，然后每隔1分钟清空一次好友列表
        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);

        System.out.println(3);
        // 监听连接请求，接受连接，处理通信
        while(true)
        {
            try {
                ds.receive(dp);
            } catch (IOException e) {
                System.out.println(2);
                e.printStackTrace();
            }
            if(new String(dp.getData(),0,dp.getLength()).equals(linkPassword))
            {
                String hostAddress = dp.getAddress().getHostAddress();
                if (!friendHost.contains(hostAddress))
                {
                    friendHost.add(dp.getAddress().getHostAddress());
                    chatWindow.updateFriendList(new ArrayList<String>(friendHost) );
                }
                else
                {

                }
            }
            System.out.println(dp.getAddress().getHostAddress());
            System.out.println(new String(dp.getData(),0,dp.getLength()));
        }
    }


}

// 发送连接请求
class ClientSocketHandler implements Runnable {
    private final ChatWindow chatWindow;
    private final int port;
    private final String linkPassword;
    DatagramSocket ds;
    InetAddress address;
    public ClientSocketHandler(int port,ChatWindow chatWindow,String linkPassword) throws SocketException, UnknownHostException {
        this.chatWindow = chatWindow;
        this.port = port;
        this.linkPassword = linkPassword;
        ds = new DatagramSocket();
        address = InetAddress.getByName("255.255.255.255");
        //this.run();
    }

    @Override
    public void run() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            byte[] bytes = linkPassword.getBytes();
            DatagramPacket dp = new DatagramPacket(bytes,bytes.length,address,port);
            try {
                ds.send(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(1);
        };
        // 初始延迟为0，然后每隔5秒执行一次任务
        executor.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }
}

package com.zsc;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable{


    static final String fileNameMark = "606060";
    static final String fileDataMark = "060606";
    static  String fileName;
    private final ChatWindow chatWindow;
    int port;
    File file = null;

    public TCPServer(int port,ChatWindow chatWindow) {
        this.port = port;
        this.chatWindow = chatWindow;
    }
    @Override
    public void run() {

        while(true)
        {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(true)
            {
                Socket socket = null;
                try {
                    socket = ss.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream is = null;
                try {
                    is = socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                StringBuilder tmp = new StringBuilder();
                int b = 0;
                while (true)
                {
                    try {
                        if ((b = br.read()) == -1) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tmp.append((char)b);
                    System.out.print((char)b);
                }

                String ip = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();

                String directoryPath = ".\\" + ip;

                File directory = new File(directoryPath);

                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        System.out.println("多级文件目录创建成功");
                    } else {
                        System.out.println("多级文件目录创建失败");
                    }
                } else {
                    System.out.println("目录已存在");
                }

                System.out.println(directoryPath);
                if (tmp.substring(0,6).equals(fileNameMark))
                {
                    fileName = tmp.substring(6);
                    file = new File(directoryPath+"\\"+fileName);
                    while(file.exists())
                    {
                        fileName = "(1)" +  fileName;
                        file = new File(directoryPath+"\\" + fileName);
                    }
                }
                else if (tmp.substring(0,6).toString().equals(fileDataMark))
                {
                    if (file == null)
                    {
                        System.out.println("收到数据但未收到文件名");
                    }
                    else
                    {
                        try (FileWriter writer = new FileWriter(file)) {
                            writer.write(tmp.substring(6));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        chatWindow.newMessage(file.getAbsolutePath(),ip);
                    }
                }
                else
                {
                    System.out.println("收到格式错误的包");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println();
            }
        }

    }
}

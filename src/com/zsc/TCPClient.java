package com.zsc;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClient {

    Socket socket;
    OutputStream os;
    static final int fileNameType = 1;
    static final int fileDataType = 2;
    static final String fileNameMark = "606060";
    static final String fileDataMark = "060606";
    String aimIPAddress;
    int port;
    //MF = 1,表示为该数据的第一部分，MF = -1表示为该数据的最后一部分，MF = 0表示为该数据的中间部分
    void Send(int MF,String data) throws IOException {

        if (MF > 0)
        {
            socket = new Socket(aimIPAddress,port);
            System.out.println(1);
            os = socket.getOutputStream();
            System.out.println(2);
        }
        os.write(data.getBytes(StandardCharsets.UTF_8));
        if (MF < 0)
        {
            os.close();
            socket.close();
        }
    }

    public TCPClient(String filePath,String aimIPAddress,int port) throws IOException {
        this.aimIPAddress = new String(aimIPAddress);
        this.port = port;

        System.out.println(port);
        System.out.println(aimIPAddress);
        System.out.println(filePath);

        //发送文件名
        String[] a = filePath.split("\\\\");
        String fileName = a[a.length - 1];
        System.out.println(fileName);
        Send(1,fileNameMark);

        System.out.println(fileName);
        Send(-1,fileName);

        System.out.println(fileName);


        //发送文件内容
        Send(1,fileDataMark);
        File file = new File(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 发送每一行文本
                Send(0,line);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Send(-1,"");


        socket.close();
        System.out.println("成功发送");
    }
}

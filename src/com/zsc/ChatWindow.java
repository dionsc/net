package com.zsc;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

// 主窗口类
public class ChatWindow extends JFrame {
    public JTextArea sendTextArea;
    public JLabel messageLabel;
    public JList<String> friendsList;
    public DefaultListModel<String> friendsListModel;
    public JList<String> messageList;
    public DefaultListModel<String> messageListModel;
    public Set<String> friends;
    public int port;
    public String friend;

    public ChatWindow(Set<String> friend,int port) {

        this.port = port;
        this.friends = friend;

        // 设置窗口属性
        setTitle("基于P2P的局域网即时通信系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null); // 居中显示

        // 初始化组件
        initComponents();

        // 添加组件到内容面板
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // 使用边界布局


        JPanel panelMessage = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // 列位置
        gbc.gridy = 0; // 行位置
        gbc.fill = GridBagConstraints.BOTH; // 填充方式
        gbc.weightx = 1.0; // 横向权重
        gbc.weighty = 1.0; // 纵向权重

        panelMessage.add(new Label("Message edit box"),gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelMessage.add(new Label("Message prompt box"),gbc);

        gbc.weightx = 10.0; // 横向权重
        gbc.weighty = 10.0; // 纵向权重

        gbc.gridx = 0; // 列位置
        gbc.gridy = 1; // 行位置
        panelMessage.add(sendTextArea, gbc);
        gbc.gridx = 1; // 列位置
        gbc.gridy = 1; // 行位置
        panelMessage.add(new JScrollPane(messageList),gbc);
        panelMessage.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));


        panel.add(panelMessage, BorderLayout.NORTH); // 消息区域在顶部


        panel.add(new JScrollPane(friendsList), BorderLayout.CENTER); // 字符串列表在中心，并使用滚动面板
        panel.add(messageLabel, BorderLayout.EAST); // 消息提示框在底部

        // 添加按钮到窗口
        JButton buttonSendMessage = new JButton("发送消息");
        buttonSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 假设点击按钮时，将文本框的内容添加到列表中
                String text = sendTextArea.getText();
                if (!text.isEmpty()) {
                    //friendsListModel.addElement(text);
                    try {
                        sendMessage(text);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    sendTextArea.setText(""); // 清空文本框
                }
            }
        });

        JButton buttonSendFile = new JButton("选择文件并发送");
        buttonSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 假设点击按钮时
                messageLabel.setText("1111");
                try {
                    sendFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 创建一个面板来放置按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonSendMessage);
        buttonPanel.add(buttonSendFile);
        panel.add(buttonPanel, BorderLayout.SOUTH); // 按钮面板在右侧

        // 将面板添加到窗口中
        getContentPane().add(panel);

        // 显示窗口
        setVisible(true);
    }

    private void initComponents() {
        // 初始化文本编辑框
        sendTextArea = new JTextArea(10,30);
        sendTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // 初始化消息提示框
        messageLabel = new JLabel("This is a message label.");

        // 初始化好友字符串列表
        friendsListModel = new DefaultListModel<>();
        friendsList = new JList<>(friendsListModel);


        // 初始化消息字列表
        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);

        // 为好友列表添加选择监听器
        friendsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // 确保选择已稳定
                    int selectedIndex = friendsList.getSelectedIndex();
                    if (selectedIndex != -1) { // 确保有选中的项目
                        // 这里处理选中的值，例如打印到控制台

                        friend = friendsListModel.get(selectedIndex);

                        System.out.println("Selected friend: " + selectedIndex);
                    }
                }
            }
        });

        // 为消息列表添加选择监听器
        messageList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // 确保选择已稳定
                    int selectedIndex = messageList.getSelectedIndex();
                    if (selectedIndex != -1) { // 确保有选中的项目
                        // 这里处理选中的值，例如打印到控制台

                        String filePath = messageListModel.get(selectedIndex);
                        Path path = Paths.get(filePath);
                        try {
                            String content = Files.readString(path);
                            sendTextArea.setText(content);
                            System.out.println(content);
                        } catch (IOException ee) {
                            ee.printStackTrace();
                        }
                        System.out.println(filePath);
                        if(filePath.endsWith(".p2pMessageTmp"))
                        {
                            messageLabel.setText("这是消息类型,已显示在编辑框内");
                        }
                        else
                        {
                            messageLabel.setText("这是文件类型,请在文件夹中用外部软件打开");
                        }

                        System.out.println("Selected message: " + selectedIndex);
                    }
                }
            }
        });
    }


    public void updateFriendList(List<String> friend)
    {
        friendsListModel.clear();
        for (int i = 0; i < friend.size(); i++) {
            friendsListModel.add(i,friend.get(i));
        }
    }

    public void newMessage(String filePath,String Ip)
    {
        messageListModel.addElement(filePath);
        messageLabel.setText(Ip + "给你发信息了" );
    }

    public void sendMessage(String text) throws IOException {
        String filePath = ".\\file.p2pMessageTmp";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(text); // 将字符串写入文件
            System.out.println("String has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new TCPClient(filePath,friend,port);
    }


    public void sendFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            //用户选择的文件路径
            String filePath = selectedFile.getAbsolutePath();
            //TCP发送文件
            new TCPClient(filePath,friend,port);

            System.out.println(filePath);
        }
    }
}

package com.example.lunaticlemon.tetris;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by lemon on 2017-06-11.
 */

public class Client implements Runnable {

    private String serverIP;
    private int port;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    static boolean isConnected = false;
    static boolean isReady_p1 = false;  // server
    static boolean isReady_p2 = false;  // client
    static boolean checkReady_p2 = false;  // client
    static boolean isEnd = false;
    static boolean allSet = false;
    static boolean done = false;
    static boolean isChange = false;
    static int[][] board = new int[20][10];


    public Client(String ip, int port_num)
    {
        serverIP = ip;
        port = port_num;
        isConnected = false;
        isReady_p1 = false;  // server
        isReady_p2 = false;  // client
        checkReady_p2 = false;  // client
        isEnd = false;
        allSet = false;
        done = false;
        isChange = false;
    }

    @Override
    public void run(){
        // TODO Auto-generated method stub
        try {
            //서버와 연결하는 소켓 생성..
            socket= new Socket(InetAddress.getByName(serverIP), port);


            //여기까지 왔다는 것을 예외가 발생하지 않았다는 것이므로 소켓 연결 성공..
            isConnected = true;

            Log.d("client","on");
            //서버와 메세지를 주고받을 통로 구축
            is = new DataInputStream(socket.getInputStream()); //클라이언트로 부터 메세지를 받기 위한 통로
            os = new DataOutputStream(socket.getOutputStream()); //클라이언트로 메세지를 보내기 위한 통로

            done = true;
            while (isConnected) {
                byte[] b = new byte[1000];
                int ac = is.read(b, 0, b.length);
                String input = new String(b, 0, b.length);

                if(input.length() > 0)
                    processMsg(input);

                if(ac==-1)
                    isConnected = false;
            }

            socket.close();

            Log.d("client","close");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("client","off");
            done = true;
            isConnected = false;
        }
    }

    private void processMsg(String msg)
    {
        Log.d("client","msg_in"+msg);
        if(msg.startsWith("ready"))
        {
            isReady_p1 = true;
            sendMsg("ack_ready");
        }
        else if(msg.startsWith("unready"))
        {
            isReady_p1 = false;
            sendMsg("ack_unready");
        }
        else if(msg.startsWith("ack_ready"))
        {
            checkReady_p2 = true;
        }
        else if(msg.startsWith("ack_unready"))
        {
            checkReady_p2 = false;
        }
        else if(msg.startsWith("allset"))
        {
            StringTokenizer st = new StringTokenizer(msg, ".");
            st.nextToken(); // allset
            enter_room_dialog.nick = st.nextToken();  // user_nick
            enter_room_dialog.rank = st.nextToken(); // user_rank
            allSet = true;
        }
        else if(msg.startsWith("close"))
        {
            isConnected = false;
        }
        else if(msg.startsWith("board"))
        {
            while(isChange)
            {}

            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 10; i++) {
                    board[j][i] = msg.charAt(5+10*j+i)-48;
                }
            }

            isChange = true;
        }
        else if(msg.startsWith("isEnd"))
        {
            isEnd = true;
        }
    }

    public void sendMsg(String msg)
    {
        try {
            byte[] b;
            b = msg.getBytes();
            Log.d("client", "msg_out" + msg);
            os.write(b);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("client", "msg transmit fail");
        }
    }

    public void closeClient()
    {
        try {
            if(isConnected == true) {
                isConnected = false;
                sendMsg("closed");
            }
            if(socket != null && socket.isBound() == true)
                socket.close();
        }catch (IOException e) {
            isConnected = false;
            e.printStackTrace();
            Log.d("client","off");
        }
    }
}

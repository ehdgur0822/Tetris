package com.example.lunaticlemon.tetris;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by lemon on 2017-06-11.
 */

public class Server implements Runnable {

    public int port;
    public boolean portSet;

    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    static boolean isConnected = false;
    static boolean isReady_p1 = false;  // server
    static boolean checkReady_p1 = false;  // sever
    static boolean isReady_p2 = false;  // client
    static boolean isEnd = false;
    static boolean allSet = false;
    static boolean isChange = false;
    static int[][] board = new int[20][10];

    public Server()
    {
        isConnected = false;
        isReady_p1 = false;  // server
        checkReady_p1 = false;  // sever
        isReady_p2 = false;  // client
        isEnd = false;
        allSet = false;
        isChange = false;
    }

    @Override
    public void run() {

        // TODO Auto-generated method stub
        portSet = false;
        while(!portSet) {
            port = new Random().nextInt(47000) + 2000;
            try {
                serverSocket = new ServerSocket(port);
                Log.d("server", "maked" + Integer.toString(port));
                portSet = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if(!new_room_dialog.isEnd) {
                //서버에 접속하는 클라이언트 소켓 얻어오기(클라이언트가 접속하면 클라이언트 소켓 리턴)
                socket = serverSocket.accept(); //서버는 클라이언트가 접속할 때까지 여기서 대기...

                Log.d("server", "connected");
                isConnected = true;
                //여기 까지 왔다는 것은 클라이언트가 접속했다는 것을 의미하므로
                //클라이언트와 데이터를 주고 받기 위한 통로구축..
                is = new DataInputStream(socket.getInputStream()); //클라이언트로 부터 메세지를 받기 위한 통로
                os = new DataOutputStream(socket.getOutputStream()); //클라이언트로 메세지를 보내기 위한 통로

                while (isConnected) {
                    byte[] b = new byte[1000];
                    int ac = is.read(b, 0, b.length);
                    String input = new String(b, 0, b.length);

                    if (input.length() > 0)
                        processMsg(input);

                    if (ac == -1)
                        isConnected = false;
                }

                serverSocket.close();
                socket.close();

                Log.d("server", "closed");
            }
        } catch (IOException e) {
            isConnected = false;
            closeServer();
            e.printStackTrace();
            Log.d("server","not maked");
        }
    }

    private void processMsg(String msg)
    {
        Log.d("server","msg_in"+msg);
        if(msg.startsWith("ready"))
        {
            isReady_p2 = true;
            sendMsg("ack_ready");
        }
        else if(msg.startsWith("unready"))
        {
            isReady_p2 = false;
            sendMsg("ack_unready");
        }
        else if(msg.startsWith("ack_ready"))
        {
            checkReady_p1 = true;
        }
        else if(msg.startsWith("ack_unready"))
        {
            checkReady_p1 = false;
        }
        else if(msg.startsWith("allset"))
        {
            StringTokenizer st = new StringTokenizer(msg, ".");
            st.nextToken(); // allset
            new_room_dialog.nick = st.nextToken();  // user_nick
            new_room_dialog.rank = st.nextToken(); // user_rank
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
            Log.d("server","msg_out"+msg);
            os.write(b);
        } catch (Exception e) {
            Log.d("server", "msg transmit fail" + e);
        }
    }

    public void closeServer()
    {
        try {
            if(isConnected == true) {
                isConnected = false;
                sendMsg("closed");
            }
            if(serverSocket.isBound() == true && serverSocket.isClosed() == false)
                serverSocket.close();
            if(socket != null && socket.isClosed() == false)
                socket.close();

            Log.d("server","close");
        }catch (IOException e) {
            isConnected = false;
            e.printStackTrace();
            Log.d("server","off");
        }
    }
}

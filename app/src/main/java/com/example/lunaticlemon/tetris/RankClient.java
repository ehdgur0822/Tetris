package com.example.lunaticlemon.tetris;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.lang.Thread.sleep;

/**
 * Created by lemon on 2017-06-23.
 */

public class RankClient implements Runnable{

    String ip = "115.71.233.23";
    int port = 5001;
    static boolean disConnect;
    static boolean reConnect;
    static boolean isConnected;
    boolean allSet;
    SocketChannel client = null;
    ByteBuffer buffer;
    private MessagePacker msg;
    String id;
    String nick;
    String cate;
    String room_name;
    String status;

    public RankClient(String _user_id, String _user_cate, String _status)
    {
        this.id = _user_id;
        this.cate = _user_cate;
        this.room_name = null;
        this.status = _status;
        disConnect = false;
        reConnect = false;
        isConnected = false;
        allSet = false;
    }

    public void connectServer()
    {
        try {
            InetSocketAddress hostAddress = new InetSocketAddress(ip, port);
            client = SocketChannel.open(hostAddress);
            buffer = ByteBuffer.allocate(1024);
            isConnected = true;
        }catch(IOException e)
        {
            Log.d("RankClient err",String.valueOf(e));
        }
    }

    public void disconnectServer()
    {
        try {
            if(client.isConnected()) {
                client.close();
                Log.d("RankClient", "close");
            }
            else
                Log.d("RankClient", "already close");
        }catch(IOException e)
        {
            Log.d("RankClient close err",String.valueOf(e));
        }
    }

    public boolean isConnect()
    {
        return client.isConnected();
    }

    public void run(){
        // TODO Auto-generated method stub
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(!isConnected)
        {
            connectServer();
        }

        if(!reConnect)
            sendReady();
        else {
            reConnect = false;
            RankView.user1_disconnect = false;
            sendReconnect();
        }

        if(!RankGameActivity.isWin)
            Log.d("RankClient isWin", "check");
        if(!RankGameActivity.isLose)
            Log.d("RankClient isLose", "check");
        if(!RankView.isEnd)
            Log.d("RankClient isEnd", "check");

        while(!RankGameActivity.isWin && !RankGameActivity.isLose && !RankView.isEnd)
        {
            if(RankGameActivity.isWin || RankGameActivity.isLose || RankView.isEnd)
                break;

            try {
                if (client.read(buffer) != -1) {
                    processData(buffer);
                    buffer.clear();
                }
            }catch(IOException e){
                break;
            }
        }

        disconnectServer();
    }

    public void sendReady()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.READY);
        msg.add(id);
        msg.add(cate);
        msg.add("single");
        msg.add(status);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendReconnect()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.RECONNECT);
        msg.add(room_name);
        msg.add(id);
        msg.add(cate);
        msg.add("single");
        msg.add(status);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
            Log.d("RankClient","send reconnect");
        }catch(IOException e) {
            e.printStackTrace();
            Log.d("RankClient","send reconnect err");
        }
    }

    public void sendDisconnect()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.DISCONNECT);
        msg.add(id);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendBoard()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.BOARD_CHANGE);
        msg.add(room_name);
        msg.add(id);
        msg.add("single");
        msg.add(IntArrayToString(RankView.t.getEntireBoard()));
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendObstacle()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.OBSTACLE);
        msg.add(room_name);
        msg.add(id);
        msg.add("single");
        msg.Finish();

        Log.d("RankClient","send obstacle");
        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendEnd()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.GAME_END);
        msg.add(room_name);
        msg.add(id);
        msg.add("single");
        msg.Finish();

        Log.d("RankClient","send end");
        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendACK(String _msg)
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.ACK);
        msg.add(room_name);
        msg.add(id);
        msg.add("single");
        msg.add(_msg);
        msg.Finish();

        Log.d("RankClient","sendACK" + _msg);
        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendEmoticon(String _type)
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.EMOTICON);
        msg.add(room_name);
        msg.add(id);
        msg.add("single");
        msg.add(_type);
        msg.Finish();

        Log.d("RankClient","sendEMOTICON" + _type);
        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void processData(ByteBuffer _buffer)
    {
        msg = new MessagePacker(_buffer.array());
        byte protocol = msg.getProtocol();

        switch (protocol) {
            case MessageProtocol.READY: {
                Log.d("getData","roomname");
                room_name = msg.getString();
                RankView.user2 = msg.getString();
                RankView.user2_rank = msg.getString();
                RankView.user2_id = msg.getString();
                RankView.user2_cate = msg.getString();
                RankGameActivity.rank_change_win = msg.getInt();
                RankGameActivity.rank_change_lose = msg.getInt();
                allSet = true;
                sendACK("READY");
                break;
            }

            case MessageProtocol.BOARD_CHANGE: {
                Log.d("getData","boardchange");
                String msg_board = msg.getString();
                if(msg_board.length() == 200) {
                    RankView.t.EnemyUpdate(StringToIntArray(msg_board));
                    RankView.isChange = true;
                }
                RankView.user2_disconnect = false;
                sendACK("BOARD_CHANGE");
                break;
            }

            case MessageProtocol.GAME_END: {
                Log.d("getData","gameend");
                String status = msg.getString();
                if(status.equals("win")) {
                    sendACK("GAME_END_WIN");
                    RankGameActivity.isWin = true;
                }
                else if(status.equals("lose")) {
                    sendACK("GAME_END_LOSE");
                    RankGameActivity.isLose = true;
                }
                else if(status.equals("ack"))
                {}
                break;
            }

            case MessageProtocol.RECONNECT:{
                Log.d("getData","reconnect");
                allSet = true;
                break;
            }

            case MessageProtocol.OBSTACLE:{
                Log.d("getData","obstacle");
                RankView.isObstacle = true;
                break;
            }

            case MessageProtocol.ACK:{
                Log.d("getData","ack");
                RankView.user2_disconnect = true;
                break;
            }

            case MessageProtocol.EMOTICON:{
                Log.d("getData","emoticon");
                String type = msg.getString();
                switch(type)
                {
                    case "good":
                        RankView.user2_good = true;
                        RankView.user2_angry = false;
                        RankView.user2_sad = false;
                        break;
                    case "angry":
                        RankView.user2_good = false;
                        RankView.user2_angry = true;
                        RankView.user2_sad = false;
                        break;
                    case "sad":
                        RankView.user2_good = false;
                        RankView.user2_angry = false;
                        RankView.user2_sad = true;
                        break;
                }
                break;
            }
        }
    }

    private String IntArrayToString(int[][] a)
    {
        StringBuilder sb=new StringBuilder();
        for(int j=0;j<a.length;j++)
        {
            for(int i=0;i<a[0].length;i++)
            {
                sb.append(a[j][i]);
            }
        }
        return sb.toString();
    }

    private int[][] StringToIntArray(String a)
    {
        int[][] board = new int[20][10];

        for(int j=0;j<board.length;j++)
        {
            for(int i=0;i<board[0].length;i++)
            {
                board[j][i] = Integer.parseInt(String.valueOf(a.charAt(j*board[0].length + i)));
            }
        }

        return board;
    }
}

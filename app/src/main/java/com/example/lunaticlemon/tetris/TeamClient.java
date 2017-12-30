package com.example.lunaticlemon.tetris;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by lemon on 2017-07-14.
 */

public class TeamClient implements Runnable{

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
    int team;

    public TeamClient(String _user_id, String _user_cate, String _status)
    {
        this.id = _user_id;
        this.cate = _user_cate;
        this.room_name = null;
        this.status = _status;
        TeamClient.disConnect = false;
        TeamClient.reConnect = false;
        TeamClient.isConnected = false;
        allSet = false;
    }

    public void connectServer()
    {
        try {
            InetSocketAddress hostAddress = new InetSocketAddress(ip, port);
            client = SocketChannel.open(hostAddress);
            buffer = ByteBuffer.allocate(1024);
            TeamClient.isConnected = true;
        }catch(IOException e)
        {
            Log.d("TeamClient err",String.valueOf(e));
        }
    }

    public void disconnectServer()
    {
        try {
            if(client.isConnected()) {
                client.close();
                Log.d("TeamClient", "close");
            }
            else
                Log.d("TeamClient", "already close");
        }catch(IOException e)
        {
            Log.d("TeamClient close err",String.valueOf(e));
        }
    }

    public boolean isConnect()
    {
        return client.isConnected();
    }

    public void run(){
        // TODO Auto-generated method stub
        while(!isConnected)
        {
            connectServer();
        }

        if(!TeamClient.reConnect)
            sendReady();
        else {
            reConnect = false;
            TeamView.user1_disconnect = false;
            sendReconnect();
        }

        while(!TeamGameActivity.isWin && !TeamGameActivity.isLose && !TeamView.isEnd)
        {
            if(TeamGameActivity.isWin || TeamGameActivity.isLose || TeamView.isEnd)
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
        msg.add("team");
        msg.add(status);
        msg.Finish();
        try {
            client.write(msg.getBuffer());
            msg = null;
        }catch(IOException e) {}
    }

    public void sendReconnect()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.RECONNECT);
        msg.add(room_name);
        msg.add(id);
        msg.add(cate);
        msg.add("team");
        msg.Finish();

        try {
            client.write(msg.getBuffer());
            Log.d("send","reconnect");
        }catch(IOException e) {
            e.printStackTrace();
            Log.d("send","reconnect err");
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
        msg.add("team");
        msg.add(IntArrayToString(TeamView.t.getEntireBoard()));
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    // TODO
    public void sendObstacle()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.OBSTACLE);
        msg.add(room_name);
        msg.add(id);
        msg.add("team");
        msg.Finish();

        Log.d("client","send obstacle");
        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    // TODO
    public void sendEnd()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(MessageProtocol.GAME_END);
        msg.add(room_name);
        msg.add(id);
        msg.add("team");
        msg.Finish();

        Log.d("client","send end");
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
        msg.add("team");
        msg.add(_msg);
        msg.Finish();

        Log.d("client","sendACK" + _msg);
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
        msg.add("team");
        msg.add(_type);
        msg.Finish();

        Log.d("client","sendEMOTICON" + _type);
        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    // TODO
    public void processData(ByteBuffer _buffer)
    {
        msg = new MessagePacker(_buffer.array());
        byte protocol = msg.getProtocol();

        switch (protocol) {
            case MessageProtocol.READY: {
                Log.d("getData","roomname");
                room_name = msg.getString();
                team = msg.getInt();
                switch(team)
                {
                    case 0:
                        TeamView.user2 = msg.getString();
                        TeamView.user2_rank = msg.getString();
                        TeamView.user2_id = msg.getString();
                        TeamView.user2_cate = msg.getString();
                        TeamView.user3 = msg.getString();
                        TeamView.user3_rank = msg.getString();
                        TeamView.user3_id = msg.getString();
                        TeamView.user3_cate = msg.getString();
                        TeamView.user4 = msg.getString();
                        TeamView.user4_rank = msg.getString();
                        TeamView.user4_id = msg.getString();
                        TeamView.user4_cate = msg.getString();
                        break;
                    case 1:
                        TeamView.user2 = msg.getString();
                        TeamView.user2_rank = msg.getString();
                        TeamView.user2_id = msg.getString();
                        TeamView.user2_cate = msg.getString();
                        TeamView.user3 = msg.getString();
                        TeamView.user3_rank = msg.getString();
                        TeamView.user3_id = msg.getString();
                        TeamView.user3_cate = msg.getString();
                        TeamView.user4 = msg.getString();
                        TeamView.user4_rank = msg.getString();
                        TeamView.user4_id = msg.getString();
                        TeamView.user4_cate = msg.getString();
                        break;
                    case 2:
                        TeamView.user3 = msg.getString();
                        TeamView.user3_rank = msg.getString();
                        TeamView.user3_id = msg.getString();
                        TeamView.user3_cate = msg.getString();
                        TeamView.user4 = msg.getString();
                        TeamView.user4_rank = msg.getString();
                        TeamView.user4_id = msg.getString();
                        TeamView.user4_cate = msg.getString();
                        TeamView.user2 = msg.getString();
                        TeamView.user2_rank = msg.getString();
                        TeamView.user2_id = msg.getString();
                        TeamView.user2_cate = msg.getString();
                        break;
                    case 3:
                        TeamView.user3 = msg.getString();
                        TeamView.user3_rank = msg.getString();
                        TeamView.user3_id = msg.getString();
                        TeamView.user3_cate = msg.getString();
                        TeamView.user4 = msg.getString();
                        TeamView.user4_rank = msg.getString();
                        TeamView.user4_id = msg.getString();
                        TeamView.user4_cate = msg.getString();
                        TeamView.user2 = msg.getString();
                        TeamView.user2_rank = msg.getString();
                        TeamView.user2_id = msg.getString();
                        TeamView.user2_cate = msg.getString();
                        break;
                }
                TeamGameActivity.rank_change_win = msg.getInt();
                TeamGameActivity.rank_change_lose = msg.getInt();
                allSet = true;
                sendACK("READY");
                break;
            }

            case MessageProtocol.BOARD_CHANGE: {
                Log.d("getData","boardchange");
                int get_team = msg.getInt();
                String msg_board = msg.getString();
                if(msg_board.length() == 200) {
                    switch(team)
                    {
                        case 0:
                            switch(get_team)
                            {
                                case 1:
                                    if(!TeamView.isDelete) {
                                        TeamView.t.TeamUpdate(StringToIntArray(msg_board));
                                        TeamView.isChange = true;
                                    }
                                    TeamView.user2_disconnect = false;
                                    break;
                                case 2:
                                    TeamView.t.Enemy1Update(StringToIntArray(msg_board));
                                    TeamView.isChange = true;
                                    TeamView.user3_disconnect = false;
                                    break;
                                case 3:
                                    TeamView.t.Enemy2Update(StringToIntArray(msg_board));
                                    TeamView.isChange = true;
                                    TeamView.user4_disconnect = false;
                                    break;
                            }
                            break;
                        case 1:
                            switch(get_team)
                            {
                                case 0:
                                    if(!TeamView.isDelete) {
                                        TeamView.t.TeamUpdate(StringToIntArray(msg_board));
                                        TeamView.isChange = true;
                                    }
                                    TeamView.user2_disconnect = false;
                                    break;
                                case 2:
                                    TeamView.t.Enemy1Update(StringToIntArray(msg_board));
                                    TeamView.isChange = true;
                                    TeamView.user3_disconnect = false;
                                    break;
                                case 3:
                                    TeamView.t.Enemy2Update(StringToIntArray(msg_board));
                                    TeamView.isChange = true;
                                    TeamView.user4_disconnect = false;
                                    break;
                            }
                            break;
                        case 2:
                            switch(get_team)
                            {
                                case 0:
                                    TeamView.t.Enemy1Update(StringToIntArray(msg_board));
                                    TeamView.isChange = true;
                                    TeamView.user3_disconnect = false;
                                    break;
                                case 1:
                                    TeamView.t.Enemy2Update(StringToIntArray(msg_board));
                                    TeamView.isChange = true;
                                    TeamView.user4_disconnect = false;
                                    break;
                                case 3:
                                    if(!TeamView.isDelete) {
                                        TeamView.t.TeamUpdate(StringToIntArray(msg_board));
                                        TeamView.isChange = true;
                                    }
                                    TeamView.user2_disconnect = false;
                                    break;
                            }
                            break;
                        case 3:
                            switch(get_team)
                            {
                                case 0:
                                    TeamView.t.Enemy1Update(StringToIntArray(msg_board));
                                    TeamView.isChange = true;
                                    TeamView.user3_disconnect = false;
                                    break;
                                case 1:
                                    TeamView.t.Enemy2Update(StringToIntArray(msg_board));
                                    TeamView.isChange = true;
                                    TeamView.user4_disconnect = false;
                                    break;
                                case 2:
                                    if(!TeamView.isDelete) {
                                        TeamView.t.TeamUpdate(StringToIntArray(msg_board));
                                        TeamView.isChange = true;
                                    }
                                    TeamView.user2_disconnect = false;
                                    break;
                            }
                            break;
                    }
                }
                sendACK("BOARD_CHANGE");
                break;
            }

            case MessageProtocol.GAME_END: {
                Log.d("getData","gameend");
                String status = msg.getString();
                if(status.equals("win")) {
                    sendACK("GAME_END_WIN");
                    TeamGameActivity.isWin = true;
                }
                else if(status.equals("lose")) {
                    sendACK("GAME_END_LOSE");
                    TeamGameActivity.isLose = true;
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
                TeamView.isObstacle = true;
                break;
            }

            case MessageProtocol.ACK:{
                int get_team = msg.getInt();
                if(get_team == 7) {
                    TeamView.isDeleteAck = true;
                    Log.d("getData","ack delete");
                }
                else {
                    Log.d("getData","ack disconnet");
                    switch (team) {
                        case 0:
                            switch (get_team) {
                                case 1:
                                    TeamView.user2_disconnect = true;
                                    break;
                                case 2:
                                    TeamView.user3_disconnect = true;
                                    break;
                                case 3:
                                    TeamView.user4_disconnect = true;
                                    break;
                            }
                            break;
                        case 1:
                            switch (get_team) {
                                case 0:
                                    TeamView.user2_disconnect = true;
                                    break;
                                case 2:
                                    TeamView.user3_disconnect = true;
                                    break;
                                case 3:
                                    TeamView.user4_disconnect = true;
                                    break;
                            }
                            break;
                        case 2:
                            switch (get_team) {
                                case 0:
                                    TeamView.user3_disconnect = true;
                                    break;
                                case 1:
                                    TeamView.user4_disconnect = true;
                                    break;
                                case 3:
                                    TeamView.user2_disconnect = true;
                                    break;
                            }
                            break;
                        case 3:
                            switch (get_team) {
                                case 0:
                                    TeamView.user3_disconnect = true;
                                    break;
                                case 1:
                                    TeamView.user4_disconnect = true;
                                    break;
                                case 2:
                                    TeamView.user2_disconnect = true;
                                    break;
                            }
                            break;
                    }
                }

                break;
            }

            case MessageProtocol.EMOTICON:{
                Log.d("getData","emoticon");
                int get_team = msg.getInt();
                String type = msg.getString();
                switch(team) {
                    case 0:
                        switch (get_team) {
                            case 1:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user2_good = true;
                                        TeamView.user2_angry = false;
                                        TeamView.user2_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user2_good = false;
                                        TeamView.user2_angry = true;
                                        TeamView.user2_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user2_good = false;
                                        TeamView.user2_angry = false;
                                        TeamView.user2_sad = true;
                                        break;
                                }
                                break;
                            case 2:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user3_good = true;
                                        TeamView.user3_angry = false;
                                        TeamView.user3_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user3_good = false;
                                        TeamView.user3_angry = true;
                                        TeamView.user3_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user3_good = false;
                                        TeamView.user3_angry = false;
                                        TeamView.user3_sad = true;
                                        break;
                                }
                                break;
                            case 3:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user4_good = true;
                                        TeamView.user4_angry = false;
                                        TeamView.user4_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user4_good = false;
                                        TeamView.user4_angry = true;
                                        TeamView.user4_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user4_good = false;
                                        TeamView.user4_angry = false;
                                        TeamView.user4_sad = true;
                                        break;
                                }
                                break;
                        }
                        break;
                    case 1:
                        switch (get_team) {
                            case 0:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user2_good = true;
                                        TeamView.user2_angry = false;
                                        TeamView.user2_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user2_good = false;
                                        TeamView.user2_angry = true;
                                        TeamView.user2_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user2_good = false;
                                        TeamView.user2_angry = false;
                                        TeamView.user2_sad = true;
                                        break;
                                }
                                break;
                            case 2:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user3_good = true;
                                        TeamView.user3_angry = false;
                                        TeamView.user3_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user3_good = false;
                                        TeamView.user3_angry = true;
                                        TeamView.user3_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user3_good = false;
                                        TeamView.user3_angry = false;
                                        TeamView.user3_sad = true;
                                        break;
                                }
                                break;
                            case 3:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user4_good = true;
                                        TeamView.user4_angry = false;
                                        TeamView.user4_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user4_good = false;
                                        TeamView.user4_angry = true;
                                        TeamView.user4_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user4_good = false;
                                        TeamView.user4_angry = false;
                                        TeamView.user4_sad = true;
                                        break;
                                }
                                break;
                        }
                        break;
                    case 2:
                        switch (get_team) {
                            case 0:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user3_good = true;
                                        TeamView.user3_angry = false;
                                        TeamView.user3_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user3_good = false;
                                        TeamView.user3_angry = true;
                                        TeamView.user3_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user3_good = false;
                                        TeamView.user3_angry = false;
                                        TeamView.user3_sad = true;
                                        break;
                                }
                                break;
                            case 1:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user4_good = true;
                                        TeamView.user4_angry = false;
                                        TeamView.user4_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user4_good = false;
                                        TeamView.user4_angry = true;
                                        TeamView.user4_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user4_good = false;
                                        TeamView.user4_angry = false;
                                        TeamView.user4_sad = true;
                                        break;
                                }
                                break;
                            case 3:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user2_good = true;
                                        TeamView.user2_angry = false;
                                        TeamView.user2_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user2_good = false;
                                        TeamView.user2_angry = true;
                                        TeamView.user2_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user2_good = false;
                                        TeamView.user2_angry = false;
                                        TeamView.user2_sad = true;
                                        break;
                                }
                                break;
                        }
                        break;
                    case 3:
                        switch (get_team) {
                            case 0:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user3_good = true;
                                        TeamView.user3_angry = false;
                                        TeamView.user3_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user3_good = false;
                                        TeamView.user3_angry = true;
                                        TeamView.user3_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user3_good = false;
                                        TeamView.user3_angry = false;
                                        TeamView.user3_sad = true;
                                        break;
                                }
                                break;
                            case 1:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user4_good = true;
                                        TeamView.user4_angry = false;
                                        TeamView.user4_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user4_good = false;
                                        TeamView.user4_angry = true;
                                        TeamView.user4_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user4_good = false;
                                        TeamView.user4_angry = false;
                                        TeamView.user4_sad = true;
                                        break;
                                }
                                break;
                            case 2:
                                switch(type)
                                {
                                    case "good":
                                        TeamView.user2_good = true;
                                        TeamView.user2_angry = false;
                                        TeamView.user2_sad = false;
                                        break;
                                    case "angry":
                                        TeamView.user2_good = false;
                                        TeamView.user2_angry = true;
                                        TeamView.user2_sad = false;
                                        break;
                                    case "sad":
                                        TeamView.user2_good = false;
                                        TeamView.user2_angry = false;
                                        TeamView.user2_sad = true;
                                        break;
                                }
                                break;
                        }
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


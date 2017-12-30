package com.example.lunaticlemon.tetris;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.example.lunaticlemon.tetris.Fragment_clan.adapter;

/**
 * Created by lemon on 2017-08-02.
 */

public class ClanClient implements Runnable{

    String ip = "115.71.233.23";
    int port = 5002;
    SocketChannel client = null;
    ByteBuffer buffer;
    private MessagePacker msg;
    Activity activity;
    boolean isConnected;
    String id;
    String cate;
    String clantag;
    String clanstat;

    boolean onFaced;

    public ClanClient(Activity _activity, String _user_id, String _user_cate, String _clantag, String _clanstat)
    {
        this.activity = _activity;
        this.id = _user_id;
        this.cate = _user_cate;
        this.clantag = _clantag;
        this.clanstat = _clanstat;
        this.isConnected = false;
        this.onFaced = true;
    }

    public void connectServer()
    {
        try {
            InetSocketAddress hostAddress = new InetSocketAddress(ip, port);
            client = SocketChannel.open(hostAddress);
            buffer = ByteBuffer.allocate(1024);
            isConnected = true;
            Log.d("ChatClient", "connect");
        }catch(IOException e)
        {
            Log.d("ChatClient err",String.valueOf(e));
        }
    }

    public void disconnectServer()
    {
        try {
            if(client != null && client.isConnected()) {
                sendOut();
                client.close();
                Log.d("ChatClient", "close");
            }
            else
                Log.d("ChatClient", "already close");
        }catch(IOException e)
        {
            Log.d("ChatClient close err",String.valueOf(e));
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

        sendEnter();

        while(onFaced)
        {
            try {
                if (client.read(buffer) != -1) {
                    processData(buffer);
                    buffer.clear();
                }
            }catch(IOException e){
                break;
            }
        }

        Log.d("clanclient","disconnet");
        disconnectServer();
    }

    public void sendEnter()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(ChatMessageProtocol.ENTER);
        msg.add(this.id);
        msg.add(this.cate);
        msg.add(this.clantag);
        msg.add(this.clanstat);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendMsg(String _chat, String _time)
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(ChatMessageProtocol.CHAT);
        msg.add(this.id);
        msg.add(this.clantag);
        msg.add(this.clanstat);
        msg.add(_chat);
        msg.add(_time);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendInvite(String _clantag, String _time)
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(ChatMessageProtocol.INVITE);
        msg.add(this.id);
        msg.add(_clantag);
        msg.add(_time);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendSign(String _clantag, String _nick, String _time, int _type, String currentTime)
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(ChatMessageProtocol.SIGN);
        msg.add(_clantag);
        msg.add(_nick);
        msg.add(_time);
        msg.add(_type);
        msg.add(currentTime);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendMatch(String _status, String _time)
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(ChatMessageProtocol.MATCH);
        msg.add(_status);
        msg.add(this.id);
        msg.add(this.clantag);
        msg.add(this.clanstat);
        msg.add(_time);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendSet(String _match, String _clantag, String _nick, String _time)
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(ChatMessageProtocol.SET);
        msg.add(_match);
        msg.add(_clantag);
        msg.add(_nick);
        msg.add(_time);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendOut()
    {
        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(ChatMessageProtocol.OUT);
        msg.add(this.id);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        }catch(IOException e) {}
    }

    public void sendNotify() {

        MessagePacker msg = new MessagePacker();

        msg.SetProtocol(ChatMessageProtocol.NOTIFY);
        msg.add(this.clantag);
        msg.Finish();

        try {
            client.write(msg.getBuffer());
        } catch (IOException e) {
        }
    }

    public void processData(ByteBuffer _buffer)
    {
        msg = new MessagePacker(_buffer.array());
        byte protocol = msg.getProtocol();

        switch (protocol) {
            case ChatMessageProtocol.CHAT: {
                // num. nick, msg, time
                final String num = msg.getString();
                final String nick = msg.getString();
                final String clanstat = msg.getString();
                final String chat = msg.getString();
                final String time = msg.getString();

                activity.runOnUiThread(new Runnable() {
                    @Override public void run() {
                        adapter.addItem(Integer.parseInt(num), chat, time, nick, clanstat);
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            }

            case ChatMessageProtocol.INVITE:{
                final String num = msg.getString();
                final String nick = msg.getString();
                final String time = msg.getString();

                activity.runOnUiThread(new Runnable() {
                    @Override public void run() {
                        adapter.addItem(Integer.parseInt(num), "signto", time, nick, "0");
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            }

            case ChatMessageProtocol.SIGN:{
                ((MenuNaviActivity) activity).onClanChange("");
                break;
            }

            case ChatMessageProtocol.SET:{
                final String status = msg.getString();
                switch(Fragment_clan.isMatched)
                {
                    case 1:
                        ((MenuNaviActivity) activity).startSingle(status);
                        break;
                    case 2:
                        ((MenuNaviActivity) activity).startTeam(status);
                        break;
                }
                Fragment_clan.isMatched = 0;
                break;
            }
        }
    }
}

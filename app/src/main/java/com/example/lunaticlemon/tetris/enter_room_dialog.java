package com.example.lunaticlemon.tetris;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

import static java.lang.Thread.sleep;

/**
 * Created by lemon on 2017-06-11.
 */

public class enter_room_dialog extends Dialog {

    public Handler handler;
    boolean isReady_p2 = false;
    boolean isMatched = false;
    Context con;

    static String nick, rank;

    static Client client;
    static Thread ClientThread;
    Thread ClientMsgThread;
    String Tag;
    String ServerIP;
    int ServerPort;
    TextView textIP, textPlayer1, textP1Ready, textP2Ready, textStatus;
    Button btn_ready;
    Button btn_cancel;
    int left_time=3;

    public enter_room_dialog(@NonNull Context context, String tag) {
        super(context);

        Log.d("enter room", tag);
        Tag = tag;
        convertTag(Tag);
        con = context;
        this.setCanceledOnTouchOutside(false); // 다이알로그 바깥영역 터치시, 다이알로그 닫히지 않기
        this.setCancelable(true); // 백키로 다이알로그 닫기
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enter_room_dialog);

        client = null;
        ClientThread = null;
        client = new Client(ServerIP, ServerPort);
        ClientThread = new Thread(client);
        ClientMsgThread = new Thread(new ClientSendMsg());

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if(client.isReady_p1 == false)
                {
                    textP1Ready.setText("Waiting..");
                }
                else
                {
                    textP1Ready.setText("Ready!");
                }

                if(isReady_p2 == false)
                {
                    textP2Ready.setText("Waiting..");
                }
                else
                {
                    textP2Ready.setText("Ready!");
                }

                if(client.isConnected == true) {
                    btn_ready.setEnabled(true);
                    btn_ready.setFocusable(true);
                    textPlayer1.setText("Connected");
                }
                else {
                    btn_ready.setEnabled(false);
                    btn_ready.setFocusable(false);
                    textPlayer1.setText("Waiting..");
                }

                if(client.isReady_p1 == true && isReady_p2 == true && left_time != 0 && client.allSet == true)
                {
                    textStatus.setText(Integer.toString(left_time)+"초 후 게임이 시작됩니다..");
                    btn_ready.setEnabled(false);
                    btn_ready.setFocusable(false);
                    btn_cancel.setEnabled(false);
                    btn_cancel.setFocusable(false);
                    left_time--;
                    this.sendEmptyMessageDelayed(0, 1000);
                }
                else if(client.isReady_p1 == true && isReady_p2 == true && left_time == 0 && client.allSet == true)
                {
                    isMatched = true;
                    textStatus.setText(Integer.toString(left_time)+"초 후 게임이 시작됩니다..");
                    btn_ready.setEnabled(false);
                    btn_ready.setFocusable(false);
                    btn_cancel.setEnabled(false);
                    btn_cancel.setFocusable(false);
                    Intent intent = new Intent(con, MultiGameActivity.class);
                    intent.putExtra("Where","Client");
                    intent.putExtra("Nick",nick);
                    intent.putExtra("Rank",rank);
                    con.startActivity(intent);
                    dismiss();
                }
                else
                {
                    textStatus.setText("Waiting Player..");
                    this.sendEmptyMessageDelayed(0, 10);
                }

            }
        };

        View.OnClickListener ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_cancel:
                        dismiss();
                        break;
                    case R.id.button_ready:
                        if (isReady_p2 == false) {
                            isReady_p2 = true;
                            client.isReady_p2 = true;
                        } else {
                            isReady_p2 = false;
                            client.isReady_p2 = false;
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        textIP = (TextView) findViewById(R.id.textViewIP);
        textIP.setText(ServerIP+"."+Integer.toString(ServerPort));
        textPlayer1 = (TextView) findViewById(R.id.textPlayer1);
        textP1Ready = (TextView) findViewById(R.id.textViewP1Ready);
        textP2Ready = (TextView) findViewById(R.id.textViewP2Ready);
        textStatus = (TextView) findViewById(R.id.textViewStatus);
        btn_ready = (Button) findViewById(R.id.button_ready);
        btn_ready.setOnClickListener(ClickListener);
        btn_cancel = (Button) findViewById(R.id.button_cancel);
        btn_cancel.setOnClickListener(ClickListener);

        ClientThread.start();
    }

    @Override
    public void show() {
        super.show();

        while(!client.done)
        {}

        if(client.isConnected == false)
        {
            search_room_dialog dialog = new search_room_dialog(con);
            dialog.setTitle("Room");
            dialog.show();
            Toast toast = Toast.makeText(con, "올바른 tag가 아닙니다."+ServerIP, Toast.LENGTH_SHORT);
            toast.show();
            this.dismiss();
        }
        else
        {
            handler.sendEmptyMessage(0);
            ClientMsgThread.start();
        }
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
        if(!isMatched)
            client.closeClient();
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
    }

    public void convertTag(String tag)
    {
        String res_ip = "";

        StringTokenizer st = new StringTokenizer(tag, ".");
        for(int i=0;i<5;i++)
        {
            if(i < 3)
                res_ip += st.nextToken() + ".";
            else if(i == 3)
                res_ip += st.nextToken();
            else
                ServerPort = Integer.parseInt(st.nextToken());
        }

        ServerIP = res_ip;

        Log.d("ip",ServerIP);
        Log.d("port",Integer.toString(ServerPort));
    }

    public class ClientSendMsg implements Runnable
    {
        public void run()
        {
            while(client.isConnected)
            {
                if(client.isReady_p2 != client.checkReady_p2)
                {
                    if(client.isReady_p2 == true) {
                        Log.d("client","ready");
                        client.sendMsg("ready");
                    }
                    else
                        client.sendMsg("unready");

                    try{
                        sleep(500);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                else {
                    if (client.isReady_p1 == true && client.isReady_p2 == true) {
                        client.sendMsg("allset" + "." + RoomActivity.user_nick + "." + RoomActivity.user_rank);
                        break;
                    }
                }
            }
        }
    }
}
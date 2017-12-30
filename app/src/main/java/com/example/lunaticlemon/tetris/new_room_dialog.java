package com.example.lunaticlemon.tetris;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import static java.lang.Thread.sleep;

/**
 * Created by lemon on 2017-06-10.
 */

public class new_room_dialog extends Dialog {

    public Handler handler;
    static boolean isEnd = false;
    boolean isReady_p1 = false;
    boolean isMatched = false;

    static String nick, rank;

    Context con;
    static Server server;
    static Thread ServerThread;
    Thread ServerMsgThread;
    TextView textIP, textPlayer2, textP1Ready, textP2Ready, textStatus;
    Button btn_ready;
    Button btn_cancel;
    ImageView imageInvite;
    int left_time=3;
    String RoomTag;

    public new_room_dialog(@NonNull Context context) {
        super(context);

        con = context;
        isEnd = false;
        isReady_p1 = false;
        isMatched = false;
        server = null;
        ServerThread = null;

        this.setCanceledOnTouchOutside(false); // 다이알로그 바깥영역 터치시, 다이알로그 닫히지 않기
        this.setCancelable(true); // 백키로 다이알로그 닫기
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_room_dialog);


        server = new Server();
        ServerThread = new Thread(server);
        ServerMsgThread = new Thread(new ServerSendMsg());

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if(isReady_p1 == false)
                {
                    textP1Ready.setText("Waiting..");
                }
                else
                {
                    textP1Ready.setText("Ready!");
                }

                if(server.isReady_p2 == false)
                {
                    textP2Ready.setText("Waiting..");
                }
                else
                {
                    textP2Ready.setText("Ready!");
                }

                if(server.isConnected == true) {
                    btn_ready.setEnabled(true);
                    btn_ready.setFocusable(true);
                    textPlayer2.setText("Connected");
                }
                else {
                    btn_ready.setEnabled(false);
                    btn_ready.setFocusable(false);
                    textPlayer2.setText("Waiting..");
                }

                if(isReady_p1 == true && server.isReady_p2 == true && left_time != 0 && server.allSet == true)
                {
                    textStatus.setText(Integer.toString(left_time)+"초 후 게임이 시작됩니다..");
                    btn_ready.setEnabled(false);
                    btn_ready.setFocusable(false);
                    btn_cancel.setEnabled(false);
                    btn_cancel.setFocusable(false);
                    left_time--;
                    this.sendEmptyMessageDelayed(0, 1000);
                }
                else if(isReady_p1 == true && server.isReady_p2 == true && left_time == 0 && server.allSet == true)
                {
                    isMatched = true;
                    textStatus.setText(Integer.toString(left_time)+"초 후 게임이 시작됩니다..");
                    btn_ready.setEnabled(false);
                    btn_ready.setFocusable(false);
                    btn_cancel.setEnabled(false);
                    btn_cancel.setFocusable(false);
                    Intent intent = new Intent(con, MultiGameActivity.class);
                    intent.putExtra("Where","Server");
                    intent.putExtra("Nick",nick);
                    intent.putExtra("Rank",rank);
                    con.startActivity(intent);
                    dismiss();
                }
                else if(isEnd == false)
                {
                    textStatus.setText("Waiting Player..");
                    this.sendEmptyMessageDelayed(0, 10);
                }

            }
        };

        View.OnClickListener ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.button_cancel:
                            dismiss();
                        break;
                    case R.id.button_ready:
                        if(isReady_p1 == false)
                        {
                            isReady_p1 = true;
                            server.isReady_p1 = true;
                        }
                        else
                        {
                            isReady_p1 = false;
                            server.isReady_p1 = false;
                        }
                        break;
                    case R.id.imageInvite:
                        try{
                            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(con);
                            final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

                            kakaoBuilder.addText("RoomTag is : " + RoomTag);
                            //kakaoBuilder.addAppButton("앱 실행");

                            kakaoLink.sendMessage(kakaoBuilder, con);
                        }catch(KakaoParameterException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        textIP = (TextView) findViewById(R.id.textViewIP);
        textPlayer2 = (TextView) findViewById(R.id.textPlayer2);
        textP1Ready = (TextView) findViewById(R.id.textViewP1Ready);
        textP2Ready = (TextView) findViewById(R.id.textViewP2Ready);
        textStatus = (TextView) findViewById(R.id.textViewStatus);
        btn_ready = (Button) findViewById(R.id.button_ready);
        btn_ready.setOnClickListener(ClickListener);
        btn_cancel = (Button) findViewById(R.id.button_cancel);
        btn_cancel.setOnClickListener(ClickListener);
        imageInvite = (ImageView) findViewById(R.id.imageInvite);
        imageInvite.setOnClickListener(ClickListener);
    }

    @Override
    public void show() {
        super.show();

        if(checkAvailableConnection())
        {
            try{
                for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();)
                {
                    NetworkInterface intf = en.nextElement();
                    for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                    {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address){
                            Log.d("mobile",inetAddress.getHostAddress().toString());
                            ServerThread.start();
                            while(!server.portSet)
                            {}
                            RoomTag = inetAddress.getHostAddress().toString()+"."+Integer.toString(server.port);
                            textIP.setText(RoomTag);
                            handler.sendEmptyMessage(0);
                            ServerMsgThread.start();
                        }
                    }
                }
            }catch(SocketException e){
                e.printStackTrace();
            }
        }
        else
        {
            Toast toast = Toast.makeText(con, "인터넷에 연결되어있지 않습니다.", Toast.LENGTH_SHORT);
            toast.show();
            this.dismiss();
        }
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
        isEnd = true;

        handler.removeMessages(0);

        if(!isMatched)
            server.closeServer();

        if(ServerMsgThread.isAlive())
            Log.d("newroom","err");
        if(ServerThread.isAlive())
        {
            Log.d("newroom","err1");
        }

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public boolean checkAvailableConnection()
    {
        ConnectivityManager connMgr = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();

        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile
                return true;
            }
        }
        else {
            // not connected to the internet
            return false;
        }

        return false;
    }

    public class ServerSendMsg implements Runnable
    {
        public void run()
        {
            while(!server.isConnected)
            {
                if(isEnd == true)
                    break;
            }

            while(server.isConnected)
            {
                if(server.isReady_p1 != server.checkReady_p1)
                {
                    if(server.isReady_p1 == true) {
                        Log.d("server", "ready");
                        server.sendMsg("ready");
                    }
                    else
                    {
                        Log.d("server","unready");
                        server.sendMsg("unready");
                    }


                    try{
                        sleep(500);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                else {
                    if (server.isReady_p1 == true && server.isReady_p2 == true) {
                        server.sendMsg("allset" + "." + RoomActivity.user_nick + "." + RoomActivity.user_rank);
                        break;
                    }
                }
            }
        }
    }

    public String makeTag(String ip, int port)
    {
        String result = "";
        int temp;

        Log.d("ip",ip);
        StringTokenizer st = new StringTokenizer(ip, ".");
        while(st.hasMoreTokens()) {

            temp = Integer.parseInt(st.nextToken());
            Log.d("ip token",Integer.toString(temp));
            result += DecToHex(temp/16);
            result += DecToHex(temp%16);
        }

        result += Integer.toString(port);


        return result;
    }

    public String DecToHex(int num)
    {
        if(num <= 9)
            return Integer.toString(num);
        else
        {
            switch(num)
            {
                case 10:
                    return "A";
                case 11:
                    return "B";
                case 12:
                    return "C";
                case 13:
                    return "D";
                case 14:
                    return "E";
                case 15:
                    return "F";
            }
        }

        return "G";
    }
}

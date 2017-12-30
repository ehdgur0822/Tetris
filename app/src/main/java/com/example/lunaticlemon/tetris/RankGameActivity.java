package com.example.lunaticlemon.tetris;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class RankGameActivity extends AppCompatActivity {

    String user_id;
    String user_cate;
    String user_nick;
    String user_rank;
    String status;

    static int rank_change_win=0;
    static int rank_change_lose=0;

    rank_loading_dialog loading_dialog = null;
    static RankClient client;
    Thread clientThread = null;
    Thread SendThread = null;

    public Handler handler, left_handler, right_handler, down_handler;
    ImageView imageLogo;
    ImageView imageLeft;
    ImageView imageRight;
    ImageView imageDown;
    ImageView imageGood, imageSad, imageAngry;
    AnimationDrawable logo_anim;
    boolean onLeft = false;
    boolean onRight = false;
    boolean onDown = false;
    static boolean isWin = false;
    static boolean isLose = false;
    boolean isCancel = false;
    boolean isShow = false;
    boolean canEnd = false;
    boolean onTalk = false;
    boolean isGood = false;
    boolean isSad = false;
    boolean isAngry = false;

    Status_dialog status_dialog;
    MediaPlayer mp_bgm, mp_echo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_game);

        client = null;
        rank_change_win = 0;
        rank_change_lose = 0;
        RankGameActivity.isWin = false;
        RankGameActivity.isLose = false;

        Intent intent = getIntent();
        user_id = intent.getExtras().getString("id");
        user_cate = intent.getExtras().getString("cate");
        user_nick = intent.getExtras().getString("nick");;
        user_rank = intent.getExtras().getString("rank");
        status = intent.getExtras().getString("status");

        RankView.user1 = user_nick;
        RankView.user1_rank = user_rank;
        RankView.user1_id = user_id;
        RankView.user1_cate = user_cate;
        RankView.user2 = "searching..";
        RankView.user2_rank = "0";

        imageLeft = (ImageView)findViewById(R.id.imageLeft);
        imageRight = (ImageView)findViewById(R.id.imageRight);
        imageDown = (ImageView)findViewById(R.id.imageDown);
        imageLogo = (ImageView) findViewById(R.id.imageLogo);
        imageGood = (ImageView) findViewById(R.id.imageGood);
        imageSad = (ImageView) findViewById(R.id.imageSad);
        imageAngry = (ImageView) findViewById(R.id.imageAngry);
        imageGood.setVisibility(INVISIBLE);
        imageSad.setVisibility(INVISIBLE);
        imageAngry.setVisibility(INVISIBLE);
        logo_anim = (AnimationDrawable) imageLogo.getBackground();

        status_dialog = null;
        mp_bgm = MediaPlayer.create(this, R.raw.game);
        mp_bgm.setLooping(true);
        mp_echo = MediaPlayer.create(this, R.raw.echo);
        mp_echo.setLooping(false);

        imageLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!onLeft) {
                        left_handler.sendEmptyMessage(0);
                        onLeft = true;
                    }
                    imageLeft.setBackgroundResource(R.drawable.left_clicked);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    left_handler.removeMessages(0);
                    onLeft = false;
                    imageLeft.setBackgroundResource(R.drawable.left_unclicked);
                }
                return true;
            }
        });


        imageRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!onRight) {
                        right_handler.sendEmptyMessage(0);
                        onRight = true;
                    }
                    imageRight.setBackgroundResource(R.drawable.right_clicked);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    right_handler.removeMessages(0);
                    onRight = false;
                    imageRight.setBackgroundResource(R.drawable.right_unclicked);
                }
                return true;
            }
        });

        imageDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!onDown) {
                        down_handler.sendEmptyMessage(0);
                        onDown = true;
                    }
                    imageDown.setBackgroundResource(R.drawable.down_clicked);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    down_handler.removeMessages(0);
                    onDown = false;
                    imageDown.setBackgroundResource(R.drawable.down_unclicked);
                }
                return true;
            }
        });

        left_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                RankView.isLeft = true;
                this.sendEmptyMessageDelayed(0, 200);
            }
        };

        right_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                RankView.isRight = true;
                this.sendEmptyMessageDelayed(0, 200);
            }
        };

        down_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                RankView.isDown = true;
                this.sendEmptyMessageDelayed(0, 100);
            }
        };

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if(RankView.isEnd == true)
                {
                    if(!isShow)
                        show("lose");
                    this.removeMessages(0);
                }
                else if(RankGameActivity.isWin == true)
                {
                    if(!isShow)
                        show("win");
                    RankView.isEnd = true;
                    this.removeMessages(0);
                }
                else if(isCancel == true)
                {
                    RankView.isEnd = true;
                    this.sendEmptyMessageDelayed(0, 100);
                }
                else if(RankGameActivity.isLose == true)
                {
                    if(!isShow)
                        show("lose");
                    this.removeMessages(0);
                }
                else {
                    if(client.allSet && status_dialog == null)
                    {
                        status_dialog = new Status_dialog(RankGameActivity.this, "single");
                    }
                    this.sendEmptyMessageDelayed(0, 100);
                }
            }
        };

        handler.sendEmptyMessage(0);

        client = new RankClient(user_id, user_cate, status);

        loading_dialog = new rank_loading_dialog(this, "single"+status);
        loading_dialog.show();

        clientThread = new Thread(client);
        clientThread.start();

        SendThread = new Thread() {
            @Override
            public void run() {
                while(true)
                {
                    if(client.allSet == true && loading_dialog != null && loading_dialog.isShowing())
                    {
                        loading_dialog.dismiss();
                    }
                    if(client.disConnect == true)
                    {
                        client.sendDisconnect();
                        finish();
                        break;
                    }

                    if(RankView.isSet == true)
                    {
                        if(client != null && client.isConnect())
                            client.sendBoard();
                        RankView.isSet = false;
                    }

                    if(RankView.isDestroy == true)
                    {
                        if(client != null && client.isConnect())
                            client.sendObstacle();
                        RankView.isDestroy = false;
                    }

                    if(RankGameActivity.isWin)
                    {
                        break;
                    }

                    if(isCancel)
                    {
                        if(client.isConnect())
                            client.sendEnd();
                        break;
                    }

                    if(RankGameActivity.isLose)
                    {
                        RankView.isEnd = true;
                        break;
                    }

                    if(isGood)
                    {
                        if(client.isConnect())
                            client.sendEmoticon("good");
                        isGood = false;
                    }

                    if(isAngry)
                    {
                        if(client.isConnect())
                            client.sendEmoticon("angry");
                        isAngry = false;
                    }

                    if(isSad)
                    {
                        if(client.isConnect())
                            client.sendEmoticon("sad");
                        isSad = false;
                    }

                    if(RankView.isEnd == true && RankGameActivity.isWin == false)
                    {
                        if(client.isConnect())
                            client.sendEnd();
                        break;
                    }

                    if(RankView.isEnd == false && RankGameActivity.isWin == false && RankGameActivity.isLose == false)
                    {
                        if(client.allSet == true && !client.isConnect() && !clientThread.isAlive() && client.reConnect == false)
                        {
                            client.isConnected = client.isConnect();
                            RankView.user1_disconnect = true;
                            client.reConnect = true;
                            clientThread = new Thread(client);
                            clientThread.start();

                            Log.d("reconnect","wow");
                        }
                    }
                }
            }
        };

        SendThread.start();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        logo_anim.start();
        mp_bgm.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mp_bgm.pause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        logo_anim.stop();
        mp_bgm.stop();
        mp_bgm.release();
        mp_echo.release();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("cancel");
        builder.setCancelable(false);
        builder.setMessage("패배로 기록됩니다. 그만 두시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isCancel = true;
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    public void onClickRotate(View v)
    {
        RankView.isRotate = true;
    }

    public void onClickHold(View v)
    {
        RankView.isHold = true;
    }

    public void onClickTalk(View v)
    {
        if(onTalk) {
            onTalk = false;
            imageGood.setVisibility(INVISIBLE);
            imageSad.setVisibility(INVISIBLE);
            imageAngry.setVisibility(INVISIBLE);
        }
        else
        {
            onTalk = true;
            imageGood.setVisibility(VISIBLE);
            imageSad.setVisibility(VISIBLE);
            imageAngry.setVisibility(VISIBLE);
        }
    }

    public void onClickGood(View v)
    {
        onClickTalk(v);
        RankView.user1_good = true;
        RankView.user1_angry = false;
        RankView.user1_sad = false;
        isGood = true;
        isAngry = false;
        isSad = false;
    }

    public void onClickAngry(View v)
    {
        onClickTalk(v);
        RankView.user1_good = false;
        RankView.user1_angry = true;
        RankView.user1_sad = false;
        isGood = false;
        isAngry = true;
        isSad = false;
    }

    public void onClickSad(View v)
    {
        onClickTalk(v);
        RankView.user1_good = false;
        RankView.user1_angry = false;
        RankView.user1_sad = true;
        isGood = false;
        isAngry = false;
        isSad = true;
    }

    private void checkEnd()
    {
        try {
            if (loading_dialog != null && loading_dialog.isShowing()) {
                loading_dialog.dismiss();
            }

            if(SendThread.isAlive()) {
                Log.d("send", "alive");
                SendThread.interrupt();
            }

            if(client != null && client.isConnect()) {
                Log.d("Rank client", "connect");
                client.disconnectServer();
            }
            canEnd =  true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String status)
    {
        isShow = true;
        checkEnd();
        while(!canEnd)
        {}

        if(!status.equals("lose"))
        {
            status_dialog.set(status, rank_change_win);
            status_dialog.show();
        }
        else
        {
            if(Integer.parseInt(user_rank) < rank_change_lose)
                rank_change_lose = Integer.parseInt(user_rank);

            status_dialog.set(status, rank_change_lose);
            status_dialog.show();
        }
    }
}

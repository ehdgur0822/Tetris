package com.example.lunaticlemon.tetris;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class TeamGameActivity extends AppCompatActivity {

    String user_id;
    String user_cate;
    String user_nick;
    String user_rank;
    String status;

    static int rank_change_win=0;
    static int rank_change_lose=0;

    rank_loading_dialog loading_dialog = null;
    static TeamClient teamclient;
    Thread clientThread = null;
    Thread SendThread = null;

    public Handler handler, left_handler, right_handler, down_handler;
    ImageView imageLeft;
    ImageView imageRight;
    ImageView imageDown;
    ImageView imageGood, imageSad, imageAngry;

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
    static boolean isDeleteSend = false;
    Status_dialog status_dialog;

    MediaPlayer mp_bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_game);

        teamclient = null;
        TeamGameActivity.isWin = false;
        TeamGameActivity.isLose = false;
        isDeleteSend = false;

        user_id = getIntent().getExtras().getString("id");
        user_cate = getIntent().getExtras().getString("cate");
        user_nick = getIntent().getExtras().getString("nick");;
        user_rank = getIntent().getExtras().getString("rank");
        status = getIntent().getExtras().getString("status");

        TeamView.user1 = user_nick;
        TeamView.user1_rank = user_rank;
        TeamView.user1_id = user_id;
        TeamView.user1_cate = user_cate;

        imageLeft = (ImageView)findViewById(R.id.imageLeft);
        imageRight = (ImageView)findViewById(R.id.imageRight);
        imageDown = (ImageView)findViewById(R.id.imageDown);
        imageGood = (ImageView) findViewById(R.id.imageGood);
        imageSad = (ImageView) findViewById(R.id.imageSad);
        imageAngry = (ImageView) findViewById(R.id.imageAngry);
        imageGood.setVisibility(INVISIBLE);
        imageSad.setVisibility(INVISIBLE);
        imageAngry.setVisibility(INVISIBLE);

        status_dialog = null;
        mp_bgm = MediaPlayer.create(this, R.raw.game);
        mp_bgm.setLooping(true);

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

                TeamView.isLeft = true;
                this.sendEmptyMessageDelayed(0, 200);
            }
        };

        right_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                TeamView.isRight = true;
                this.sendEmptyMessageDelayed(0, 200);
            }
        };

        down_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                TeamView.isDown = true;
                this.sendEmptyMessageDelayed(0, 100);
            }
        };

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if(TeamView.isEnd == true)
                {
                    if(!isShow)
                        show("lose");
                    this.removeMessages(0);
                }
                else if(TeamGameActivity.isWin == true)
                {
                    if(!isShow)
                        show("win");
                    TeamView.isEnd = true;
                    this.removeMessages(0);
                }
                else if(isCancel == true)
                {
                    TeamView.isEnd = true;
                }
                else if(TeamGameActivity.isLose == true)
                {
                    if(!isShow)
                        show("lose");
                    this.removeMessages(0);
                }
                else {
                    if(teamclient.allSet && status_dialog == null)
                    {
                        status_dialog = new Status_dialog(TeamGameActivity.this, "team");
                    }
                    this.sendEmptyMessageDelayed(0, 100);
                }
            }
        };

        handler.sendEmptyMessage(0);

        teamclient = new TeamClient(user_id, user_cate, status);

        loading_dialog = new rank_loading_dialog(this, "team"+status);
        loading_dialog.show();

        clientThread = new Thread(teamclient);
        clientThread.start();

        SendThread = new Thread() {
            @Override
            public void run() {
                while(true)
                {
                    if(teamclient.allSet == true && loading_dialog != null && loading_dialog.isShowing())
                    {
                        loading_dialog.dismiss();
                    }
                    if(teamclient.disConnect == true)
                    {
                        teamclient.sendDisconnect();
                        finish();
                        break;
                    }

                    if(TeamView.isDelete && !isDeleteSend)
                    {
                        if(teamclient != null && teamclient.isConnect())
                        {
                            teamclient.sendACK("DELETE");
                            isDeleteSend = true;
                            Log.d("delete row","gg2");
                        }
                    }

                    if(!TeamView.isDelete && isDeleteSend)
                    {
                        isDeleteSend = false;
                    }

                    if(TeamView.isSet == true)
                    {
                        if(teamclient != null && teamclient.isConnect())
                            teamclient.sendBoard();
                        TeamView.isSet = false;
                    }

                    if(TeamView.isDestroy == true)
                    {
                        if(teamclient != null && teamclient.isConnect())
                            teamclient.sendObstacle();
                        TeamView.isDestroy = false;
                    }

                    if(TeamGameActivity.isWin)
                    {
                        break;
                    }

                    if(isCancel)
                    {
                        if(teamclient.isConnect())
                            teamclient.sendEnd();
                        break;
                    }

                    if(TeamGameActivity.isLose)
                    {
                        TeamView.isEnd = true;
                        break;
                    }

                    if(isGood)
                    {
                        if(teamclient.isConnect())
                            teamclient.sendEmoticon("good");
                        isGood = false;
                    }

                    if(isAngry)
                    {
                        if(teamclient.isConnect())
                            teamclient.sendEmoticon("angry");
                        isAngry = false;
                    }

                    if(isSad)
                    {
                        if(teamclient.isConnect())
                            teamclient.sendEmoticon("sad");
                        isSad = false;
                    }

                    if(TeamView.isEnd == true && TeamGameActivity.isWin == false)
                    {
                        if(teamclient.isConnect())
                            teamclient.sendEnd();
                        break;
                    }

                    if(TeamView.isEnd == false && TeamGameActivity.isWin == false && TeamGameActivity.isLose == false)
                    {
                        if(teamclient.allSet == true && !teamclient.isConnect() && !clientThread.isAlive() && teamclient.reConnect == false)
                        {
                            teamclient.isConnected = teamclient.isConnect();
                            TeamView.user1_disconnect = true;
                            teamclient.reConnect = true;
                            clientThread = new Thread(teamclient);
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
        mp_bgm.stop();
        mp_bgm.release();
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
                        show("lose");
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
        TeamView.isRotate = true;
    }

    public void onClickHold(View v)
    {
        TeamView.isHold = true;
    }

    public void onClickGood(View v)
    {
        onClickTalk(v);
        TeamView.user1_good = true;
        TeamView.user1_angry = false;
        TeamView.user1_sad = false;
        isGood = true;
        isAngry = false;
        isSad = false;
    }

    public void onClickAngry(View v)
    {
        onClickTalk(v);
        TeamView.user1_good = false;
        TeamView.user1_angry = true;
        TeamView.user1_sad = false;
        isGood = false;
        isAngry = true;
        isSad = false;
    }

    public void onClickSad(View v)
    {
        onClickTalk(v);
        TeamView.user1_good = false;
        TeamView.user1_angry = false;
        TeamView.user1_sad = true;
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

            if(teamclient != null && teamclient.isConnect()) {
                Log.d("Team client", "connect");
                teamclient.disconnectServer();
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

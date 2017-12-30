package com.example.lunaticlemon.tetris;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MultiGameActivity extends AppCompatActivity {

    public Handler left_handler, right_handler, down_handler, check_handler;
    Thread thread;
    String where;
    ImageView imageLeft;
    ImageView imageRight;
    ImageView imageDown;
    Client c = null;
    Server s = null;
    boolean onLeft = false;
    boolean onRight = false;
    boolean onDown = false;

    MediaPlayer mp_bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_game);

        Intent intent = getIntent();
        where = intent.getExtras().getString("Where");
        MultiView.user2 = intent.getExtras().getString("Nick");
        MultiView.user2_rank = intent.getExtras().getString("Rank");

        mp_bgm = MediaPlayer.create(this, R.raw.game);
        mp_bgm.setLooping(true);

        if(where.equals("Server"))
        {
            s = new_room_dialog.server;
            thread = new Thread() {
                @Override
                public void run() {
                    while(true)
                    {
                        if(MultiView.isEnd == true)
                        {
                            s.sendMsg("isEnd");
                            break;
                        }

                        if(s.isEnd == true)
                        {
                            break;
                        }

                        if(s.isChange == true)
                        {
                            MultiView.t.EnemyUpdate(s.board);
                            MultiView.isChange = true;
                            s.isChange = false;
                        }

                        if(MultiView.isSet == true)
                        {
                            MultiView.isSet = false;
                            s.sendMsg("board"+IntArrayToString(MultiView.t.getEntireBoard()));
                        }
                    }
                }
            };
        }
        else if(where.equals("Client"))
        {
            c = enter_room_dialog.client;
            thread = new Thread() {
                @Override
                public void run() {
                    while(true)
                    {
                        if(MultiView.isEnd == true)
                        {
                            c.sendMsg("isEnd");
                            break;
                        }

                        if(c.isEnd == true)
                        {
                            break;
                        }

                        if(c.isChange == true)
                        {
                            MultiView.t.EnemyUpdate(c.board);
                            MultiView.isChange = true;
                            c.isChange = false;
                        }

                        if(MultiView.isSet == true)
                        {
                            MultiView.isSet = false;
                            c.sendMsg("board"+IntArrayToString(MultiView.t.getEntireBoard()));
                        }
                    }
                }
            };
        }

        thread.start();

        imageLeft = (ImageView)findViewById(R.id.imageLeft);
        imageRight = (ImageView)findViewById(R.id.imageRight);
        imageDown = (ImageView)findViewById(R.id.imageDown);

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

                MultiView.isLeft = true;
                this.sendEmptyMessageDelayed(0, 200);
            }
        };

        right_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                MultiView.isRight = true;
                this.sendEmptyMessageDelayed(0, 200);
            }
        };

        down_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                MultiView.isDown = true;
                this.sendEmptyMessageDelayed(0, 100);
            }
        };

        check_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                if(MultiView.isEnd == true)
                {
                    check_handler.removeMessages(0);
                    ProcessLose();
                }
                else if(c != null && c.isEnd == true)
                {
                    check_handler.removeMessages(0);
                    MultiView.isEnd = true;
                    ProcessWin();
                }
                else if(s != null && s.isEnd == true)
                {
                    check_handler.removeMessages(0);
                    MultiView.isEnd = true;
                    ProcessWin();
                }
                else {
                    this.sendEmptyMessageDelayed(0, 100);
                }
            }
        };

        check_handler.sendEmptyMessage(0);
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

    public void onClickRotate(View v)
    {
        MultiView.isRotate = true;
    }

    public void onClickHold(View v)
    {
        MultiView.isHold = true;
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

    public void ProcessWin()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Win");
        builder.setCancelable(false);
        builder.setMessage("You Win");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    public void ProcessLose()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Lose");
        builder.setCancelable(false);
        builder.setMessage("You Lose");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
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
                        MultiView.isEnd = true;
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }
}

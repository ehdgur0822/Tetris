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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class SingleGameActivity extends AppCompatActivity {

    public Handler handler, left_handler, right_handler, down_handler;
    ImageView imageLogo;
    ImageView imageLeft;
    ImageView imageRight;
    ImageView imageDown;
    AnimationDrawable logo_anim;
    boolean onLeft = false;
    boolean onRight = false;
    boolean onDown = false;
    MediaPlayer mp_bgm, mp_echo;

    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_game);

        imageLeft = (ImageView)findViewById(R.id.imageLeft);
        imageRight = (ImageView)findViewById(R.id.imageRight);
        imageDown = (ImageView)findViewById(R.id.imageDown);
        imageLogo = (ImageView) findViewById(R.id.imageLogo);
        logo_anim = (AnimationDrawable) imageLogo.getBackground();

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

                SingleView.isLeft = true;
                this.sendEmptyMessageDelayed(0, 200);
            }
        };

        right_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                SingleView.isRight = true;
                this.sendEmptyMessageDelayed(0, 200);
            }
        };

        down_handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                SingleView.isDown = true;
                this.sendEmptyMessageDelayed(0, 100);
            }
        };

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if(SingleView.isEnd == true)
                {
                    show(SingleView.score);
                }
                else {
                    if(SingleView.isEcho)
                    {
                        mp_echo.start();
                        SingleView.isEcho = false;
                    }
                    this.sendEmptyMessageDelayed(0, 100);
                }
            }
        };

        handler.sendEmptyMessage(0);
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
        builder.setMessage("그만 두시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SingleView.isEnd = true;
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
        SingleView.isRotate = true;
    }

    public void onClickHold(View v)
    {
        SingleView.isHold = true;
    }

    public void show(int score)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Integer.toString(score)+"점을 달성했습니다!");
        builder.setCancelable(false);
        builder.setMessage("다시 하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SingleGameActivity.this.finish();
                        startActivity(new Intent(getApplication(),SingleGameActivity.class));
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SingleGameActivity.this.finish();
                    }
                });
        builder.show();
    }

}

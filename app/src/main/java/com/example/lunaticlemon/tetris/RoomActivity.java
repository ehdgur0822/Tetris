package com.example.lunaticlemon.tetris;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class RoomActivity extends AppCompatActivity {

    ImageView imageLogo;
    AnimationDrawable logo_anim;
    static String user_nick, user_rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Intent intent = getIntent();
        user_nick = intent.getExtras().getString("nick");
        user_rank = intent.getExtras().getString("rank");

        imageLogo = (ImageView) findViewById(R.id.imageLogo);
        logo_anim = (AnimationDrawable) imageLogo.getBackground();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        logo_anim.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        logo_anim.stop();
    }

    public void onClickMake(View v)
    {
        new_room_dialog make_dialog = new new_room_dialog(RoomActivity.this);
        make_dialog.setTitle("Room");
        make_dialog.show();
    }

    public void onClickSearch(View v)
    {
        search_room_dialog dialog = new search_room_dialog(RoomActivity.this);
        dialog.setTitle("Room");
        dialog.show();
    }
}

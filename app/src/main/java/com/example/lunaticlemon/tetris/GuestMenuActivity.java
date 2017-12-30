package com.example.lunaticlemon.tetris;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.lunaticlemon.tetris.Login.LoginActivity;


public class GuestMenuActivity extends AppCompatActivity {

    ImageView imageLogo;
    AnimationDrawable logo_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageLogo = (ImageView) findViewById(R.id.imageLogo);
        logo_anim = (AnimationDrawable) imageLogo.getBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.guestbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.login_btn:
                startActivity(new Intent(getApplication(),LoginActivity.class));
                finish();
                return true;
            case R.id.exit_btn:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("종료하시겠습니까?");
                builder1.setPositiveButton("예",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        finish();
                    }
                });

                builder1.setNegativeButton("아니오",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                    }
                });

                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("종료하시겠습니까?");
        builder.setPositiveButton("예",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton)
            {
                setResult(-1);
                finish();
            }
        });

        builder.setNegativeButton("아니오",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton)
            {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onClickSingle(View v)
    {
        startActivity(new Intent(getApplication(),SingleGameActivity.class));
    }

    public void onClickUser(View v)
    {
        final Intent intent = new Intent(this, RoomActivity.class);
        intent.putExtra("nick","guest");
        intent.putExtra("rank","0");
        startActivity(intent);
    }
}

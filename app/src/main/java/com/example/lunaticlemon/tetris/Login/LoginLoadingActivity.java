package com.example.lunaticlemon.tetris.Login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.lunaticlemon.tetris.R;

public class LoginLoadingActivity extends AppCompatActivity {

    ImageView imageView;
    Animation ani;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_loginloading);

        imageView = (ImageView) findViewById(R.id.imageView2);
        ani = AnimationUtils.loadAnimation(this, R.anim.loading);
        imageView.startAnimation(ani);

        Handler hd = new Handler();
        hd.postDelayed(new LoginHandler(), 3000);
    }

    private class LoginHandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(),LoginActivity.class));
            LoginLoadingActivity.this.finish();
        }
    }
}

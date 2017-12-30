package com.example.lunaticlemon.tetris;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lemon on 2017-08-11.
 */

public class user_setting_dialog extends Dialog {
    Context con;
    Button btn_give, btn_up, btn_down, btn_out;

    String id, nick, cate, clantag;
    int stat, mystat;

    Fragment_clan frag;

    public user_setting_dialog(@NonNull Context context, String _id, String _nick, String _cate, int _stat, String _clantag, int _mystat) {
        super(context);

        this.setCanceledOnTouchOutside(true); // 다이알로그 바깥영역 터치시, 다이알로그 닫기
        this.setCancelable(true); // 백키로 다이알로그 닫기

        con = context;
        id = _id;
        nick = _nick;
        cate = _cate;
        clantag = _clantag;
        stat = _stat;
        mystat = _mystat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_setting_dialog);



        btn_give = (Button) findViewById(R.id.btn_give);
        btn_give.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUserData("give", id, nick, cate, clantag);
                ((MenuNaviActivity) con).sendNotify();
            }
        });
        btn_up = (Button) findViewById(R.id.btn_up);
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUserData("up", id, nick, cate, clantag);
                ((MenuNaviActivity) con).sendNotify();
            }
        });

        btn_down = (Button) findViewById(R.id.btn_down);
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUserData("down", id, nick, cate, clantag);
                ((MenuNaviActivity) con).sendNotify();
            }
        });

        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUserData("out", id, nick, cate, clantag);
                ((MenuNaviActivity) con).sendNotify();
            }
        });

        switch(mystat)
        {
            case 1: // 부마스터
                btn_give.setVisibility(View.GONE);
                if(stat != 0) {
                    btn_up.setVisibility(View.GONE);
                    btn_out.setVisibility(View.GONE);
                }
                btn_down.setVisibility(View.GONE);
                break;
            case 2: // 마스터
                if(stat != 1)
                    btn_give.setVisibility(View.GONE);

                if(stat != 0) {
                    btn_up.setVisibility(View.GONE);
                    btn_out.setVisibility(View.GONE);
                }

                if(stat == 0 || stat == 2)
                    btn_down.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public void SetUserData(String _status, String _id, String _nick, String _cate, String _clantag){
        class SetUserJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String status = (String)params[0];
                String id = (String)params[1];
                String nick = (String)params[2];
                String cate = (String)params[3];
                String tag = (String)params[4];

                java.util.Date dt = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String currentTime = sdf.format(dt);

                String serverURL = "http://115.71.233.23/set_clanuser.php";
                String postParameters = "status=" + status + "&id=" + id + "&nick=" + nick + "&category=" + cate + "&tag=" + tag + "&time=" + currentTime;


                try {
                    URL url = new URL(serverURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    //httpURLConnection.setRequestProperty("content-type", "application/json");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = httpURLConnection.getResponseCode();

                    InputStream inputStream;
                    if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    }
                    else{
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result){
                dismiss();
            }
        }

        SetUserJSON g = new SetUserJSON();
        g.execute(_status, _id, _nick, _cate, _clantag);
    }
}

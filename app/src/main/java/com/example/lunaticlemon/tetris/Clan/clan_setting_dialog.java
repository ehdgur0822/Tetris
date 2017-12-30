package com.example.lunaticlemon.tetris.Clan;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.lunaticlemon.tetris.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lemon on 2017-07-28.
 */

public class clan_setting_dialog extends Dialog {

    Context con;

    ImageSwitcher sw;
    ImageView sw_left, sw_right, type_right, limit_left, limit_right;
    TextView textCurSwitch, textType, textLimit;
    Button btn_complete;

    String tag;
    int cur_switcher;
    int cur_type;
    int cur_limit;

    public clan_setting_dialog(@NonNull Context context, String _tag, int _mark, int _invite, int _rank_limit) {
        super(context);

        this.setCanceledOnTouchOutside(false); // 다이알로그 바깥영역 터치시, 다이알로그 닫히지 않기
        this.setCancelable(true); // 백키로 다이알로그 닫기

        con = context;

        tag = _tag;
        cur_switcher = _mark;
        cur_type = _invite;
        cur_limit = _rank_limit/100;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.clan_setting_dialog);



        sw = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(con);
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });
        sw.setImageResource(con.getResources().getIdentifier("c"+cur_switcher,"drawable",con.getPackageName()));

        sw_left = (ImageView) findViewById(R.id.imageSwitcherLeft);
        sw_right = (ImageView) findViewById(R.id.imageSwitcherRight);
        type_right = (ImageView) findViewById(R.id.imageTypeRight);
        limit_left = (ImageView) findViewById(R.id.imageLimitLeft);
        limit_right = (ImageView) findViewById(R.id.imageLimitRight);
        textCurSwitch = (TextView) findViewById(R.id.textCurSwitch);
        textType = (TextView) findViewById(R.id.textType);
        textLimit = (TextView) findViewById(R.id.textLimit);

        //init
        textCurSwitch.setText(Integer.toString(cur_switcher));
        textLimit.setText(Integer.toString(100*cur_limit));
        switch(cur_type)
        {
            case 0:
                textType.setText("임의로 가입");
                break;
            case 1:
                textType.setText("초대한정");
                break;
        }


        sw_left.setClickable(true);
        sw_right.setClickable(true);
        type_right.setClickable(true);
        limit_left.setClickable(true);
        limit_right.setClickable(true);

        sw_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(cur_switcher)
                {
                    case 1:
                        cur_switcher = 39;
                        break;
                    default:
                        cur_switcher--;
                        break;
                }

                sw.setImageResource(con.getResources().getIdentifier("c"+cur_switcher,"drawable",con.getPackageName()));
                textCurSwitch.setText(Integer.toString(cur_switcher));
            }
        });

        sw_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(cur_switcher)
                {
                    case 39:
                        cur_switcher = 1;
                        break;
                    default:
                        cur_switcher++;
                        break;
                }

                sw.setImageResource(con.getResources().getIdentifier("c"+cur_switcher,"drawable",con.getPackageName()));
                textCurSwitch.setText(Integer.toString(cur_switcher));
            }
        });

        type_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur_type++;
                cur_type %= 2;

                switch(cur_type)
                {
                    case 0:
                        textType.setText("임의로 가입");
                        break;
                    case 1:
                        textType.setText("초대한정");
                        break;
                }
            }
        });

        limit_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(cur_limit)
                {
                    case 0:
                        break;
                    default:
                        cur_limit--;
                        break;
                }

                textLimit.setText(Integer.toString(100*cur_limit));
            }
        });

        limit_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(cur_limit)
                {
                    case 30:
                        break;
                    default:
                        cur_limit++;
                        break;
                }

                textLimit.setText(Integer.toString(100*cur_limit));
            }
        });


        btn_complete = (Button) findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyClan task = new ModifyClan();
                task.execute(tag, String.valueOf(cur_switcher), String.valueOf(cur_type), String.valueOf(cur_limit * 100));
            }
        });
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

    class ModifyClan extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("clansetting","processing");

            if(result.equals("success"))
            {
                dismiss();
            }
            else
            {
                Toast toast = Toast.makeText(con, "수정 실패.", Toast.LENGTH_SHORT);
                toast.show();
            }

        }


        @Override
        protected String doInBackground(String... params) {

            String tag = (String)params[0];
            String mark = (String)params[1];
            String type = (String)params[2];
            String rank = (String)params[3];

            String serverURL = "http://115.71.233.23/modify_clan.php";
            String postParameters = "tag=" + tag + "&mark=" + mark + "&type=" + type + "&rank=" + rank;


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
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                return sb.toString();


            } catch (Exception e) {
                return new String("Error: " + e.getMessage());
            }
        }
    }
}

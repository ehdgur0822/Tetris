package com.example.lunaticlemon.tetris;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewClanActivity extends AppCompatActivity {

    ImageSwitcher sw;
    ImageView sw_left, sw_right, type_right, limit_left, limit_right;
    TextView textCurSwitch, textType, textLimit;
    EditText clanname;

    int cur_switcher;
    int cur_type;
    int cur_limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_clan);

        cur_switcher = 1;
        cur_type = 0;
        cur_limit = 0;

        sw = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });
        sw.setImageResource(R.drawable.c1);

        sw_left = (ImageView) findViewById(R.id.imageSwitcherLeft);
        sw_right = (ImageView) findViewById(R.id.imageSwitcherRight);
        type_right = (ImageView) findViewById(R.id.imageTypeRight);
        limit_left = (ImageView) findViewById(R.id.imageLimitLeft);
        limit_right = (ImageView) findViewById(R.id.imageLimitRight);
        textCurSwitch = (TextView) findViewById(R.id.textCurSwitch);
        textType = (TextView) findViewById(R.id.textType);
        textLimit = (TextView) findViewById(R.id.textLimit);
        clanname = (EditText) findViewById(R.id.editTextName);

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

                sw.setImageResource(getResources().getIdentifier("c"+cur_switcher,"drawable",NewClanActivity.this.getPackageName()));
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

                sw.setImageResource(getResources().getIdentifier("c"+cur_switcher,"drawable",NewClanActivity.this.getPackageName()));
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
                        textType.setText("초대 한정");
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
    }

    public void onClickMake(View v)
    {
        String name = clanname.getText().toString();

        if(name.length() < 2)
        {
            Toast toast = Toast.makeText(this, "클랜명은 2자이상 입력해야 합니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            MakeClan task = new MakeClan();
            task.execute(MenuNaviActivity.user_id, MenuNaviActivity.user_cate, name, Integer.toString(cur_switcher), Integer.toString(cur_type), Integer.toString(cur_limit*100));
        }

    }

    class MakeClan extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("makeclan",result);
            NewClanActivity.this.finish();
        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[0];
            String cate = (String)params[1];
            String name = (String)params[2];
            String mark = (String)params[3];
            String type = (String)params[4];
            String rank = (String)params[5];

            String serverURL = "http://115.71.233.23/make_clan.php";
            String postParameters = "id=" + id + "&category=" + cate + "&name=" + name + "&mark=" + mark + "&type=" + type + "&rank=" + rank;

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

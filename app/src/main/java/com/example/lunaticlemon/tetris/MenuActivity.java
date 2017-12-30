package com.example.lunaticlemon.tetris;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lunaticlemon.tetris.Login.LoginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import static com.example.lunaticlemon.tetris.R.drawable.bronze;
import static com.example.lunaticlemon.tetris.R.drawable.dia;
import static com.example.lunaticlemon.tetris.R.drawable.gold;
import static com.example.lunaticlemon.tetris.R.drawable.grand;
import static com.example.lunaticlemon.tetris.R.drawable.master;
import static com.example.lunaticlemon.tetris.R.drawable.platinum;
import static com.example.lunaticlemon.tetris.R.drawable.silver;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    ImageView imageLogo;
    AnimationDrawable logo_anim;

    static TextView textNick;
    TextView textRank, textLevel, textExp;
    ImageView imageTier;
    ProgressBar progressbar;
    private GoogleApiClient mGoogleApiClient;

    String user_id;
    String user_cate;
    String nick;
    String rank;
    String highrank;
    String exp;
    String win;
    String lose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        user_id = getIntent().getExtras().getString("id");
        user_cate = getIntent().getExtras().getString("cate");

        imageLogo = (ImageView) findViewById(R.id.imageLogo);
        logo_anim = (AnimationDrawable) imageLogo.getBackground();

        textNick = (TextView) findViewById(R.id.textNick);
        textRank = (TextView) findViewById(R.id.textRank);
        textLevel = (TextView) findViewById(R.id.textLevel);
        textExp = (TextView) findViewById(R.id.textExp);
        imageTier = (ImageView) findViewById(R.id.imageTier);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        logo_anim.start();

        GetData task = new GetData();
        task.execute(user_id, user_cate);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        logo_anim.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.actionbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.logout_btn:
                onClickLogout();
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
        intent.putExtra("nick",nick);
        intent.putExtra("rank",rank);
        startActivity(intent);
    }

    public void onClickRank(View v)
    {
        final Intent intent = new Intent(this, RankGameActivity.class);
        intent.putExtra("id",user_id);
        intent.putExtra("cate",user_cate);
        intent.putExtra("nick",nick);
        intent.putExtra("rank",rank);
        startActivity(intent);
    }

    public void onClickTeam(View v)
    {
        final Intent intent = new Intent(this, TeamGameActivity.class);
        intent.putExtra("id",user_id);
        intent.putExtra("cate",user_cate);
        intent.putExtra("nick",nick);
        intent.putExtra("rank",rank);
        startActivity(intent);
    }
    public void onClickTier(View v)
    {
        Tier_dialog Tierinfo = new Tier_dialog(this, rank);
        Tierinfo.show();
    }

    private void onClickLogout() {
        if(user_cate.equals("kakao"))
            UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
        else if(user_cate.equals("google"))
        {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            final Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("menuactivity", "google onConnectionFailed:" + connectionResult);
    }

    class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            StringTokenizer st = new StringTokenizer(result, ".");

            nick = st.nextToken();
            rank = st.nextToken();
            highrank = st.nextToken();
            exp = st.nextToken();
            win = st.nextToken();
            lose = st.nextToken();

            setUserInfo();
            Log.d("GET RESULT", result);
        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[0];
            String cate = (String)params[1];

            String serverURL = "http://115.71.233.23/get_data.php";
            String postParameters = "id=" + id + "&category=" + cate;


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

    private void setUserInfo()
    {
        int cur_exp = Integer.parseInt(exp);
        int cur_rank = Integer.parseInt(rank);
        int delimeter;
        int level;
        float progress;

        textNick.setText(nick);
        textRank.setText(rank);

        delimeter = 0;
        for(int i=1;;i++)
        {
            delimeter += i*1000;
            if(cur_exp < delimeter) {
                level = i;
                break;
            }
        }
        textLevel.setText(Integer.toString(level));
        progressbar.setMax(delimeter);
        progressbar.setProgress(cur_exp);

        progress = cur_exp;
        progress /= delimeter;
        progress *= 100f;
        progress = Math.round(progress*100f) / 100f;

        textExp.setText(String.valueOf(progress) + '%');

        if(cur_rank >=0 && cur_rank <= 500)
        {
            imageTier.setImageResource(bronze);
        }
        else if(cur_rank >= 501 && cur_rank <= 1000)
        {
            imageTier.setImageResource(silver);
        }
        else if(cur_rank >= 1001 && cur_rank <= 1500)
        {
            imageTier.setImageResource(gold);
        }
        else if(cur_rank >= 1501 && cur_rank <= 2000)
        {
            imageTier.setImageResource(platinum);
        }
        else if(cur_rank >= 2001 && cur_rank <= 3000)
        {
            imageTier.setImageResource(dia);
        }
        else if(cur_rank >= 3001 && cur_rank <= 4500)
        {
            imageTier.setImageResource(master);
        }
        else if(cur_rank > 4500)
        {
            imageTier.setImageResource(grand);
        }
    }
}

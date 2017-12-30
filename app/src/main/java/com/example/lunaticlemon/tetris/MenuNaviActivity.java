package com.example.lunaticlemon.tetris;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.lunaticlemon.tetris.Clan.clan_info_dialog;
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

public class MenuNaviActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    static String user_id, user_cate, nick, clantag, clanstat, singlerank;
    String clan, exp;
    String singlewin, singlelose, singlehigh;
    String teamrank, teamwin, teamlose, teamhigh;
    MediaPlayer mp_bgm;
    Fragment selectedFragment = null;
    int fragmentSelected = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_battle:
                    fragmentSelected = 0;
                    selectedFragment = Fragment_battle.newInstance();
                    break;
                case R.id.navigation_clan:
                    fragmentSelected = 1;
                    if(clantag.equals("-1"))
                        selectedFragment = Fragment_noclan.newInstance();
                    else
                        selectedFragment = Fragment_clan.newInstance();
                    break;
                case R.id.navigation_user:
                    fragmentSelected = 2;
                    selectedFragment = Fragment_user.newInstance();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch(fragmentSelected)
            {
                case 0:
                    transaction.replace(R.id.frame_layout, selectedFragment, "battle");
                    break;
                case 1:
                    if(clantag.equals("-1"))
                        transaction.replace(R.id.frame_layout, selectedFragment, "noclan");
                    else
                        transaction.replace(R.id.frame_layout, selectedFragment, "clan");
                    break;
                case 2:
                    transaction.replace(R.id.frame_layout, selectedFragment, "user");
                    break;
            }
            transaction.commit();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ac = getSupportActionBar();
        ac.setTitle("Tetris");
        ac.setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_menu_navi);

        user_id = null;
        user_cate = null;

        user_id = getIntent().getExtras().getString("id");
        user_cate = getIntent().getExtras().getString("cate");

        mp_bgm = MediaPlayer.create(this, R.raw.main);
        mp_bgm.setLooping(true);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
            case R.id.help_btn:
                startActivity(new Intent(getApplication(),HelpActivity.class));
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

    @Override
    public void onResume()
    {
        super.onResume();

        mp_bgm.start();

        if(user_id == null || user_cate == null)
        {
            user_id = getIntent().getExtras().getString("id");
            user_cate = getIntent().getExtras().getString("cate");
        }

        SetData(user_id, user_cate);
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

    public void onClickTier(View v)
    {
        Tier_dialog Tierinfo = new Tier_dialog(this, singlerank);
        Tierinfo.show();
    }

    public void onClickSingle(View v)
    {
        startActivity(new Intent(getApplication(),SingleGameActivity.class));
    }

    public void onClickUser(View v)
    {
        final Intent intent = new Intent(this, RoomActivity.class);
        intent.putExtra("nick",nick);
        intent.putExtra("rank",singlerank);
        startActivity(intent);
    }

    public void onClickRank(View v)
    {
        final Intent intent = new Intent(this, RankGameActivity.class);
        intent.putExtra("id",user_id);
        intent.putExtra("cate",user_cate);
        intent.putExtra("nick",nick);
        intent.putExtra("rank",singlerank);
        intent.putExtra("status", "match");
        startActivity(intent);
    }

    public void onClickTeam(View v)
    {
        final Intent intent = new Intent(this, TeamGameActivity.class);
        intent.putExtra("id",user_id);
        intent.putExtra("cate",user_cate);
        intent.putExtra("nick",nick);
        intent.putExtra("rank",teamrank);
        intent.putExtra("status", "match");
        startActivity(intent);
    }

    public void onClickMake(View v)
    {
        startActivity(new Intent(getApplication(),NewClanActivity.class));
    }

    public void onClickClanInfo(View v)
    {
        clan_info_dialog clan_dialog = new clan_info_dialog(this, clantag, clanstat);
        clan_dialog.show();
    }

    public void sendNotify()
    {
        Fragment_clan frag = (Fragment_clan) getSupportFragmentManager().findFragmentByTag("clan");
        frag.sendNotify();
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
                            final Intent intent = new Intent(MenuNaviActivity.this, LoginActivity.class);
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
        Log.d("menunaviactivity", "google onConnectionFailed:" + connectionResult);
    }

    public void onClanChange(String status)
    {
        SetData(MenuNaviActivity.user_id, MenuNaviActivity.user_cate);
    }

    public void startSingle(String _status)
    {
        final Intent intent = new Intent(this, RankGameActivity.class);
        intent.putExtra("id",MenuNaviActivity.user_id);
        intent.putExtra("cate",MenuNaviActivity.user_cate);
        intent.putExtra("nick",MenuNaviActivity.nick);
        intent.putExtra("rank",MenuNaviActivity.singlerank);
        intent.putExtra("status", _status);
        startActivity(intent);
    }

    public void startTeam(String _status)
    {
        final Intent intent = new Intent(this, TeamGameActivity.class);
        intent.putExtra("id",user_id);
        intent.putExtra("cate",user_cate);
        intent.putExtra("nick",nick);
        intent.putExtra("rank",teamrank);
        intent.putExtra("status", _status);
        startActivity(intent);
    }

    public void SetData(String _id, String _cate) {
        class GetData extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Log.d("getData", "processing");

                StringTokenizer st = new StringTokenizer(result, ".");

                nick = st.nextToken();
                clantag = st.nextToken();
                clan = st.nextToken();
                clanstat = st.nextToken();
                exp = st.nextToken();
                singlerank = st.nextToken();
                singlewin = st.nextToken();
                singlelose = st.nextToken();
                singlehigh = st.nextToken();
                teamrank = st.nextToken();
                teamwin = st.nextToken();
                teamlose = st.nextToken();
                teamhigh = st.nextToken();

                switch (fragmentSelected) {
                    case 0:
                        selectedFragment = Fragment_battle.newInstance();
                        break;
                    case 1:
                        if(clantag.equals("-1"))
                            selectedFragment = Fragment_noclan.newInstance();
                        else
                            selectedFragment = Fragment_clan.newInstance();
                        break;
                    case 2:
                        selectedFragment = Fragment_user.newInstance();
                        break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch(fragmentSelected)
                {
                    case 0:
                        transaction.replace(R.id.frame_layout, selectedFragment, "battle");
                        break;
                    case 1:
                        if(clantag.equals("-1"))
                            transaction.replace(R.id.frame_layout, selectedFragment, "noclan");
                        else
                            transaction.replace(R.id.frame_layout, selectedFragment, "clan");
                        break;
                    case 2:
                        transaction.replace(R.id.frame_layout, selectedFragment, "user");
                        break;
                }
                transaction.commit();
            }


            @Override
            protected String doInBackground(String... params) {

                String id = (String) params[0];
                String cate = (String) params[1];

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
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    return sb.toString();


                } catch (Exception e) {
                    return new String("Error: " + e.getMessage());
                }
            }
        }

        GetData task = new GetData();
        task.execute(_id, _cate);
    }
}

package com.example.lunaticlemon.tetris;


import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

/**
 * Created by lemon on 2017-07-23.
 */

public class Fragment_battle extends Fragment {

    String nick, clan, clantag, clanstat, exp;
    String singlerank, singlewin, singlelose, singlehigh;
    String teamrank, teamwin, teamlose, teamhigh;

    ImageView imageLogo;
    AnimationDrawable logo_anim;

    TextView textBattleNick, textBattleSingleRank, textBattleTeamRank, textBattleLevel, textBattleExp;
    ImageView imageBattleSingleTier, imageBattleTeamTier;
    ProgressBar progressbar;

    public static Fragment_battle newInstance() {
        Fragment_battle fragment = new Fragment_battle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_battle, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageLogo = (ImageView) getView().findViewById(R.id.imageBattleLogo);
        logo_anim = (AnimationDrawable) imageLogo.getBackground();

        textBattleNick = (TextView) getView().findViewById(R.id.textBattleNick);
        textBattleSingleRank = (TextView) getView().findViewById(R.id.textBattleSingleRank);
        textBattleTeamRank = (TextView) getView().findViewById(R.id.textBattleTeamRank);
        textBattleLevel = (TextView) getView().findViewById(R.id.textBattleLevel);
        textBattleExp = (TextView) getView().findViewById(R.id.textBattleExp);
        imageBattleSingleTier = (ImageView) getView().findViewById(R.id.imageBattleSingleTier);
        imageBattleTeamTier = (ImageView) getView().findViewById(R.id.imageBattleTeamTier);
        progressbar = (ProgressBar) getView().findViewById(R.id.progressBar);


    }

    @Override
    public void onResume()
    {
        super.onResume();

        logo_anim.start();

        GetData task = new GetData();
        task.execute(MenuNaviActivity.user_id, MenuNaviActivity.user_cate);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        logo_anim.stop();
    }

    public void init()
    {
        int cur_exp = Integer.parseInt(exp);
        int cur_singlerank = Integer.parseInt(singlerank);
        int cur_teamrank = Integer.parseInt(teamrank);
        int delimeter;
        int level;
        float progress;

        textBattleNick.setText(nick);
        textBattleSingleRank.setText(singlerank);
        textBattleTeamRank.setText(teamrank);

        delimeter = 0;
        for(int i=1;;i++)
        {
            delimeter += i*1000;
            if(cur_exp < delimeter) {
                level = i;
                break;
            }
        }
        textBattleLevel.setText(Integer.toString(level));
        progressbar.setMax(delimeter);
        progressbar.setProgress(cur_exp);

        progress = cur_exp;
        progress /= delimeter;
        progress *= 100f;
        progress = Math.round(progress*100f) / 100f;

        textBattleExp.setText(String.valueOf(progress) + '%');

        if(cur_singlerank >=0 && cur_singlerank <= 500)
        {
            imageBattleSingleTier.setImageResource(bronze);
        }
        else if(cur_singlerank >= 501 && cur_singlerank <= 1000)
        {
            imageBattleSingleTier.setImageResource(silver);
        }
        else if(cur_singlerank >= 1001 && cur_singlerank <= 1500)
        {
            imageBattleSingleTier.setImageResource(gold);
        }
        else if(cur_singlerank >= 1501 && cur_singlerank <= 2000)
        {
            imageBattleSingleTier.setImageResource(platinum);
        }
        else if(cur_singlerank >= 2001 && cur_singlerank <= 3000)
        {
            imageBattleSingleTier.setImageResource(dia);
        }
        else if(cur_singlerank >= 3001 && cur_singlerank <= 4500)
        {
            imageBattleSingleTier.setImageResource(master);
        }
        else if(cur_singlerank > 4500)
        {
            imageBattleSingleTier.setImageResource(grand);
        }

        if(cur_teamrank >=0 && cur_teamrank <= 500)
        {
            imageBattleTeamTier.setImageResource(bronze);
        }
        else if(cur_teamrank >= 501 && cur_teamrank <= 1000)
        {
            imageBattleTeamTier.setImageResource(silver);
        }
        else if(cur_teamrank >= 1001 && cur_teamrank <= 1500)
        {
            imageBattleTeamTier.setImageResource(gold);
        }
        else if(cur_teamrank >= 1501 && cur_teamrank <= 2000)
        {
            imageBattleTeamTier.setImageResource(platinum);
        }
        else if(cur_teamrank >= 2001 && cur_teamrank <= 3000)
        {
            imageBattleTeamTier.setImageResource(dia);
        }
        else if(cur_teamrank >= 3001 && cur_teamrank <= 4500)
        {
            imageBattleTeamTier.setImageResource(master);
        }
        else if(cur_teamrank > 4500)
        {
            imageBattleTeamTier.setImageResource(grand);
        }
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

            init();
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
}

package com.example.lunaticlemon.tetris;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Fragment_user extends Fragment {

    String nick, clan, clantag, clanstat, exp;
    String singlerank, singlewin, singlelose, singlehigh;
    String teamrank, teamwin, teamlose, teamhigh;

    TextView textUserNick, textUserLevel, textUserClan;
    TextView textUserSingleRank, textUserSingleWin, textUserSingleLose, textUserSingleHigh;
    TextView textUserTeamRank, textUserTeamWin, textUserTeamLose, textUserTeamHigh;
    ImageView imageUserSingleTier, imageUserTeamTier;
    EditText editNick;
    Button btn_change;

    public static Fragment_user newInstance() {
        Fragment_user fragment = new Fragment_user();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textUserNick = (TextView) getView().findViewById(R.id.textUserNick);
        textUserLevel = (TextView) getView().findViewById(R.id.textUserLevel);
        textUserClan = (TextView) getView().findViewById(R.id.textUserClan);

        textUserSingleRank = (TextView) getView().findViewById(R.id.textUserSingleRank);
        textUserSingleWin = (TextView) getView().findViewById(R.id.textUserSingleWin);
        textUserSingleLose = (TextView) getView().findViewById(R.id.textUserSingleLose);
        textUserSingleHigh = (TextView) getView().findViewById(R.id.textUserSingleHigh);

        textUserTeamRank = (TextView) getView().findViewById(R.id.textUserTeamRank);
        textUserTeamWin = (TextView) getView().findViewById(R.id.textUserTeamWin);
        textUserTeamLose = (TextView) getView().findViewById(R.id.textUserTeamLose);
        textUserTeamHigh = (TextView) getView().findViewById(R.id.textUserTeamHigh);

        imageUserSingleTier = (ImageView) getView().findViewById(R.id.imageUserSingleTier);
        imageUserTeamTier = (ImageView) getView().findViewById(R.id.imageUserTeamTier);

        editNick = (EditText) getView().findViewById(R.id.editTextNick);
        btn_change = (Button) getView().findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_nick = editNick.getText().toString();
                editNick.setText("");
                if(new_nick.equals(""))
                {
                    Toast toast = Toast.makeText(getActivity(), "닉네임을 입력해야 합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                    ChangeNick(MenuNaviActivity.user_id, new_nick, MenuNaviActivity.user_cate);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        GetData task = new GetData();
        task.execute(MenuNaviActivity.user_id, MenuNaviActivity.user_cate);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private void init()
    {
        int cur_exp = Integer.parseInt(exp);
        int cur_singlerank = Integer.parseInt(singlerank);
        int cur_teamrank = Integer.parseInt(teamrank);
        int delimeter;
        int level;

        textUserNick.setText(nick);

        if(clan.equals("-1"))
            textUserClan.setText("없음");
        else
            textUserClan.setText(clan);
        textUserSingleRank.setText(singlerank);
        textUserSingleWin.setText(singlewin);
        textUserSingleLose.setText(singlelose);
        textUserSingleHigh.setText(singlehigh);

        textUserTeamRank.setText(teamrank);
        textUserTeamWin.setText(teamwin);
        textUserTeamLose.setText(teamlose);
        textUserTeamHigh.setText(teamhigh);

        delimeter = 0;
        for(int i=1;;i++)
        {
            delimeter += i*1000;
            if(cur_exp < delimeter) {
                level = i;
                break;
            }
        }
        textUserLevel.setText(Integer.toString(level));

        if(cur_singlerank >=0 && cur_singlerank <= 500)
        {
            imageUserSingleTier.setImageResource(bronze);
        }
        else if(cur_singlerank >= 501 && cur_singlerank <= 1000)
        {
            imageUserSingleTier.setImageResource(silver);
        }
        else if(cur_singlerank >= 1001 && cur_singlerank <= 1500)
        {
            imageUserSingleTier.setImageResource(gold);
        }
        else if(cur_singlerank >= 1501 && cur_singlerank <= 2000)
        {
            imageUserSingleTier.setImageResource(platinum);
        }
        else if(cur_singlerank >= 2001 && cur_singlerank <= 3000)
        {
            imageUserSingleTier.setImageResource(dia);
        }
        else if(cur_singlerank >= 3001 && cur_singlerank <= 4500)
        {
            imageUserSingleTier.setImageResource(master);
        }
        else if(cur_singlerank > 4500)
        {
            imageUserSingleTier.setImageResource(grand);
        }

        if(cur_teamrank >=0 && cur_teamrank <= 500)
        {
            imageUserTeamTier.setImageResource(bronze);
        }
        else if(cur_teamrank >= 501 && cur_teamrank <= 1000)
        {
            imageUserTeamTier.setImageResource(silver);
        }
        else if(cur_teamrank >= 1001 && cur_teamrank <= 1500)
        {
            imageUserTeamTier.setImageResource(gold);
        }
        else if(cur_teamrank >= 1501 && cur_teamrank <= 2000)
        {
            imageUserTeamTier.setImageResource(platinum);
        }
        else if(cur_teamrank >= 2001 && cur_teamrank <= 3000)
        {
            imageUserTeamTier.setImageResource(dia);
        }
        else if(cur_teamrank >= 3001 && cur_teamrank <= 4500)
        {
            imageUserTeamTier.setImageResource(master);
        }
        else if(cur_teamrank > 4500)
        {
            imageUserTeamTier.setImageResource(grand);
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

    public void ChangeNick(String _id, String _new_nick, String _cate){
        class ChangeNick extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                String id = (String) params[0];
                String nick = (String) params[1];
                String cate = (String) params[2];

                String serverURL = "http://115.71.233.23/change_nick.php";
                String postParameters = "id=" + id + "&nick=" + nick + "&category=" + cate;


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
                GetData task = new GetData();
                task.execute(MenuNaviActivity.user_id, MenuNaviActivity.user_cate);
            }
        }

        ChangeNick g = new ChangeNick();
        g.execute(_id, _new_nick, _cate);
    }
}

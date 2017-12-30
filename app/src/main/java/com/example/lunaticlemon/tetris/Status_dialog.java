package com.example.lunaticlemon.tetris;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Created by lemon on 2017-07-25.
 */

public class Status_dialog extends Dialog {
    Context con;
    String status;
    String match;
    int rank_change;

    String[] user1, user2, user3, user4;

    Button btn_ok;

    TextView textStatus;
    TextView textTeam1High, textTeam2High;
    TextView textUser1Nick, textUser1Clan, textUser1Rank, textUser1Win, textUser1Lose, textUser1High;
    TextView textUser2Nick, textUser2Clan, textUser2Rank, textUser2Win, textUser2Lose, textUser2High;
    TextView textUser3Nick, textUser3Clan, textUser3Rank, textUser3Win, textUser3Lose, textUser3High;
    TextView textUser4Nick, textUser4Clan, textUser4Rank, textUser4Win, textUser4Lose, textUser4High;

    public Status_dialog(@NonNull Context context, String _match) {
        super(context);

        this.setCanceledOnTouchOutside(true); // 다이알로그 바깥영역 터치시, 다이알로그 닫기
        this.setCancelable(true); // 백키로 다이알로그 닫기

        con = context;
        match = _match;

        new Thread() {
            public void run() {
                if(match.equals("single"))
                {
                    user1 = getData(RankView.user1_id, RankView.user1_cate);
                    user2 = getData(RankView.user2_id, RankView.user2_cate);
                }
                else if(match.equals("team")) {
                    user1 = getData(TeamView.user1_id, TeamView.user1_cate);
                    user2 = getData(TeamView.user2_id, TeamView.user2_cate);
                    user3 = getData(TeamView.user3_id, TeamView.user3_cate);
                    user4 = getData(TeamView.user4_id, TeamView.user4_cate);
                }
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.status_dialog);

        textStatus = (TextView) findViewById(R.id.textStatus);

        textTeam1High = (TextView) findViewById(R.id.textTeam1High);
        textTeam2High = (TextView) findViewById(R.id.textTeam2High);

        textUser1Nick = (TextView) findViewById(R.id.textUser1Nick);
        textUser1Clan = (TextView) findViewById(R.id.textUser1Clan);
        textUser1Rank = (TextView) findViewById(R.id.textUser1Rank);
        textUser1Win = (TextView) findViewById(R.id.textUser1Win);
        textUser1Lose = (TextView) findViewById(R.id.textUser1Lose);
        textUser1High = (TextView) findViewById(R.id.textUser1High);

        textUser2Nick = (TextView) findViewById(R.id.textUser2Nick);
        textUser2Clan = (TextView) findViewById(R.id.textUser2Clan);
        textUser2Rank = (TextView) findViewById(R.id.textUser2Rank);
        textUser2Win = (TextView) findViewById(R.id.textUser2Win);
        textUser2Lose = (TextView) findViewById(R.id.textUser2Lose);
        textUser2High = (TextView) findViewById(R.id.textUser2High);

        textUser3Nick = (TextView) findViewById(R.id.textUser3Nick);
        textUser3Clan = (TextView) findViewById(R.id.textUser3Clan);
        textUser3Rank = (TextView) findViewById(R.id.textUser3Rank);
        textUser3Win = (TextView) findViewById(R.id.textUser3Win);
        textUser3Lose = (TextView) findViewById(R.id.textUser3Lose);
        textUser3High = (TextView) findViewById(R.id.textUser3Line);

        textUser4Nick = (TextView) findViewById(R.id.textUser4Nick);
        textUser4Clan = (TextView) findViewById(R.id.textUser4Clan);
        textUser4Rank = (TextView) findViewById(R.id.textUser4Rank);
        textUser4Win = (TextView) findViewById(R.id.textUser4Win);
        textUser4Lose = (TextView) findViewById(R.id.textUser4Lose);
        textUser4High = (TextView) findViewById(R.id.textUser4High);


        View.OnClickListener ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonOK:
                        dismiss();
                        ((Activity) con).finish();
                        break;
                    default:
                        break;
                }
            }
        };

        btn_ok = (Button) findViewById(R.id.buttonOK);
        btn_ok.setOnClickListener(ClickListener);

        init();
    }

    public void set(String _status, int _rank_change) {
        status = _status;
        rank_change = _rank_change;
    }

    public void init()
    {
        if(match.equals("single"))
        {
            textTeam1High.setText(user1[5]);
            textTeam2High.setText(user2[5]);

            textUser1Nick.setText(user1[0]);
            if(user1[1].equals("-1"))
                textUser1Clan.setText("없음");
            else
                textUser1Clan.setText(user1[2]);
            textUser1Rank.setText(user1[5]);
            textUser1Win.setText(user1[6]);
            textUser1Lose.setText(user1[7]);
            textUser1High.setText(user1[8]);

            textUser3Nick.setText(user2[0]);
            if(user2[1].equals("-1"))
                textUser3Clan.setText("없음");
            else
                textUser3Clan.setText(user2[2]);
            textUser3Rank.setText(user2[5]);
            textUser3Win.setText(user2[6]);
            textUser3Lose.setText(user2[7]);
            textUser3High.setText(user2[8]);
        }
        else if(match.equals("team"))
        {
            int team1, team2;

            team1 = Integer.parseInt(user1[9]) + Integer.parseInt(user2[9]);
            team2 = Integer.parseInt(user3[9]) + Integer.parseInt(user4[9]);
            textTeam1High.setText(Integer.toString(team1));
            textTeam1High.setText(Integer.toString(team2));


            textUser1Nick.setText(user1[0]);
            if(user1[1].equals("-1"))
                textUser1Clan.setText("없음");
            else
                textUser1Clan.setText(user1[2]);
            textUser1Rank.setText(user1[9]);
            textUser1Win.setText(user1[10]);
            textUser1Lose.setText(user1[11]);
            textUser1High.setText(user1[12]);

            textUser2Nick.setText(user2[0]);
            if(user2[1].equals("-1"))
                textUser2Clan.setText("없음");
            else
                textUser2Clan.setText(user2[2]);
            textUser2Rank.setText(user2[9]);
            textUser2Win.setText(user2[10]);
            textUser2Lose.setText(user2[11]);
            textUser2High.setText(user2[12]);

            textUser3Nick.setText(user3[0]);
            if(user3[1].equals("-1"))
                textUser3Clan.setText("없음");
            else
                textUser3Clan.setText(user3[2]);
            textUser3Rank.setText(user3[9]);
            textUser3Win.setText(user3[10]);
            textUser3Lose.setText(user3[11]);
            textUser3High.setText(user3[12]);

            textUser4Nick.setText(user4[0]);
            if(user4[1].equals("-1"))
                textUser4Clan.setText("없음");
            else
                textUser4Clan.setText(user4[2]);
            textUser4Rank.setText(user4[9]);
            textUser4Win.setText(user4[10]);
            textUser4Lose.setText(user4[11]);
            textUser4High.setText(user4[12]);
        }

        if(status.equals("lose"))
        {
            textStatus.setText("패배 " + "-" + Integer.toString(rank_change));
            textStatus.setTextColor(Color.RED);
        }
        else if(status.equals("win"))
        {
            textStatus.setText("승리 " + "+" + Integer.toString(rank_change));
            textStatus.setTextColor(Color.BLUE);
        }
    }

    @Override
    public void show() {
        super.show();
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

    public String[] getData(String _id, String _cate) {

        String id = _id;
        String cate = _cate;

        String serverURL = "http://115.71.233.23/get_data.php";
        String postParameters = "id=" + id + "&category=" + cate;

        String[] result = new String[13];

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

            StringTokenizer st = new StringTokenizer(sb.toString(), ".");

            result[0] = st.nextToken();
            result[1] = st.nextToken();
            result[2] = st.nextToken();
            result[3] = st.nextToken();
            result[4] = st.nextToken();
            result[5] = st.nextToken();
            result[6] = st.nextToken();
            result[7] = st.nextToken();
            result[8] = st.nextToken();
            result[9] = st.nextToken();
            result[10] = st.nextToken();
            result[11] = st.nextToken();
            result[12] = st.nextToken();
            return result;
        } catch (Exception e) {
            Log.d("status dialog",e.toString());
            return null;
        }
    }
}
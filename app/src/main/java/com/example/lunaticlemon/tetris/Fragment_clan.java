package com.example.lunaticlemon.tetris;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lunaticlemon.tetris.Clan.Clan_Chat_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.lunaticlemon.tetris.MenuNaviActivity.clanstat;
import static com.example.lunaticlemon.tetris.MenuNaviActivity.clantag;
import static com.example.lunaticlemon.tetris.MenuNaviActivity.user_cate;
import static com.example.lunaticlemon.tetris.MenuNaviActivity.user_id;

/**
 * Created by lemon on 2017-07-23.
 */

public class Fragment_clan extends Fragment {

    LinearLayout layout_match, layout_cancel;
    ImageView clanMark;
    TextView clanName, clanTag;
    EditText textChat;
    Button btn_send, btn_single, btn_team, btn_cancel;

    ListView listview;
    static Clan_Chat_Adapter adapter;

    int mark, number, invite, rank_limit;
    String name;

    JSONArray clans = null;

    ClanClient client;
    Thread clientThread = null;
    Thread SendThread = null;
    Thread matchThread = null;
    static int isMatched = 0;     // 0:none, 1:single, 2:team

    public static Fragment_clan newInstance() {
        Fragment_clan fragment = new Fragment_clan();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clan, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layout_match = (LinearLayout) getView().findViewById(R.id.layoutBtn_Match);
        layout_cancel = (LinearLayout) getView().findViewById(R.id.layoutBtn_Cancel);
        layout_cancel.setVisibility(View.GONE);

        clanMark = (ImageView) getView().findViewById(R.id.imageClanMark);
        clanName = (TextView) getView().findViewById(R.id.textClanName);
        clanTag = (TextView) getView().findViewById(R.id.textClanTag);
        textChat = (EditText) getView().findViewById(R.id.editTextChat);
        btn_send = (Button) getView().findViewById(R.id.buttonSend);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Date dt = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                final String currentTime = sdf.format(dt);
                final String chat_msg = textChat.getText().toString();

                if(!chat_msg.equals("")) {
                    textChat.setText("");

                    adapter.addItem(Clan_Chat_Adapter.cur_num++, chat_msg, currentTime, MenuNaviActivity.nick, clanstat);
                    adapter.notifyDataSetChanged();

                    SendThread = new Thread() {
                        @Override
                        public void run() {
                            client.sendMsg(chat_msg, currentTime);
                        }
                    };
                    SendThread.start();
                }
                else
                {
                    Toast toast = Toast.makeText(getActivity(), "메세지를 입력하세요.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        btn_single = (Button) getView().findViewById(R.id.btn_single);
        btn_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Date dt = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                final String currentTime = sdf.format(dt);

                adapter.addItem(Clan_Chat_Adapter.cur_num++, "friendlysingle", currentTime, MenuNaviActivity.nick, clanstat);
                adapter.notifyDataSetChanged();

                layout_match.setVisibility(View.GONE);
                layout_cancel.setVisibility(View.VISIBLE);

                matchThread = new Thread() {
                    @Override
                    public void run() {
                        isMatched = 1;
                        client.sendMatch("single", currentTime);
                    }
                };
                matchThread.start();
            }
        });

        btn_team = (Button) getView().findViewById(R.id.btn_team);
        btn_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Date dt = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                final String currentTime = sdf.format(dt);

                adapter.addItem(Clan_Chat_Adapter.cur_num++, "friendlyteam", currentTime, MenuNaviActivity.nick, clanstat);
                adapter.notifyDataSetChanged();

                layout_match.setVisibility(View.GONE);
                layout_cancel.setVisibility(View.VISIBLE);

                matchThread = new Thread() {
                    @Override
                    public void run() {
                        isMatched = 2;
                        client.sendMatch("team", currentTime);
                    }
                };
                matchThread.start();
            }
        });

        btn_cancel = (Button) getView().findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Date dt = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                final String currentTime = sdf.format(dt);

                layout_match.setVisibility(View.VISIBLE);
                layout_cancel.setVisibility(View.GONE);

                SendThread = new Thread() {
                    @Override
                    public void run() {
                        isMatched = 0;
                        client.sendMatch("cancel", currentTime);
                    }
                };
                SendThread.start();
            }
        });

        if(isMatched == 0)
        {
            layout_match.setVisibility(View.VISIBLE);
            layout_cancel.setVisibility(View.GONE);
        }
        else
        {
            layout_match.setVisibility(View.GONE);
            layout_cancel.setVisibility(View.VISIBLE);
        }

        adapter = new Clan_Chat_Adapter(this);


        listview = (ListView) getView().findViewById(R.id.listViewChat);
        listview.setAdapter(adapter);

        client = new ClanClient(getActivity(), user_id, user_cate, clantag, clanstat);
        clientThread = new Thread(client);

        getClanData(clantag);
        getChat(clantag, user_id, user_cate);

        if(!clientThread.isAlive())
            clientThread.start();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        client.onFaced = false;

        SendThread = new Thread() {
            @Override
            public void run() {
                client.disconnectServer();
            }
        };
        SendThread.start();


        Log.d("clan","out");
    }

    public void sendSet(final String _match, final String _nick, final String _time)
    {
        SendThread = new Thread() {
            @Override
            public void run() {
                client.sendSet(_match, clantag, _nick, _time);
            }
        };
        SendThread.start();
        if(_match.equals("single"))
            ((MenuNaviActivity) getActivity()).startSingle(clantag + _time);
        else if(_match.equals("team"))
            ((MenuNaviActivity) getActivity()).startTeam(clantag + _time);
    }

    public void sendSign(final String _nick, final String _time, final int _type)    // 1 ok, 0 nok
    {
        SendThread = new Thread() {
            @Override
            public void run() {
                java.util.Date dt = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                final String currentTime = sdf.format(dt);

                client.sendSign(clantag, _nick, _time, _type, currentTime);
            }
        };
        SendThread.start();
    }

    public void sendNotify()
    {
        SendThread = new Thread() {
            @Override
            public void run() {
                client.sendNotify();
            }
        };
        SendThread.start();
    }

    public void getClanData(String _tag){
        class GetClanJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {


                String tag = (String)params[0];

                String serverURL = "http://115.71.233.23/get_clan_info.php";
                String postParameters = "tag=" + tag;


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
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    clans = jsonObj.getJSONArray("result");

                    JSONObject c = clans.getJSONObject(0);

                    mark = c.getInt("mark");
                    name = c.getString("name");
                    number = c.getInt("number");
                    invite = c.getInt("invite");
                    rank_limit = c.getInt("rank");

                    clanMark.setImageResource(getResources().getIdentifier("c"+mark,"drawable",getActivity().getPackageName()));
                    clanName.setText(name);
                    clanTag.setText(clantag);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        GetClanJSON g = new GetClanJSON();
        g.execute(_tag);
    }

    public void getChat(String _tag, String _id, String _cate){
        class GetChatDataJSON extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {


                String tag = (String)params[0];
                String id = (String) params[1];
                String cate = (String) params[2];

                String serverURL = "http://115.71.233.23/get_clan_chat.php";
                String postParameters = "tag=" + tag + "&id=" + id + "&category=" + cate;


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
                adapter.init();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    clans = jsonObj.getJSONArray("result");

                    for(int i=0;i<clans.length();i++){
                        JSONObject c = clans.getJSONObject(i);
                        int num = c.getInt("num");
                        String message = c.getString("message");
                        String nick = c.getString("nick");
                        String clanstat = c.getString("clanstat");
                        String time = c.getString("time");

                        adapter.addItem(num, message, time, nick, clanstat );
                    }

                    adapter.notifyDataSetChanged();
                    listview.setSelection(adapter.getCount()-1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        GetChatDataJSON g = new GetChatDataJSON();
        g.execute(_tag, _id, _cate);
    }
}

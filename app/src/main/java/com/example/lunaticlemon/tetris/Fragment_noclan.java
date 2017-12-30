package com.example.lunaticlemon.tetris;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.lunaticlemon.tetris.Clan.Clan_List;
import com.example.lunaticlemon.tetris.Clan.Clan_List_Adapter;
import com.example.lunaticlemon.tetris.Clan.clan_info_dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lemon on 2017-07-23.
 */

public class Fragment_noclan extends Fragment {

    ListView listview;
    Clan_List_Adapter adapter;

    EditText clanName;

    JSONArray clans = null;

    ClanClient client;
    Thread clientThread = null;
    Thread SendThread = null;

    public static Fragment_noclan newInstance() {
        Fragment_noclan fragment = new Fragment_noclan();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noclan, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new Clan_List_Adapter(this);

        listview = (ListView) getView().findViewById(R.id.noClanListView);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener( new ListViewItemClickListener());

        getData();

        clanName = (EditText) getView().findViewById(R.id.editClanName);
        clanName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {

                String filterText = edit.toString() ;
                if (filterText.length() > 0) {
                    listview.setFilterText(filterText) ;
                } else {
                    listview.clearTextFilter() ;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        }) ;

        client = new ClanClient(getActivity(), MenuNaviActivity.user_id, MenuNaviActivity.user_cate, MenuNaviActivity.clantag, MenuNaviActivity.clanstat);
        clientThread = new Thread(client);

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

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            final int selectedPos = position;

            Clan_List item = (Clan_List) adapter.getItem(selectedPos);

            clan_info_dialog clan_dialog = new clan_info_dialog(getActivity(), item.getTag(), "-1");
            clan_dialog.show();
        }
    }

    public void sendInvite(final String _clantag)
    {
        SendThread = new Thread() {
            @Override
            public void run() {
                java.util.Date dt = new java.util.Date();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                final String currentTime = sdf.format(dt);

                client.sendInvite(_clantag, currentTime);
            }
        };
        SendThread.start();
    }

    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL("http://115.71.233.23/get_clan.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

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
                Log.d("noclan",result);
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    clans = jsonObj.getJSONArray("result");

                    for(int i=0;i<clans.length();i++){
                        JSONObject c = clans.getJSONObject(i);
                        String tag = c.getString("tag");
                        String name = c.getString("name");
                        String id = c.getString("id");
                        String cate = c.getString("category");
                        int number = c.getInt("number");
                        int mark = c.getInt("mark");
                        int invite = c.getInt("invite");
                        int rank = c.getInt("rank");
                        adapter.addItem(tag, name, id, cate, number, mark, invite, rank);
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
}


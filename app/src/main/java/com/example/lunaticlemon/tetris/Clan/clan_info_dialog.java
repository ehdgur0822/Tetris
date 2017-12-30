package com.example.lunaticlemon.tetris.Clan;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lunaticlemon.tetris.MenuNaviActivity;
import com.example.lunaticlemon.tetris.R;
import com.example.lunaticlemon.tetris.User_List;
import com.example.lunaticlemon.tetris.User_List_Adapter;
import com.example.lunaticlemon.tetris.user_setting_dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.view.View.INVISIBLE;

/**
 * Created by lemon on 2017-07-28.
 */

public class clan_info_dialog extends Dialog {

    Context con;
    String tag;
    String stat;

    JSONArray clans = null;

    User_List_Adapter adapter;

    ImageView clanMark, btn_setting;
    TextView clanName, clanRank, clanInvite, clanLimit, clanTag, clanNumber;
    ListView listview;
    Button btn_out;

    String name;
    int mark, number, invite, rank_limit;
    int total_rank = 0;

    public clan_info_dialog(@NonNull Context context, String _tag, String _stat) {
        super(context);

        this.setCanceledOnTouchOutside(true); // 다이알로그 바깥영역 터치시, 다이알로그 닫히지 않기
        this.setCancelable(true); // 백키로 다이알로그 닫기

        con = context;
        tag = _tag;
        stat = _stat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.clan_info_dialog);

        clanMark = (ImageView) findViewById(R.id.imageClanMark);
        clanName = (TextView) findViewById(R.id.textClanName);
        clanRank = (TextView) findViewById(R.id.textClanRank);
        clanInvite = (TextView) findViewById(R.id.textClanInvite);
        clanLimit = (TextView) findViewById(R.id.textClanLimit);
        clanTag = (TextView) findViewById(R.id.textClanTag);
        clanNumber = (TextView) findViewById(R.id.textClanNumber);
        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat.equals("2") && number > 1) {
                    Toast toast = Toast.makeText(con, "대표 자리를 위임해야합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(con);
                    builder.setMessage("정말 탈퇴하시겠습니까?");
                    builder.setPositiveButton("예",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            ClanOut(tag, MenuNaviActivity.user_id, MenuNaviActivity.user_cate);
                            ((MenuNaviActivity) con).onClanChange("out");
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
            }
        });

        adapter = new User_List_Adapter();

        listview = (ListView) findViewById(R.id.listviewClanMember);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                User_List item = adapter.getItem(position);


                if(Integer.parseInt(MenuNaviActivity.clanstat) >= 1 && item.getStat() != 2) {
                    user_setting_dialog user_dialog = new user_setting_dialog(con, item.getId(), item.getNick(), item.getCate(), item.getStat(), MenuNaviActivity.clantag, Integer.parseInt(MenuNaviActivity.clanstat));
                    user_dialog.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            getUserData(tag);
                        }

                    });
                    // set position
                    int res[];
                    res = toViewRawXY(adapter.getView(position, view, parent));

                    WindowManager.LayoutParams params = user_dialog.getWindow().getAttributes();
                    params.y = res[1]-400;
                    user_dialog.getWindow().setAttributes(params);
                    user_dialog.show();
                }
            }
        });

        btn_setting = (ImageView) findViewById(R.id.imageSetting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat.equals("2")) {
                    clan_setting_dialog clan_setting_dialog = new clan_setting_dialog(con, tag, mark, invite, rank_limit);
                    clan_setting_dialog.show();
                    dismiss();
                }else
                {
                    Toast toast = Toast.makeText(con, "관리자만 사용가능합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
         });

        if(stat.equals("-1"))
        {

            btn_out.setVisibility(INVISIBLE);
        }

        getClanData(tag);
        getUserData(tag);
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

    private int[] toViewRawXY(View view){
        View parentView = view.getRootView();
        int result[] = new int [2];
        int sumX = 0;
        int sumY = 0;

        boolean chk =false;
        while (!chk) {
            sumX = sumX + view.getLeft();
            sumY = sumY + view.getTop();

            view = (View)view.getParent();
            if (parentView == view) {
                chk = true;
            }
        }

        result[0] = sumX;
        result[1] = sumY;

        return result;
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

                    clanMark.setImageResource(con.getResources().getIdentifier("c"+mark,"drawable",con.getPackageName()));

                    clanName.setText(name);
                    switch(invite)
                    {
                        case 0:
                            clanInvite.setText("임의로 가입");
                            break;
                        case 1:
                            clanInvite.setText("초대한정");
                            break;
                    }
                    clanLimit.setText(Integer.toString(rank_limit));
                    clanTag.setText(tag);
                    clanNumber.setText(Integer.toString(number));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        GetClanJSON g = new GetClanJSON();
        g.execute(_tag);
    }

    public void getUserData(String _tag){
        class GetUserJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {


                String tag = (String)params[0];

                String serverURL = "http://115.71.233.23/get_clanuser_info.php";
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

                    adapter.init();

                    for(int i=0;i<clans.length();i++){
                        JSONObject c = clans.getJSONObject(i);

                        String nick = c.getString("nick");
                        String id = c.getString("id");
                        String cate = c.getString("category");
                        int clanstat = c.getInt("clanstat");
                        int singlerank = c.getInt("singlerank");
                        int teamrank = c.getInt("teamrank");

                        total_rank += singlerank;

                        adapter.addItem(i+1, nick, id, cate, clanstat, singlerank, teamrank);
                    }

                    clanRank.setText(Integer.toString(total_rank));
                    adapter.notifyDataSetChanged();

                    Log.d("clan_info","getuserdata");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        GetUserJSON g = new GetUserJSON();
        g.execute(_tag);
    }

    public void ClanOut(String _tag, String _id, String _cate){
        class SignOutClan extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Log.d("singout",result);

                dismiss();
            }


            @Override
            protected String doInBackground(String... params) {

                String tag = (String)params[0];
                String id = (String)params[1];
                String cate = (String)params[2];

                Log.d("singout",tag + id + cate);
                String serverURL = "http://115.71.233.23/out_clan.php";
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

        SignOutClan g = new SignOutClan();
        g.execute(_tag, _id, _cate);
    }
}
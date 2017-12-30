package com.example.lunaticlemon.tetris.Clan;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lunaticlemon.tetris.Fragment_noclan;
import com.example.lunaticlemon.tetris.MenuNaviActivity;
import com.example.lunaticlemon.tetris.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by lemon on 2017-07-27.
 */

public class Clan_List_Adapter extends BaseAdapter implements Filterable {

    private ArrayList<Clan_List> clan_list = new ArrayList<Clan_List>();
    private ArrayList<Clan_List> filteredClanList = clan_list;
    Filter listFilter ;
    Fragment_noclan fragment;

    public Clan_List_Adapter(Fragment_noclan _fragment) {
        this.fragment = _fragment;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null)
        {
            listFilter = new ListFilter();
        }
        return listFilter ;
    }

    private class ViewHolder{
        public ImageView clanMark;
        public TextView clanName;
        public TextView clanTag;
        public TextView clanNumber;
        public TextView clanInvite;
        public Button btn_sign;
    }

    @Override
    public int getCount() {
        return filteredClanList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredClanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_clan, parent, false);

            holder.clanMark = (ImageView) convertView.findViewById(R.id.imageClanMark);
            holder.clanName = (TextView) convertView.findViewById(R.id.textClanName);
            holder.clanTag = (TextView) convertView.findViewById(R.id.textClanTag);
            holder.clanNumber = (TextView) convertView.findViewById(R.id.textClanNumber);
            holder.clanInvite = (TextView) convertView.findViewById(R.id.textClanInvite);
            holder.btn_sign = (Button) convertView.findViewById(R.id.btn_sign);

            holder.btn_sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(filteredClanList.get(pos).getRank_limit() <= Integer.parseInt(MenuNaviActivity.singlerank)) {
                        AlertDialog.Builder alertDlg = new AlertDialog.Builder(context);
                        alertDlg.setTitle(filteredClanList.get(pos).getName() + "클랜에 가입하시겠습니까?");

                        alertDlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(filteredClanList.get(pos).getInvite() == 0) {
                                    java.util.Date dt = new java.util.Date();
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    final String currentTime = sdf.format(dt);

                                    signClan(filteredClanList.get(pos).getTag(), currentTime, MenuNaviActivity.user_id, MenuNaviActivity.user_cate);
                                    ((MenuNaviActivity) context).onClanChange("in");
                                }
                                else if(filteredClanList.get(pos).getInvite() == 1)
                                {
                                    fragment.sendInvite(filteredClanList.get(pos).getTag());
                                }

                            }
                        });

                        alertDlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDlg.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(context, "랭크 점수가 부족합니다.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Clan_List listViewItem = filteredClanList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        int ImageResource = context.getResources().getIdentifier("c"+listViewItem.getMark(),"drawable", context.getPackageName());
        holder.clanMark.setImageResource(ImageResource);
        holder.clanName.setText(listViewItem.getName());
        holder.clanTag.setText(listViewItem.getTag());
        holder.clanNumber.setText(String.valueOf(listViewItem.getNumber()));
        Log.d("why",String.valueOf(listViewItem.getInvite()));
        switch(listViewItem.getInvite())
        {
            case 0:
                holder.clanInvite.setText("임의로 가입");
                break;
            case 1:
                holder.clanInvite.setText("초대한정");
                break;
        }

        return convertView;
    }

    public void addItem(String _tag, String _name, String _id, String _cate, int _number, int _mark, int _invite, int _rank_limit)
    {
        Clan_List item = new Clan_List(_tag, _name, _id, _cate, _number, _mark, _invite, _rank_limit);

        clan_list.add(item);
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            Log.d("clanadapter","filter");
            if (constraint == null || constraint.length() == 0) {
                results.values = clan_list;
                results.count = clan_list.size() ;
            } else {
                ArrayList<Clan_List> itemList = new ArrayList<Clan_List>() ;

                for (Clan_List item : clan_list) {
                    if (item.getName().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredClanList = (ArrayList<Clan_List>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }

    public void signClan(String _tag, String _time, String _id, String _cate){
        class SignClan extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Log.d("sing",result);
            }

            @Override
            protected String doInBackground(String... params) {

                String tag = (String) params[0];
                String time = (String) params[1];
                String id = (String) params[2];
                String cate = (String) params[3];

                String serverURL = "http://115.71.233.23/sign_clan.php";
                String postParameters = "tag=" + tag + "&time=" + time + "&id=" + id + "&category=" + cate;


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

        SignClan g = new SignClan();
        g.execute(_tag, _time, _id, _cate);
    }
}

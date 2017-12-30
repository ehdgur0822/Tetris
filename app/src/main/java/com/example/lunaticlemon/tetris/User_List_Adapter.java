package com.example.lunaticlemon.tetris;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lemon on 2017-07-28.
 */

public class User_List_Adapter extends BaseAdapter{

    private ArrayList<User_List> user_list = new ArrayList<User_List>();
    Context context;

    public User_List_Adapter() {
    }

    private class ViewHolder{
        public TextView textRate;
        public TextView textNick;
        public TextView textStat;
        public TextView textSingleRank;
        public TextView textTeamRank;
    }

    @Override
    public int getCount() {
        return user_list.size();
    }

    @Override
    public User_List getItem(int position) {
        return user_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        context = parent.getContext();

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_member, parent, false);

            holder.textRate = (TextView) convertView.findViewById(R.id.textUserRate);
            holder.textNick = (TextView) convertView.findViewById(R.id.textUserNick);
            holder.textStat = (TextView) convertView.findViewById(R.id.textUserStat);
            holder.textSingleRank = (TextView) convertView.findViewById(R.id.textUserSingleRank);
            holder.textTeamRank = (TextView) convertView.findViewById(R.id.textUserTeamRank);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        User_List listViewItem = user_list.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        holder.textRate.setText(Integer.toString(listViewItem.getRate()));
        holder.textNick.setText(listViewItem.getNick());
        switch(listViewItem.getStat())
        {
            case 0:
                holder.textStat.setText("클랜원");
                break;
            case 1:
                holder.textStat.setText("부마스터");
                break;
            case 2:
                holder.textStat.setText("마스터");
                break;
        }
        holder.textSingleRank.setText(Integer.toString(listViewItem.getSinglerank()));
        holder.textTeamRank.setText(Integer.toString(listViewItem.getTeamrank()));

        return convertView;
    }

    public void init()
    {
        user_list.clear();
    }

    public void addItem(int _rate, String _nick, String _id, String _cate, int _stat, int _singlerank, int _teamrank)
    {
        User_List item = new User_List(_rate, _nick, _id, _cate, _stat, _singlerank, _teamrank);

        user_list.add(item);
    }
}

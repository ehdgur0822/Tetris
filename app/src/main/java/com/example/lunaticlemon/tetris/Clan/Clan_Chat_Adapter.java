package com.example.lunaticlemon.tetris.Clan;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lunaticlemon.tetris.Fragment_clan;
import com.example.lunaticlemon.tetris.MenuNaviActivity;
import com.example.lunaticlemon.tetris.R;

import java.util.ArrayList;

import static com.example.lunaticlemon.tetris.R.id.textChat;

/**
 * Created by lemon on 2017-08-01.
 */

public class Clan_Chat_Adapter extends BaseAdapter{

    private ArrayList<Clan_Chat> chat_list = new ArrayList<Clan_Chat>();
    static int cur_num;
    Fragment_clan fragment;
    public Clan_Chat_Adapter(Fragment_clan _fragment) {
        fragment = _fragment;
        cur_num = -1;
    }

    private class ViewHolder{
        public LinearLayout layout, textLayout, btnLayout;
        public TextView textChat, textNick, textStat, textTime;
        public View viewLeft, viewRight;
        public Button btn_ok, btn_nok, btn_enter;
    }

    @Override
    public int getCount() {
        return chat_list.size();
    }

    @Override
    public Object getItem(int position) {
        return chat_list.get(position);
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
            convertView = inflater.inflate(R.layout.list_clanchat, parent, false);

            holder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
            holder.textLayout = (LinearLayout) convertView.findViewById(R.id.textLayout);
            holder.btnLayout = (LinearLayout) convertView.findViewById(R.id.btnLayout);
            holder.textChat = (TextView) convertView.findViewById(textChat);
            holder.textNick = (TextView) convertView.findViewById(R.id.textNick);
            holder.textStat = (TextView) convertView.findViewById(R.id.textStat);
            holder.textTime = (TextView) convertView.findViewById(R.id.textTime);
            holder.viewLeft = (View) convertView.findViewById(R.id.imageViewleft);
            holder.viewRight = (View) convertView.findViewById(R.id.imageViewright);
            holder.btn_ok = (Button) convertView.findViewById(R.id.btn_ok);
            holder.btn_nok = (Button) convertView.findViewById(R.id.btn_nok);
            holder.btn_enter = (Button) convertView.findViewById(R.id.btn_enter);

            holder.btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("clanchatadapter",chat_list.get(pos).getNick() + "/" + chat_list.get(pos).getRealTime());
                    if(!MenuNaviActivity.clanstat.equals("0")) {
                        fragment.sendSign(chat_list.get(pos).getNick(), chat_list.get(pos).getRealTime(), 1);
                    }
                    else{
                        Toast toast = Toast.makeText(context, "관리자가 아닙니다.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

            holder.btn_nok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("clanchatadapter",chat_list.get(pos).getNick() + "/" + chat_list.get(pos).getRealTime());
                    if(!MenuNaviActivity.clanstat.equals("0")) {
                        fragment.sendSign(chat_list.get(pos).getNick(), chat_list.get(pos).getRealTime(), 0);
                    }
                    else{
                        Toast toast = Toast.makeText(context, "관리자가 아닙니다.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

            holder.btn_enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("why","ads");
                    if(!chat_list.get(pos).getNick().equals(MenuNaviActivity.nick) && Fragment_clan.isMatched == 0)
                    {
                        switch(chat_list.get(pos).getRealMessage())
                        {
                            case "friendlysingle":
                                fragment.sendSet("single", chat_list.get(pos).getNick(), chat_list.get(pos).getRealTime());
                                break;
                            case "friendlyteam":
                                fragment.sendSet("team", chat_list.get(pos).getNick(), chat_list.get(pos).getRealTime());
                                break;
                        }

                    }
                }
            });

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Clan_Chat listViewItem = chat_list.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        switch(listViewItem.getType())
        {
            case 0: // left
                holder.textLayout.setBackgroundResource(R.drawable.layout_form0);
                holder.layout.setGravity(Gravity.LEFT);
                holder.viewRight.setVisibility(View.GONE);
                holder.viewLeft.setVisibility(View.GONE);
                holder.btnLayout.setVisibility(View.GONE);
                break;
            case 1: // right
                holder.textLayout.setBackgroundResource(R.drawable.layout_form1);
                holder.layout.setGravity(Gravity.RIGHT);
                holder.viewRight.setVisibility(View.GONE);
                holder.viewLeft.setVisibility(View.GONE);
                holder.btnLayout.setVisibility(View.GONE);
                break;
            case 2: // mid
                holder.textLayout.setBackgroundResource(R.drawable.layout_form2);
                holder.layout.setGravity(Gravity.CENTER);
                holder.viewRight.setVisibility(View.VISIBLE);
                holder.viewLeft.setVisibility(View.VISIBLE);
                holder.btnLayout.setVisibility(View.GONE);
                break;
            case 3: // mid
                holder.textLayout.setBackgroundResource(R.drawable.layout_form2);
                holder.layout.setGravity(Gravity.CENTER);
                holder.viewRight.setVisibility(View.VISIBLE);
                holder.viewLeft.setVisibility(View.VISIBLE);
                holder.btnLayout.setVisibility(View.VISIBLE);
                holder.btn_enter.setVisibility(View.INVISIBLE);
                break;
            case 4: // mid
                holder.textLayout.setBackgroundResource(R.drawable.layout_form2);
                holder.layout.setGravity(Gravity.CENTER);
                holder.viewRight.setVisibility(View.VISIBLE);
                holder.viewLeft.setVisibility(View.VISIBLE);
                holder.btnLayout.setVisibility(View.VISIBLE);
                holder.btn_ok.setVisibility(View.INVISIBLE);
                holder.btn_nok.setVisibility(View.INVISIBLE);
        }

        holder.textChat.setText(listViewItem.getMessage());
        holder.textNick.setText(listViewItem.getNick());
        holder.textStat.setText(listViewItem.getClanstat());
        holder.textTime.setText(listViewItem.getTime());
        return convertView;
    }

    public void init()
    {
        chat_list.clear();
    }

    public void addItem(int _num, String _message, String _time, String _nick, String _clanstat)
    {
        Clan_Chat item = new Clan_Chat(_num, _message, _time, _nick, _clanstat);

        if(cur_num < _num)
            cur_num = _num;

        chat_list.add(item);

        notifyDataSetChanged();
    }
}

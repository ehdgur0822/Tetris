package com.example.lunaticlemon.tetris.Clan;

import com.example.lunaticlemon.tetris.MenuNaviActivity;

/**
 * Created by lemon on 2017-08-01.
 */

public class Clan_Chat {
    int num;
    String message;
    String time;
    String nick;
    String clanstat;

    public Clan_Chat(int _num, String _message, String _time, String _nick, String _clanstat)
    {
        num = _num;
        message = _message;
        time = _time;
        nick = _nick;
        clanstat = _clanstat;
    }

    public String getRealMessage(){
        return message;
    }

    public String getMessage() {
        switch(message)
        {
            case "signin":
                return "클랜원이 가입하였습니다.";
            case "signout":
                return "클랜원이 탈퇴하였습니다.";
            case "signto":
                return "가입신청 하였습니다.";
            case "signok":
                return "가입신청을 수락하였습니다.";
            case "signalready":
                return "이미 다른클랜에 가입하였습니다.";
            case "signnok":
                return "가입신청을 거절하였습니다.";
            case "friendlysingle":
                return "1vs1 친선전\n나랑 한판 붙죠!";
            case "friendlyteam":
                return "2vs2 친선전\n나랑 한판 하죠!";
            case "settomaster":
                return "마스터가 되었습니다.";
            case "settosecond":
                return "부마스터가 되었습니다.";
            case "settoclan":
                return "클랜원이 되었습니다.";
            case "settoout":
                return "추방당하였습니다.";
            default:
                return message;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRealTime()
    {
        return time;
    }

    public String getTime()
    {
        try{
            java.util.Date dt = new java.util.Date();
            java.util.Date savedTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);

            java.text.SimpleDateFormat cur = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.text.SimpleDateFormat time_form = new java.text.SimpleDateFormat("HH:mm:ss");

            String currentDay = cur.format(dt);
            String savedDay = cur.format(savedTime);

            if(currentDay.equals(savedDay))
            {
                return time_form.format(savedTime);
            }
            else
            {
                java.util.Date cur_date = cur.parse(currentDay);
                java.util.Date saved_date = cur.parse(savedDay);

                long diff = cur_date.getTime() - saved_date.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);

                return String.valueOf(diffDays) + "일전";
            }

        }catch(java.text.ParseException e){}

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getType(){
        if(message.startsWith("sign"))
        {
            switch(message)
            {
                case "signto":
                    return 3;
                default:
                    return 2;
            }
        }
        else if(message.startsWith("friendly"))
        {
            return 4;
        }
        else if(message.startsWith("setto"))
        {
            return 2;
        }
        else if(nick.equals(MenuNaviActivity.nick))
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public String getClanstat() {
        switch(clanstat)
        {
            case "0":
                return "클랜원";
            case "1":
                return "부마스터";
            case "2":
                return "마스터";
        }
        return "클랜원";
    }

    public void setClanstat(String clanstat) {
        this.clanstat = clanstat;
    }
}

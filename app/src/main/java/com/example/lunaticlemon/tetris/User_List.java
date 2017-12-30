package com.example.lunaticlemon.tetris;

/**
 * Created by lemon on 2017-07-28.
 */

public class User_List {

    int rate;
    String nick;
    String id;
    String cate;
    int stat;
    int singlerank;
    int teamrank;

    public User_List(int _rate, String _nick, String _id, String _cate, int _stat, int _singlerank, int _teamrank)
    {
        rate = _rate;
        nick = _nick;
        id = _id;
        cate = _cate;
        stat = _stat;
        singlerank = _singlerank;
        teamrank = _teamrank;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public int getSinglerank() {
        return singlerank;
    }

    public void setSinglerank(int singlerank) {
        this.singlerank = singlerank;
    }

    public int getTeamrank() {
        return teamrank;
    }

    public void setTeamrank(int teamrank) {
        this.teamrank = teamrank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }
}

package com.example.lunaticlemon.tetris.Clan;

/**
 * Created by lemon on 2017-07-27.
 */

public class Clan_List {
    String tag;
    String name;
    String master_id;
    String master_cate;
    int number;
    int mark;
    int invite;
    int rank_limit;

    public Clan_List(String _tag, String _name, String _id, String _cate, int _number, int _mark, int _invite, int _rank_limit)
    {
        tag = _tag;
        name = _name;
        master_id = _id;
        master_cate = _cate;
        number = _number;
        mark = _mark;
        invite = _invite;
        rank_limit = _rank_limit;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getMaster_cate() {
        return master_cate;
    }

    public void setMaster_cate(String master_cate) {
        this.master_cate = master_cate;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getInvite() {
        return invite;
    }

    public void setInvite(int invite) {
        this.invite = invite;
    }

    public int getRank_limit() {
        return rank_limit;
    }

    public void setRank_limit(int rank_limit) {
        this.rank_limit = rank_limit;
    }
}

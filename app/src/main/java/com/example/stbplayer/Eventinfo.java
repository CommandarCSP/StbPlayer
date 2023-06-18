package com.example.stbplayer;

public class Eventinfo {
    String pev_id;
    String pev_startday;
    String pev_endday;
    String pev_streamname;
    int pev_isforced;
    String pch_id;
    String pev_userid;
    String pev_streamdesc;
    String pch_no;
    String pch_mstreamip;
    int pch_mstreamport;
    String pch_name;
    String pev_pft_id;

    public String getPev_pft_id() {
        return pev_pft_id;
    }

    public void setPev_pft_id(String pev_pft_id) {
        this.pev_pft_id = pev_pft_id;
    }

    @Override
    public String toString() {
        return "Eventinfo{" +
                "pev_id='" + pev_id + '\'' +
                ", pev_startday='" + pev_startday + '\'' +
                ", pev_endday='" + pev_endday + '\'' +
                ", pev_streamname='" + pev_streamname + '\'' +
                ", pev_isforced=" + pev_isforced +
                ", pch_id='" + pch_id + '\'' +
                ", pev_userid='" + pev_userid + '\'' +
                ", pev_streamdesc='" + pev_streamdesc + '\'' +
                ", pch_no='" + pch_no + '\'' +
                ", pch_mstreamip='" + pch_mstreamip + '\'' +
                ", pch_mstreamport=" + pch_mstreamport +
                ", pch_name='" + pch_name + '\'' +
                ", pev_pft_id='" + pev_pft_id + '\'' +
                '}';
    }
}

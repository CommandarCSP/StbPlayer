package com.example.stbplayer;

public class AccessLogPacket {

    String accessdate;
    String ipaddress;
    String pev_id;
    String userid;
    String hostname;
    String psg_id;
    int flag;

    public AccessLogPacket() {
        this.hostname = null;
        this.flag = 1;

    }

    public void setAccessdate(String accessdate) {
        this.accessdate = accessdate;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public void setPev_id(String pev_id) {
        this.pev_id = pev_id;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPsg_id(String psg_id) {
        this.psg_id = psg_id;
    }

    @Override
    public String toString() {
        return "AccessLogPacket{" +
                "accessdate='" + accessdate + '\'' +
                ", ipaddress='" + ipaddress + '\'' +
                ", pev_id='" + pev_id + '\'' +
                ", userid='" + userid + '\'' +
                ", hostname='" + hostname + '\'' +
                ", psg_id='" + psg_id + '\'' +
                ", flag=" + flag +
                '}';
    }
}

package com.example.stbplayer;

import java.util.Arrays;

public class EventModel {
    Eventinfo[] eventinfo;
    String groupinfo;
    Notice notice;
    Object chat;

    public String getGroupinfo() {
        return groupinfo;
    }

    public Eventinfo[] getEventinfo() {
        return eventinfo;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "eventinfo=" + Arrays.toString(eventinfo) +
                ", groupinfo='" + groupinfo + '\'' +
                ", notice=" + notice +
                ", chat=" + chat +
                '}';
    }
}

package com.example.stbplayer;

public class VideoControl {

    String groupId;
    String videoName;

    public VideoControl(String groupId, String videoName) {
        this.groupId = groupId;
        this.videoName = videoName;
    }

    public VideoControl(String videoName) {
        this.videoName = videoName;
    }

    public VideoControl() {

    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}

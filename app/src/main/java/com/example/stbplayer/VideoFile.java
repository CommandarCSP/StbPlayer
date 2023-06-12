package com.example.stbplayer;

import java.util.Objects;

public class VideoFile {
    String name;
    long size;

    public VideoFile(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoFile videoFile = (VideoFile) o;
        return size == videoFile.size && Objects.equals(name, videoFile.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size);
    }


    @Override
    public String toString() {
        return "VideoFile{" +
                "name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}

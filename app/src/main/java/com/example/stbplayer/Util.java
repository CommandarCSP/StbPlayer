package com.example.stbplayer;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Util {

    public static String getFilePath(Context context, String fileName) {

        String directory = context.getExternalFilesDir(null).getAbsolutePath();

        return directory + "/" + fileName;
    };

    public static ArrayList<VideoFile> getLocalFiles(Context context) {

        return (ArrayList<VideoFile>) Arrays.asList(Objects.requireNonNull(context.getExternalFilesDir(null).list())).stream().map((fileName) -> {

            String filePath = Util.getFilePath(context,fileName);
            File file = new File(filePath);

            return new VideoFile(file.getName(), file.length());
        }).collect(Collectors.toList());
    };

    public static int randomPick(int count) {

        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);

        return rand.nextInt(count);
    }
}

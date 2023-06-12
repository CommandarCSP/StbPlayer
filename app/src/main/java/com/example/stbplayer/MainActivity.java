package com.example.stbplayer;

import android.Manifest;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONObject;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private static final ArrayList<String> hosts = new ArrayList<>(Arrays.asList("192.168.219.101", "192.168.219.101"));
    private static final String username = "test";
    private static final String password = "12341234";
    int port = 22;
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

    private static final String TAG = "MainActivity";

    private LinearLayout llProgress = null;
    private VLCVideoLayout mVideoLayout = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    private File lastPlayFile = null;

    Button btnSend;

    private JSchWrapper jschWrapper = null;

    private MulticastManager multicastManager = null;

    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = findViewById(R.id.btnSend);
        llProgress = findViewById(R.id.llProgress);


        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");

        mLibVLC = new LibVLC(this, args);

        mMediaPlayer = new MediaPlayer(mLibVLC);
        mVideoLayout = findViewById(R.id.video_layout);
        mVideoLayout.setKeepScreenOn(true);


        multicastManager = new MulticastManager();

        btnSend.setOnClickListener(v -> {

            new Thread(() -> {

//                Log.d("RANDOM", "" + randomPick(2));
//
                Calendar cal = Calendar.getInstance();
                int hour = 22 + Util.randomPick(7);
                cal.set(Calendar.HOUR_OF_DAY, hour);
                Calendar now = Calendar.getInstance();

                Log.d("RANDOM", "" + hour + " " + cal.getTime().toString() + " " + (cal.getTimeInMillis() - now.getTimeInMillis()));

//                cal.set(Calendar.HOUR_OF_DAY,20);

//                multicastManager.send("test");

            }).start();

        });


        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

        mMediaPlayer.setEventListener((event) -> {

//                Log.d("EVENT","" + event.type);

            if (event.type == MediaPlayer.Event.Stopped) {
//                    Log.d("EVENT","Stopped");

                try {
                    final Media media = new Media(mLibVLC, Uri.fromFile(lastPlayFile));
                    mMediaPlayer.setMedia(media);
                    media.release();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());

                }
                mMediaPlayer.play();
            }

        });
        jschWrapper = new JSchWrapper();

        ftpProcess();

        startTask();



    }


    public void ftpProcess() {

        runOnUiThread(() -> {
            llProgress.setVisibility(View.VISIBLE);
        });

        new Thread(() -> {


            //ftp 접속 및 파일 리스트 가져오기
            jschWrapper.connectSFTP(hosts.get(Util.randomPick(2)), port, username, password);
            List<VideoFile> fileList = jschWrapper.getLs();
            Log.d(TAG, fileList.toString());

            //파일 동기화 (다운로드 및 삭제)
            //local file list
            ArrayList<VideoFile> localfiles = Util.getLocalFiles(getBaseContext());
            Log.d("BEFORE", localfiles.toString());
            fileList.forEach((videoFile) -> {

                try {

                    boolean find = localfiles.remove(videoFile);
                    String filePath = Util.getFilePath(getBaseContext(), videoFile.name);
                    File file = new File(filePath);

                    if (!find) {
                        VideoFile findVideoFile = localfiles.stream().filter(_videoFile -> Objects.equals(_videoFile.name, videoFile.name))
                                .findFirst()
                                .orElse(null);
                        localfiles.remove(findVideoFile);
                        file.delete();
                    }

                    Log.d("File", videoFile.name + " " + file.length());

                    if (!file.exists()) {
                        jschWrapper.downloadFile(videoFile.name, filePath);
                    }


                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }


            });
            if (fileList.size() == 0) {
                Util.getLocalFiles(getBaseContext()).forEach((videoFile) -> {

                    String filePath = Util.getFilePath(getBaseContext(), videoFile.name);
                    File file = new File(filePath);
                    file.delete();

                });
            }

            Log.d("AFTER", localfiles.toString());

            localfiles.forEach((videoFile) -> {

                String filePath = Util.getFilePath(getBaseContext(),videoFile.name);
                File file = new File(filePath);
                file.delete();

            });

            ArrayList<VideoFile> afterfileList = Util.getLocalFiles(getBaseContext());
            Log.d("DELETE AFTER", afterfileList.toString());


            runOnUiThread(() -> {
                llProgress.setVisibility(View.GONE);
            });


        }).start();


    }


    public void playVideoFile(File file) {

        try {

            final Media media = new Media(mLibVLC, Uri.fromFile(file));
            mMediaPlayer.setMedia(media);
            lastPlayFile = file;
            media.release();
        } catch (Exception e) {
            Log.e(TAG, e.toString());

        }

        mMediaPlayer.play();

    }


    public void startTask() {

        timerTask = new TimerTask() {

            @Override
            public void run() {
                Log.i("Test", "Timer start");
                ftpProcess();
            }
        };
        Timer timer = new Timer();
        Calendar cal = Calendar.getInstance();
        int hour = 22 + Util.randomPick(7);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        Calendar now = Calendar.getInstance();
        timer.schedule(timerTask, cal.getTimeInMillis() - now.getTimeInMillis(), 86400000);

    }


}
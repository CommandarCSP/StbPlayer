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

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

                Log.d("RANDOM", "" + randomPick(2));


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

    }

    public void ftpProcess() {

        llProgress.setVisibility(View.VISIBLE);
        new Thread(() -> {


            //ftp 접속 및 파일 리스트 가져오기
            jschWrapper.connectSFTP(hosts.get(randomPick(2)), port, username, password);
            List<String> fileList = jschWrapper.getLs();
            Log.d(TAG, fileList.toString());

            //파일 동기화 (다운로드 및 삭제)
            //local file list
            ArrayList<String> localfiles = getLocalFiles();
            Log.d("DEFORE", localfiles.toString());
            fileList.forEach((fileName) -> {

                try {

                    localfiles.remove(fileName);

                    String filePath = getFilePath(fileName);
                    File file = new File(filePath);

                    if (file.exists()) {

//                            playVideoFile(file);

                    } else {
                        jschWrapper.downloadFile(fileList.get(0), filePath);

//                            playVideoFile(file);
                    }


                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }


            });
            Log.d("AFTER", localfiles.toString());
            localfiles.forEach((localFile) -> {

                String filePath = getFilePath(localFile);
                File file = new File(filePath);
                file.delete();

            });

            ArrayList<String> afterfileList = getLocalFiles();
            Log.d("DELETE AFTER", afterfileList.toString());


            runOnUiThread(() -> {
                llProgress.setVisibility(View.GONE);
            });


        }).start();


    }

    public String getFilePath(String fileName) {
        String directory = getExternalFilesDir(null).getAbsolutePath();

        return directory + "/" + fileName;
    }

    public ArrayList<String> getLocalFiles() {

        return new ArrayList<>(Arrays.asList(Objects.requireNonNull(getExternalFilesDir(null).list())));
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

    public int randomPick(int count) {

        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);

        return rand.nextInt(count);
    }

}
package com.example.stbplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    VideoView videoView;

    Button btnSend;

    private JSchWrapper jschWrapper = null;

    private MulticastManager multicastManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setPermissionsStorage();

        videoView = findViewById(R.id.videoView);
        btnSend = findViewById(R.id.btnSend);


        videoView.setMediaController(new MediaController(this));
        multicastManager = new MulticastManager();

        btnSend.setOnClickListener(v -> {

            new Thread(() -> {

                multicastManager.send("test");

            }).start();

        });

//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//
//
//
//                try {
//
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            Log.d(TAG, "onCompletion");
//                            mediaPlayer.release();
//                        }
//                    });
//                    Log.d(TAG, "onPrepared");
//                    videoView.start();
//                } catch(Exception e) {
//                    Log.e(TAG, e.toString());
//                }
//
//
//            }
//        });

//        Uri videoUri = Uri.parse("https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4");
//        videoView.setVideoURI(videoUri);
//

        jschWrapper = new JSchWrapper();

        String host = "192.168.0.4";
        String username = "h9ftpuser";
        String password = "guest0000";
        int port = 22;

        new Thread(new Runnable() {
            public void run() {

                jschWrapper.connectSFTP(host, port, username, password);
                jschWrapper.changeDirectory("/home/h9ftpuser/www/antd-storybook");
                jschWrapper.getLs();
                try {

                    String filePath = getFilePath("sample.mp4");
                    File file = new File(filePath);

                    if(file.exists()) {
//                        Uri videoUri = Uri.fromFile(file);
                        Uri videoUri = Uri.fromFile(file);


                         runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                videoView.setVideoURI(videoUri);
                                videoView.start();
                            }
                        });


                    } else {
                        jschWrapper.downloadFile("/home/h9ftpuser/www/antd-storybook/sample-mp4-file.mp4",filePath,true);
                        Uri videoUri = Uri.fromFile(file);
                        videoView.setVideoURI(videoUri);
                    }


                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

            }
        }).start();


//        new Thread(new Runnable() {
//            public void run() {
//
//                try {
//
//                    String path = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4";
//
//                    File file = new File(getFilePath("test2.mp4"));
//
//                    if (file.exists()) {
//                        Log.d("outputFile.exists", "!!");
//                        for (String s : getExternalFilesDir(null).list()) {
//                            Log.d("File", s);
//                        }
//
//                        String videoPath = getFilePath("test2.mp4");
//                        Log.d("videoPath", videoPath);
//
//                        Uri videoUri = Uri.fromFile(new File(videoPath));
//                        videoView.setVideoURI(videoUri);
//
////                        playVideo(videoPath);
//                    } else {
//                        Log.d("outputFile no exists", "!!");
//
//                        URL url = new URL(path);
//
//                        Log.i(TAG, "Connection");
//
//                        URLConnection connection = url.openConnection();
//
//
//                        Log.i(TAG, "Open Connection");
//
//                        long fileSize = connection.getContentLength();
//
//                        Log.i(TAG, "" + fileSize);
//
//                        byte[] data = new byte[8192];
//
//                        BufferedInputStream bis = null;
//                        FileOutputStream fos = null;
//                        BufferedOutputStream bos = null;
//
//
//                        Log.i(TAG, "Process 1");
//
//                        bis = new BufferedInputStream(connection.getInputStream(), 8192);
//                        fos = new FileOutputStream(file);
//                        bos = new BufferedOutputStream(fos);
//                        long downloadedSize = 0;
//                        int count;
//
//                        Log.d(TAG, "DOWNLOAD START");
//                        while ((count = bis.read(data)) != -1) {
//
//                            downloadedSize += count;
//
//                            if (fileSize > 0) {
//                                float per = ((float) downloadedSize / fileSize) * 100;
////                                String str = "Downloaded " + downloadedSize + "KB / " + fileSize + "KB (" + (int)per + "%)";
////                                Log.i(TAG, str);
//                            }
//
//                            //파일에 데이터를 기록합니다.
//                            bos.write(data, 0, count);
//                        }
//
//                        Log.d(TAG, "DOWNLOAD END");
//
//                        // Flush output
//                        fos.flush();
//                        bos.flush();
//                        // Close streams
//                        fos.close();
//                        bis.close();
//                        bos.close();
//
//                        for (String s : getExternalFilesDir(null).list()) {
//                            Log.d("File", s);
//                        }
//
//                        String videoPath = getFilePath("test2.mp4");
//                        Log.d("videoPath", videoPath);
//
//                        Uri videoUri = Uri.fromFile(new File(videoPath));
//                        videoView.setVideoURI(videoUri);
//
//                    }
//
//                } catch (Exception e) {
//
//                    Log.e("Exception", e.toString());
//
//                }
//
//
//            }
//        }).start();



    }


    public void setPermissionsStorage() {

        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1000);
            }
            return;
        }

    }

    public String getFilePath(String fileName) {
        String directory = getExternalFilesDir(null).getAbsolutePath();
        return directory + "/" + fileName;
    }

}
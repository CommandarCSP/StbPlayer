package com.example.stbplayer;

import android.Manifest;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

    private static final String TAG = "MainActivity";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

//    VideoView videoView;

    private VLCVideoLayout mVideoLayout = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;


    Button btnSend;

    private JSchWrapper jschWrapper = null;

    private MulticastManager multicastManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());

//        setPermissionsStorage();

//        videoView = findViewById(R.id.videoView);
        btnSend = findViewById(R.id.btnSend);


        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);

        mMediaPlayer = new MediaPlayer(mLibVLC);

        mVideoLayout = findViewById(R.id.video_layout);


        multicastManager = new MulticastManager();

        btnSend.setOnClickListener(v -> {

            new Thread(() -> {

                multicastManager.send("test");

            }).start();

        });


        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);


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

                    String filePath = getFilePath("sample4.avi");
                    File file = new File(filePath);

                    if(file.exists()) {
                        try {
                            final Media media = new Media(mLibVLC, Uri.fromFile(file));
                            media.setHWDecoderEnabled(false, false);
                            mMediaPlayer.setMedia(media);
                            media.release();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());

                        }
                        mMediaPlayer.play();

                    } else {
                        jschWrapper.downloadFile("/home/h9ftpuser/www/antd-storybook/drop.avi",filePath,true);
                        try {
                            final Media media = new Media(mLibVLC, Uri.fromFile(file));
                            mMediaPlayer.setMedia(media);
                            media.release();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());

                        }
                        mMediaPlayer.play();
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
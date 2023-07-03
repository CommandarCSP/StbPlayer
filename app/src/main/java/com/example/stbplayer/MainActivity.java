package com.example.stbplayer;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {

    private static final ArrayList<String> hosts = new ArrayList<>(Arrays.asList("192.168.219.101", "192.168.219.101"));
    private static final String username = "test";
    private static final String password = "12341234";

    private static String groupId;
    int port = 22;
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

    private static final String TAG = "MainActivity";

    private LinearLayout llProgress = null;
    private VLCVideoLayout mVideoLayout = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    private File lastPlayFile = null;

    Button btnStop;

    Button btnLocal;

    Button btnStream;

    TextView tvText;
    private JSchWrapper jschWrapper = null;

    private BroadcastManager broadcastManager = null;

    private TimerTask timerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStop = findViewById(R.id.btnStop);
        btnLocal = findViewById(R.id.btnLocal);
        btnStream = findViewById(R.id.btnStream);
        llProgress = findViewById(R.id.llProgress);
        tvText = findViewById(R.id.tvText);

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");

        mLibVLC = new LibVLC(this, args);

        mMediaPlayer = new MediaPlayer(mLibVLC);
        mVideoLayout = findViewById(R.id.video_layout);
        mVideoLayout.setKeepScreenOn(true);


        broadcastManager = new BroadcastManager("192.168.219.100", 1234, (String data) -> {

            runOnUiThread(() -> {

                StringBuilder sb = new StringBuilder(tvText.getText().toString());

                sb.append("\n").append(data);

                tvText.setText(sb);

            });

            Gson gson = new Gson();
            EventModel model = gson.fromJson(data, EventModel.class);

            Log.d(TAG, model.toString());

            String groupInfo = model.getGroupinfo();

            if (!Objects.equals(groupInfo, "")) {

                Eventinfo[] eventinfos = model.getEventinfo();


                if (eventinfos.length > 0) {

                    Eventinfo eventinfo = eventinfos[0];

                    String path = eventinfo.getPev_pft_id();

                    if (path != null && !path.equals("")) {

                        int lastSlashIdx = path.lastIndexOf('\\');

                        String fileName = path.substring(lastSlashIdx + 1, path.length());

                        Log.d("fileName", fileName);

                        VideoControl videoControl = new VideoControl(groupInfo, fileName);

                        controlProcess(videoControl);

                    } else {

                        VideoControl videoControl = new VideoControl(groupInfo);
                        controlProcess(videoControl);

                    }


                }


            } else {

                VideoControl videoControl = new VideoControl();

                controlProcess(videoControl);

            }


        }, (errorMsg) -> {

            runOnUiThread(() -> {

                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();

            });


        });

        btnStop.setOnClickListener((v -> {

            String testData = getTestJsonData("stop.json");

            broadcastManager.send(testData);


        }));

        btnLocal.setOnClickListener(v -> {

            String testData = getTestJsonData("local.json");

            broadcastManager.send(testData);


        });

        btnStream.setOnClickListener(v -> {

            String testData = getTestJsonData("stream.json");

            broadcastManager.send(testData);


        });


        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

        mMediaPlayer.setEventListener((event) -> {

//                Log.d("EVENT","" + event.type);

            if (event.type == MediaPlayer.Event.Stopped) {
//                    Log.d("EVENT","Stopped");

                if (lastPlayFile != null) {
                    try {
                        final Media media = new Media(mLibVLC, Uri.fromFile(lastPlayFile));
                        mMediaPlayer.setMedia(media);
                        media.release();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());

                    }
                    mMediaPlayer.play();
                }

            }

        });
        jschWrapper = new JSchWrapper();


        ftpProcess();

        startTask();


    }


    public String getTestJsonData(String jsonPath) {

        String json = "";

        try {
            InputStream is = getAssets().open(jsonPath);
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;

    }

    public void controlProcess(VideoControl videoControl) {

        Log.d("controlProcess", videoControl.toString());

        if (mMediaPlayer.isPlaying()) {
            //비디오나 실시간 방송이 플레이 중일때
            if (videoControl.groupId == null || videoControl.groupId.equals("")) {
                // 그룹이 안들어오면 플레이 스탑
                stopPlayer();
            } else {

                if (groupId.equals(videoControl.groupId)) {

                    if (videoControl.videoName == null) {
                        startLiveStream();
                    } else if (lastPlayFile != null && !videoControl.videoName.equals(lastPlayFile.getName())) {
                        //다른 파일명으로 바껴서 로컬 비디오 파일 다른걸로 플레이 시켜야함
                        stopPlayer();
                        String filePath = Util.getFilePath(getBaseContext(), videoControl.videoName);
                        File file = new File(filePath);
                        playVideoFile(file);

                    }

                }
            }

        } else {
            // 비디오 실행중이 아닐때
            if (videoControl.groupId == null || videoControl.groupId.equals("")) {
                // 그룹이 안들어오면 플레이 스탑
                stopPlayer();
            } else if (groupId.equals(videoControl.groupId)) {

                if (videoControl.videoName == null) {
                    //실시간 방송 플레이
                    startLiveStream();

                } else {
                    //그룹아이디가 내꺼랑 같고 비디오 네임이 있을때 로컬 파일 플레이 시킴
                    String filePath = Util.getFilePath(getBaseContext(), videoControl.videoName);
                    File file = new File(filePath);
                    playVideoFile(file);
                }


            }


        }


    }

    public void stopPlayer() {
        Log.d("stopPlayer", "!!");
        lastPlayFile = null;
        mMediaPlayer.stop();

    }

    public void startLiveStream() {
        stopPlayer();
        Log.d("startLiveStream", "!!");
        //김태훈 여기보면됨
        try {
            Uri uri = Uri.parse("udp://192.168.219.101:15001");
//            Uri uri = Uri.parse("udp://@192.168.219.101:10005");
//            Uri uri = Uri.parse("udp://@:10005");
            final Media media = new Media(mLibVLC, uri);
            mMediaPlayer.setMedia(media);
            media.release();
        } catch (Exception e) {
            Log.e(TAG, e.toString());

        }

        mMediaPlayer.play();


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

                String filePath = Util.getFilePath(getBaseContext(), videoFile.name);
                File file = new File(filePath);
                file.delete();

            });

            ArrayList<VideoFile> afterfileList = Util.getLocalFiles(getBaseContext());
            Log.d("DELETE AFTER", afterfileList.toString());

            broadcastManager.receive();

            GroupIdManager groupIdManager = new GroupIdManager();

            String _groupId = groupIdManager.receiveGroupId();


            if (_groupId != null) {
                String raGroupId = _groupId.replaceAll("\"", "");
                groupId = raGroupId.substring(raGroupId.length() - 4, raGroupId.length());
                Log.d("groupId", groupId);
            }

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

        if (timerTask != null) {
            timerTask.cancel();
        }
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
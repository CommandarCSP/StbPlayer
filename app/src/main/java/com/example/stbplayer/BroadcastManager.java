package com.example.stbplayer;

import android.util.Log;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BroadcastManager {

    public static String TAG = "BroadcastManager";

    DatagramSocket sendSocket;
    DatagramSocket recvSocket;
    InetAddress ia;

    int port;

    ReceiveCallback receiveCallback;

    ErrorCallback errorCallback;

    public interface ReceiveCallback {
        public void onGetMessage(String data);

    }

    public interface ErrorCallback {

        public void errorMessage(String data);
    }

    public BroadcastManager(String host, int port, ReceiveCallback callback, ErrorCallback errorCallback) {

        try {

            sendSocket = new DatagramSocket();
            recvSocket = new DatagramSocket(port);
            ia = InetAddress.getByName(host);
            this.port = port;
            this.receiveCallback = callback;
            this.errorCallback = errorCallback;

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void send(String data) {

        new Thread(() -> {

            try {
                Log.d(TAG, "SEND");
                byte[] _data = data.getBytes();

                DatagramPacket dp = new DatagramPacket(_data, _data.length, ia, port);

                sendSocket.send(dp);

            } catch (Exception e) {
                Log.e(TAG + "Send", e.toString());
            }

        }).start();
    }

    public void receive() {

        new Thread(() -> {

            try {
                Log.d(TAG, "receive");
                byte[] _data = new byte[1024];

                DatagramPacket dp = new DatagramPacket(_data, _data.length);
                int len = 0;

                while (true) {
                    recvSocket.receive(dp);

                    len = dp.getLength();
                    String recvData = new String(_data, 0, len);

                    Log.d(TAG, len + " " + recvData);

                    if(this.receiveCallback != null) {
                        this.receiveCallback.onGetMessage(recvData);
                    }

                }

            } catch (Exception e) {
                Log.e(TAG + "Recv", e.toString());
                if(this.errorCallback != null) {
                    this.errorCallback.errorMessage(e.toString());
                }

            }

        }).start();

    }
}

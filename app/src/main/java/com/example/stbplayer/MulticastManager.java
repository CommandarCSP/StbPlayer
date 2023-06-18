package com.example.stbplayer;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastManager {

    public static String TAG = "MulticastManager";

    MulticastSocket ms;
    InetAddress ia;

    int port;

    ReceiveCallback receiveCallback;

    public interface ReceiveCallback {
        public void onGetMessage(String data);
    }

    public MulticastManager(String host, int port, ReceiveCallback callback) {

        try {

            ms = new MulticastSocket(port);
            ia = InetAddress.getByName(host);
            this.port = port;
            this.receiveCallback = callback;

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

                ms.send(dp);

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

                ms.joinGroup(ia);

                DatagramPacket dp = new DatagramPacket(_data, _data.length);
                int len = 0;

                while (true) {
                    ms.receive(dp);

                    len = dp.getLength();
                    String recvData = new String(_data, 0, len);

//                    Log.d(TAG, len + " " + recvData);

                    if(this.receiveCallback != null) {
                        this.receiveCallback.onGetMessage(recvData);
                    }

                }

            } catch (Exception e) {
                Log.e(TAG + "Recv", e.toString());
            }

        }).start();

    }
}

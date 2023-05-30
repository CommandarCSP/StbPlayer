package com.example.stbplayer;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastManager {

    public static String TAG = "MulticastManager";

    MulticastSocket ms;
    InetAddress ia;


    public MulticastManager() {

        try {

            ms = new MulticastSocket();
            ia = InetAddress.getByName("239.255.255.0");


        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void send(String data) {
        try {
            Log.d(TAG,"SEND");
            byte[] _data = data.getBytes();
            DatagramPacket dp = new DatagramPacket(_data,_data.length, ia, 1234);

            ms.send(dp);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


    }
}

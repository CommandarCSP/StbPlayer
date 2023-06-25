package com.example.stbplayer;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GroupIdManager {
    private static final String TAG = "YourAsyncTask";
    private static final String ENDPOINT_URL = "http://211.200.11.212:19001/DBWebServiceLib.asmx";
    private static final String DEFAULT_IP_ADDRESS = "192.168.0.19";

    private String idAddress;

    public GroupIdManager() {
        this.idAddress = DEFAULT_IP_ADDRESS;
    }

    public GroupIdManager(String ipAddress) {
        this.idAddress = ipAddress;
    }

    public String receiveGroupId() {

//        new Thread(() -> {

            String str, receiveMsg;

            try {
                String url = ENDPOINT_URL + "/Search_DMSGroup_ID?_ipaddress=" + this.idAddress;
                // Create URL
                URL githubEndpoint = new URL(url);
                // Create connection
                HttpURLConnection conn  =
                        (HttpURLConnection) githubEndpoint.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

//                    Log.i("receiveMsg : ", receiveMsg);

                    String result = findElement(receiveMsg,"string");

//                    Log.i("result : ", result);

                    reader.close();

                    return result;


                } else {
                    Log.i("결과", conn.getResponseCode() + "Error");
                }


            } catch (Exception e) {
                Log.e(TAG, "Error occurred: " + e.getMessage());
            }

//        }).start();


        return null;
    }

    public String findElement(String xml, String elem) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xml));
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {

                final int event = xpp.getEventType();
                if (event == XmlPullParser.START_TAG && xpp.getName().equals(elem)) {
                    xpp.next();
                    return xpp.getText();
                }
                xpp.next();

            }
        } catch (XmlPullParserException | IOException e) {
            Log.e("XmlPullParserException",e.toString());
        }
        return null;
    }

}

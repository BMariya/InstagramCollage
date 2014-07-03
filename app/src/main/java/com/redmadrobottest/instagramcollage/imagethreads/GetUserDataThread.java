package com.redmadrobottest.instagramcollage.imagethreads;

import android.content.Intent;

import com.redmadrobottest.instagramcollage.application.InstagramCollageApp;
import com.redmadrobottest.instagramcollage.receiver.Receiver;
import com.redmadrobottest.instagramcollage.structure.ImageData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class GetUserDataThread extends Thread {

    private InstagramCollageApp application;
    private String userId;

    public GetUserDataThread(InstagramCollageApp application, String userId) {
        this.application = application;
        this.userId = userId;
    }

    @Override
    public void run() {
        try {
            URL urlData = new URL("https://api.instagram.com/v1/users/" + userId + "/media/recent/?client_id=" + InstagramCollageApp.Params.CLIENT_ID);
            URLConnection connection = urlData.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final ArrayList<ImageData> imagesUri = new ArrayList<ImageData>();
            String line;
            while ((line = in.readLine()) != null) {
                JSONObject ob = new JSONObject(line);
                JSONArray data = ob.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jo = (JSONObject) data.get(i);
                    JSONObject imagesUser = (JSONObject) jo.get("images");
                    imagesUri.add(new ImageData((String) ((JSONObject) imagesUser.get("low_resolution")).get("url"),
                            (String) ((JSONObject) imagesUser.get("standard_resolution")).get("url")));
                }
            }
            application.setImageData(imagesUri);
            Intent intent = new Intent(Receiver.Params.ACTION);
            intent.putExtra(Receiver.Params.ReceiveType, Receiver.ReceiveType.drawImageData);
            application.getBroadcastManager().sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Intent intent = new Intent(Receiver.Params.ACTION);
            intent.putExtra(Receiver.Params.ReceiveType, Receiver.ReceiveType.setStateFinish);
            application.getBroadcastManager().sendBroadcast(intent);
        }
    }

}
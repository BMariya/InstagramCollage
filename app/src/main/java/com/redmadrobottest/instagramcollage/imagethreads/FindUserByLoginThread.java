package com.redmadrobottest.instagramcollage.imagethreads;

import android.content.Intent;

import com.redmadrobottest.instagramcollage.application.InstagramCollageApp;
import com.redmadrobottest.instagramcollage.receiver.Receiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FindUserByLoginThread extends Thread {

    private InstagramCollageApp application;
    private String login;

    public FindUserByLoginThread(InstagramCollageApp application, String login) {
        this.application = application;
        this.login = login;
    }

    @Override
    public void run() {
        try {
            URL urlUser = new URL("https://api.instagram.com/v1/users/search?q=" + login + "&client_id=" + InstagramCollageApp.Params.CLIENT_ID);
            URLConnection connection = urlUser.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            String id = null;
            while ((line = in.readLine()) != null) {
                JSONObject ob = new JSONObject(line);
                JSONArray data = ob.getJSONArray("data");
                JSONObject jo = (JSONObject) data.get(0);
                id = (String) jo.get("id");
            }
            if (id != null && id.length() != 0) {
                (new GetUserDataThread(application, id)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Receiver.Params.ACTION);
            intent.putExtra(Receiver.Params.ReceiveType, Receiver.ReceiveType.setStateFinish);
            application.getBroadcastManager().sendBroadcast(intent);
        }
    }

}
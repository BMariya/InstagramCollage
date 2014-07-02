package com.redmandrobottest.instagramcollage.application;

import android.app.Application;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.redmandrobottest.instagramcollage.activity.ImagesActivity;

import java.util.ArrayList;

public class InstagramCollageApp extends Application {

    private ImagesActivity activity;
    private boolean inWork;
    private ArrayList<Uri> images;
    private ArrayList<Boolean> imagesChecked;
    private LocalBroadcastManager broadcastManager;

	public void onCreate() {
		super.onCreate();

        inWork = false;
        broadcastManager = LocalBroadcastManager.getInstance(this);
	}

    public void setActivity(ImagesActivity activity) {
        this.activity = activity;
    }

    public void setWorked(boolean inWork) {
        this.inWork = inWork;
    }

    public boolean isWorked() {
        return this.inWork;
    }

    public void setImageData(ArrayList<Uri> images, ArrayList<Boolean> imagesChecked) {
        this.images = images;
        this.imagesChecked = imagesChecked;
        if (activity != null) {
            activity.drawResults();
        }
        inWork = false;
    }

    public ArrayList<Uri> getImages() {
        return this.images;
    }

    public ArrayList<Boolean> getImageCheked() {
        return this.imagesChecked;
    }

    public boolean someIsCheckedImages() {
        if (imagesChecked == null) {
            return false;
        }
        for (boolean check : imagesChecked) {
            if (check) {
                return true;
            }
        }
        return false;
    }

    public int getCountCheckedImages() {
        int countChecked = 0;
        for (boolean check : imagesChecked) {
            if (check) {
                countChecked++;
            }
        }
        return countChecked;
    }

    public LocalBroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

}
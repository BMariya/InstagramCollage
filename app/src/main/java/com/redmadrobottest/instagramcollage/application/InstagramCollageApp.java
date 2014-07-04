package com.redmadrobottest.instagramcollage.application;

import android.app.Application;
import android.support.v4.content.LocalBroadcastManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.redmadrobottest.instagramcollage.structure.ImageData;

import java.util.ArrayList;

public class InstagramCollageApp extends Application {

    public static class Params {
        public static final String CLIENT_ID = "6c55f727e6a54e8abb2342e983f488a0";
    }

    private boolean inWork;
    private ArrayList<ImageData> imagesData;
    private LocalBroadcastManager broadcastManager;

	public void onCreate() {
		super.onCreate();

        inWork = false;
        broadcastManager = LocalBroadcastManager.getInstance(this);
        DisplayImageOptions displayimageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(android.R.drawable.progress_indeterminate_horizontal).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .defaultDisplayImageOptions(displayimageOptions).build();
        ImageLoader.getInstance().init(config);
	}

    public void setWorked(boolean inWork) {
        this.inWork = inWork;
    }

    public boolean isWorked() {
        return this.inWork;
    }

    public void setImageData( ArrayList<ImageData> imagesData) {
        this.imagesData = imagesData;
    }

    public ArrayList<ImageData> getImagesData() {
        return this.imagesData;
    }

    public boolean someIsCheckedImages() {
        if (imagesData == null) {
            return false;
        }
        for (ImageData imageData : imagesData) {
            if (imageData.getChecked()) {
                return true;
            }
        }
        return false;
    }

    public int getCountCheckedImages() {
        int countChecked = 0;
        for (ImageData imageData : imagesData) {
            if (imageData.getChecked()) {
                countChecked++;
            }
        }
        return countChecked;
    }

    public LocalBroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

}
package com.redmandrobottest.instagramcollage.application;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.redmandrobottest.instagramcollage.activity.ImagesActivity;

import java.util.ArrayList;

public class InstagramCollageApp extends Application {

    private ImagesActivity activity;
    private boolean inWork;
    private ArrayList<Drawable> images;
    private ArrayList<Boolean> imagesChecked;

	public void onCreate() {
		super.onCreate();

        inWork = false;
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

    public void setImageData(ArrayList<Drawable> images, ArrayList<Boolean> imagesChecked) {
        this.images = images;
        this.imagesChecked = imagesChecked;
        if (activity != null) {
            activity.drawResults();
        }
        inWork = false;
    }

    public ArrayList<Drawable> getImages() {
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

}
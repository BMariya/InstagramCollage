package com.redmadrobottest.instagramcollage.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.redmadrobottest.instagramcollage.activity.ImageActivity;
import com.redmadrobottest.instagramcollage.application.InstagramCollageApp;

public class Receiver extends BroadcastReceiver {

    public static class Params {
        public static final String ACTION = "com.redmadrobottest.instagramcollage.Receiver";
        public static final String ReceiveType = "ReceiveType";
        public static final String BitmapImage = "BitmapImage";
    }

    public enum ReceiveType {
        setStateFinish, drawImageData, sendCollage
    }

	private ImageActivity activity;
    private InstagramCollageApp application;

	public Receiver(Context context) {
		super();

        activity = (ImageActivity) context;
        application = (InstagramCollageApp) activity.getApplication();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        switch ((ReceiveType) intent.getExtras().getSerializable(Params.ReceiveType)) {
            case setStateFinish:
                application.setWorked(false);
                activity.setStates();
                break;
            case drawImageData:
                application.setWorked(false);
                activity.drawResults();
                break;
            case sendCollage:
                activity.sendCollage(intent.getExtras().getByteArray(Params.BitmapImage));
                break;
        }
	}

}
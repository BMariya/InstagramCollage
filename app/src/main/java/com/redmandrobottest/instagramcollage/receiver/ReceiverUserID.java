package com.redmandrobottest.instagramcollage.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.redmandrobottest.instagramcollage.activity.ImagesActivity;

public class ReceiverUserID extends BroadcastReceiver {

    public static class Params {
        public static final String ACTION = "com.redmandrobottest.instagramcollage.ReceiverUserID";
        public static final String UserID = "UserID";
    }

	private ImagesActivity activity;

	public ReceiverUserID(Context context) {
		super();

        activity = (ImagesActivity) context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        activity.getUserData(intent.getExtras().getString(Params.UserID));
	}

}
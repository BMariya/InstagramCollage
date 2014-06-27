package com.RedmandRobotTest.InstagramCollage.Adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.RedmandRobotTest.InstagramCollage.Activity.ImagesActivity;
import com.RedmandRobotTest.InstagramCollage.R;
import com.RedmandRobotTest.InstagramCollage.application.InstagramCollageApp;

public class ImageAdapter extends BaseAdapter {

    private ImagesActivity activity;
    private InstagramCollageApp application;
    private LayoutInflater inflater;

	public ImageAdapter(ImagesActivity activity) {
        this.activity = activity;
        application = (InstagramCollageApp) activity.getApplication();
        inflater = LayoutInflater.from(activity);
	}

	public int getCount() {
		return application.getImages() != null ? application.getImages().size() : 0;
	}

	@Override
	public Drawable getItem(int position) {
		return application.getImages().get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
		}
		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.LI_CheckBox);
        checkBox.setChecked(application.getImageCheked().get(position));
        checkBox.setTag(position);
        checkBox.setOnClickListener(onClickCheck);
        ((ImageView) convertView.findViewById(R.id.LI_Image)).setImageDrawable(getItem(position));
		return convertView;
	}

    View.OnClickListener onClickCheck = new View.OnClickListener() {

        public void onClick(View v) {
            int position = (Integer) v.getTag();
            application.getImageCheked().set(position, !application.getImageCheked().get(position));
            activity.setMakeCollageButtonState(false);
        }

    };

}
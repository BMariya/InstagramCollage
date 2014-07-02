package com.redmandrobottest.instagramcollage.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.redmandrobottest.instagramcollage.activity.ImagesActivity;
import com.redmandrobottest.instagramcollage.R;
import com.redmandrobottest.instagramcollage.application.InstagramCollageApp;
import com.squareup.picasso.Picasso;

import java.io.IOException;

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
	public Uri getItem(int position) {
		return application.getImages().get(position);
	}

	public long getItemId(int position) {
		return position;
	}

    private static class ViewHolder {

        CheckBox checkBox;
        ImageView imageView;

        public ViewHolder(View view) {
            checkBox = (CheckBox) view.findViewById(R.id.LI_CheckBox);
            imageView = (ImageView) view.findViewById(R.id.LI_Image);
        }

    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
		if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
		} else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkBox.setTag(position);
        viewHolder.checkBox.setOnClickListener(onClickCheck);
        viewHolder.checkBox.setChecked(application.getImageCheked().get(position));
        Picasso.with(activity).load(getItem(position)).into(viewHolder.imageView);
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
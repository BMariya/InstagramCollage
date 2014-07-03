package com.redmadrobottest.instagramcollage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.redmadrobottest.instagramcollage.R;
import com.redmadrobottest.instagramcollage.activity.ImageActivity;
import com.redmadrobottest.instagramcollage.application.InstagramCollageApp;

public class ImageAdapter extends BaseAdapter {

    private ImageActivity activity;
    private InstagramCollageApp application;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

	public ImageAdapter(ImageActivity activity) {
        this.activity = activity;
        application = (InstagramCollageApp) activity.getApplication();
        inflater = LayoutInflater.from(activity);
        imageLoader = ImageLoader.getInstance();
	}

	public int getCount() {
		return application.getImagesData() != null ? application.getImagesData().size() : 0;
	}

	@Override
	public String getItem(int position) {
		return application.getImagesData().get(position).getLowResolutionUri();
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
        viewHolder.checkBox.setChecked(application.getImagesData().get(position).getChecked());
        imageLoader.displayImage(getItem(position), viewHolder.imageView);
		return convertView;
	}

    View.OnClickListener onClickCheck = new View.OnClickListener() {

        public void onClick(View v) {
            int position = (Integer) v.getTag();
            application.getImagesData().get(position).setChecked(!application.getImagesData().get(position).getChecked());
            activity.setMakeCollageButtonState(false);
        }

    };

}
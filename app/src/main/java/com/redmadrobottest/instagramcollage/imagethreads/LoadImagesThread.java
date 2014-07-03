package com.redmadrobottest.instagramcollage.imagethreads;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.redmadrobottest.instagramcollage.application.InstagramCollageApp;
import com.redmadrobottest.instagramcollage.receiver.Receiver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class LoadImagesThread extends Thread {

    private InstagramCollageApp application;
    Integer loadedImagesCounter;

    public LoadImagesThread(InstagramCollageApp application) {
        this.application = application;
        loadedImagesCounter = 0;
    }

    @Override
    public void run() {
        try {
            int countImages = application.getImagesData().size();
            for (int i = 0; i < countImages; i++) {
                if (application.getImagesData().get(i).getChecked()) {
                    final int finalId = i;
                    ImageLoader.getInstance().loadImage(application.getImagesData().get(i).getStandardResolutionUri(), new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            try {
                                File startDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                String path = "ImageInstagram_" + finalId + ".png";
                                File file = new File(startDir, path);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                loadedImage.compress(Bitmap.CompressFormat.PNG, 100, bos);
                                byte[] fileData = bos.toByteArray();
                                FileOutputStream os = new FileOutputStream(file, true);
                                os.write(fileData);
                                os.flush();
                                os.close();
                                bos.flush();
                                bos.close();
                                application.getImagesData().get(finalId).setPath(file.getAbsolutePath());
                                loadedImagesCounter++;
                            } catch (Exception e) {
                                e.printStackTrace();
                                Intent intent = new Intent(Receiver.Params.ACTION);
                                intent.putExtra(Receiver.Params.ReceiveType, Receiver.ReceiveType.setStateFinish);
                                application.getBroadcastManager().sendBroadcast(intent);
                            }
                            if (application.getCountCheckedImages() == loadedImagesCounter) {
                                Intent intent = new Intent(Receiver.Params.ACTION);
                                intent.putExtra(Receiver.Params.ReceiveType, Receiver.ReceiveType.setStateFinish);
                                application.getBroadcastManager().sendBroadcast(intent);
                                (new MakeCollageThread(application)).start();
                            }
                        }

                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Receiver.Params.ACTION);
            intent.putExtra(Receiver.Params.ReceiveType, Receiver.ReceiveType.setStateFinish);
            application.getBroadcastManager().sendBroadcast(intent);
        }
    }

}
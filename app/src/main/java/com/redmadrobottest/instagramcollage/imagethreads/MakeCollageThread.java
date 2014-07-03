package com.redmadrobottest.instagramcollage.imagethreads;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;

import com.redmadrobottest.instagramcollage.application.InstagramCollageApp;
import com.redmadrobottest.instagramcollage.receiver.Receiver;

import java.io.ByteArrayOutputStream;

public class MakeCollageThread extends Thread {

    private InstagramCollageApp application;

    public MakeCollageThread(InstagramCollageApp application) {
        this.application = application;
    }

    @Override
    public void run() {
        try {
            int firstImage;
            int countImages = application.getImagesData().size();
            Bitmap backgroundImage = null;
            for (firstImage = 0; firstImage < countImages; firstImage++) {
                if (application.getImagesData().get(firstImage).getChecked()) {
                    backgroundImage = getBitmap(firstImage);
                    break;
                }
            }
            Canvas comboImage = new Canvas(backgroundImage);
            int countCheckedImages = application.getCountCheckedImages();
            float dimensionImagesLeft = backgroundImage.getWidth() / countCheckedImages;
            float dimensionImagesTop = backgroundImage.getHeight() / countCheckedImages;
            for (int i = firstImage + 1; i < countImages; i++) {
                if (application.getImagesData().get(i).getChecked()) {
                    Bitmap bitmap = getBitmap(i);
                    comboImage.drawBitmap(bitmap, dimensionImagesLeft, dimensionImagesTop, null);
                    dimensionImagesLeft = 2 * dimensionImagesLeft;
                    dimensionImagesTop = 2 * dimensionImagesTop;
                }
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            backgroundImage.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapImage = bos.toByteArray();
            bos.flush();
            bos.close();

            Intent intent = new Intent(Receiver.Params.ACTION);
            intent.putExtra(Receiver.Params.ReceiveType, Receiver.ReceiveType.sendCollage);
            intent.putExtra(Receiver.Params.BitmapImage, bitmapImage);
            application.getBroadcastManager().sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Intent intent = new Intent(Receiver.Params.ACTION);
            intent.putExtra(Receiver.Params.ReceiveType, Receiver.ReceiveType.setStateFinish);
            application.getBroadcastManager().sendBroadcast(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Bitmap getBitmap(int imageId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;
        return BitmapFactory.decodeFile(application.getImagesData().get(imageId).getPath(), options);
    }

}
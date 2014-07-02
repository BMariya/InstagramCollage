package com.redmandrobottest.instagramcollage.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.redmandrobottest.instagramcollage.adapter.ImageAdapter;
import com.redmandrobottest.instagramcollage.dialod.CollageViewSendDialog;
import com.redmandrobottest.instagramcollage.provider.FileProvider;
import com.redmandrobottest.instagramcollage.R;
import com.redmandrobottest.instagramcollage.application.InstagramCollageApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class ImagesActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {

    public static class Params {
        public static final String Dialog = "Dialog";
        public static final String BitmapImage = "BitmapImage";
        public static final int MIN_LETTERS_LOGIN = 1;
        public static final String CLIENT_ID = "6c55f727e6a54e8abb2342e983f488a0";
    }

    private InstagramCollageApp application;
    private View getImagesButton;
    private View makeCollageButton;
    private EditText loginEdit;
    private View progress;
    private ImageAdapter adapter;
    private byte[] bitmapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (InstagramCollageApp) getApplication();
        this.application.setActivity(this);

        setContentView(R.layout.activity_images);
        adapter = new ImageAdapter(ImagesActivity.this);
        ((ListView) findViewById(R.id.AI_ImageList)).setAdapter(adapter);
        progress = findViewById(R.id.AI_Progress);
        getImagesButton = findViewById(R.id.AI_GetImages);
        getImagesButton.setOnClickListener(this);
        makeCollageButton = findViewById(R.id.AI_MakeCollage);
        makeCollageButton.setOnClickListener(this);
        loginEdit = (EditText)findViewById(R.id.AI_Login);
        loginEdit.addTextChangedListener(this);
        if (savedInstanceState != null) {
            bitmapImage = savedInstanceState.getByteArray(Params.BitmapImage);
        }
        setStates(application.isWorked());
    }

    public void drawResults() {
        adapter.notifyDataSetChanged();
        setStates(false);
    }

    private void setStates(boolean isWork) {
        progress.setVisibility(isWork ? View.VISIBLE : View.INVISIBLE);
        loginEdit.setEnabled(!isWork);
        setGetImageButtonState(isWork);
        setMakeCollageButtonState(isWork);
    }

    public void setGetImageButtonState(boolean isWork) {
        getImagesButton.setEnabled(isWork ? false : loginEdit.getText().toString().length() >= Params.MIN_LETTERS_LOGIN);
    }

    public void setMakeCollageButtonState(boolean isWork) {
        makeCollageButton.setEnabled(isWork ? false : application.someIsCheckedImages());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AI_GetImages:
                setStates(true);
                startThread();
                break;
            case R.id.AI_MakeCollage:
                int countImages = application.getImageCheked().size();
                Bitmap backgroundImage = null;
                int firstCheckedImage;
                for (firstCheckedImage = 0; firstCheckedImage < countImages; firstCheckedImage++) {
                    if (application.getImageCheked().get(firstCheckedImage)) {
                        backgroundImage = drawableToBitmap(application.getImages().get(firstCheckedImage));
                        break;
                    }
                }
                Canvas comboImage = new Canvas(backgroundImage);
                float dimensionImagesLeft = backgroundImage.getWidth() / application.getCountCheckedImages();
                float dimensionImagesTop = backgroundImage.getHeight() / application.getCountCheckedImages();
                for (int i = firstCheckedImage + 1; i < countImages; i++) {
                    if (application.getImageCheked().get(i)) {
                        comboImage.drawBitmap(drawableToBitmap(application.getImages().get(i)), dimensionImagesLeft, dimensionImagesTop, null);
                        dimensionImagesLeft = 2 * dimensionImagesLeft;
                        dimensionImagesTop = 2 * dimensionImagesTop;
                    }
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                backgroundImage.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bitmapImage = bos.toByteArray();
                Bundle dialogData = new Bundle();
                dialogData.putByteArray(CollageViewSendDialog.Params.BitmapArray, bitmapImage);
                CollageViewSendDialog dialog = new CollageViewSendDialog();
                dialog.setArguments(dialogData);
                dialog.show(getSupportFragmentManager(), Params.Dialog);
                break;
        }
    }

    public void startThread () {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    application.setWorked(true);
                    URL example = new URL("https://api.instagram.com/v1/users/search?q=" + loginEdit.getText().toString() + "&client_id=" + Params.CLIENT_ID);
                    URLConnection connection = example.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    String id = "";
                    while ((line = in.readLine()) != null) {
                        JSONObject ob = new JSONObject(line);
                        JSONArray data = ob.getJSONArray("data");
                        JSONObject jo = (JSONObject) data.get(0);
                        id = (String) jo.get("id");
                    }
                    example = new URL("https://api.instagram.com/v1/users/" + id + "/media/recent/?client_id=" + Params.CLIENT_ID);
                    connection = example.openConnection();
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    final ArrayList<Drawable> images = new ArrayList<Drawable>();
                    final ArrayList<Boolean> imagesChecked = new ArrayList<Boolean>();
                    while ((line = in.readLine()) != null) {
                        JSONObject ob = new JSONObject(line);
                        JSONArray data = ob.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jo = (JSONObject) data.get(i);
                            JSONObject imagesUser = (JSONObject) jo.get("images");
                            JSONObject imageParams = (JSONObject) imagesUser.get("low_resolution");
                            String sURL = (String) imageParams.get("url");
                            images.add(getImageByURL(sURL));
                            imagesChecked.add(false);
                        }
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            application.setImageData(images, imagesChecked);
                        }

                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    application.setWorked(false);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            setStates(false);
                        }

                    });
                }
            }

        });
        thread.start();
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Drawable getImageByURL(String sUrl) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(sUrl);
            is = (InputStream) url.getContent();
            return Drawable.createFromStream(is, "src");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public DialogInterface.OnClickListener collageViewSendDialogButtonsClick = new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
            switch (whichButton) {
                case Dialog.BUTTON_NEGATIVE:
                    break;
                case Dialog.BUTTON_POSITIVE:
                    ContentValues providerCV = new ContentValues();
                    providerCV.put(FileProvider.Params.FileData, bitmapImage);
                    getContentResolver().insert(FileProvider.Params.CONTENT_URI, providerCV);
                    getContentResolver().notifyChange(FileProvider.Params.CONTENT_URI, null);

                    Intent intentSend = new Intent(Intent.ACTION_SEND);
                    intentSend.putExtra(Intent.EXTRA_STREAM, Uri.parse(FileProvider.Params.CONTENT_URI + "collage.png"));
                    intentSend.setType(getContentResolver().getType(FileProvider.Params.CONTENT_URI));
                    startActivity(Intent.createChooser(intentSend, getString(R.string.SendWith)));
                    break;
            }
        }

    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setGetImageButtonState(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);

        saveState.putByteArray(Params.BitmapImage, bitmapImage);
    }


}
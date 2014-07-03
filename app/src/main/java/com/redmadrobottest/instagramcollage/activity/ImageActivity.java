package com.redmadrobottest.instagramcollage.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.redmadrobottest.instagramcollage.R;
import com.redmadrobottest.instagramcollage.imagethreads.FindUserByLoginThread;
import com.redmadrobottest.instagramcollage.imagethreads.LoadImagesThread;
import com.redmadrobottest.instagramcollage.adapter.ImageAdapter;
import com.redmadrobottest.instagramcollage.application.InstagramCollageApp;
import com.redmadrobottest.instagramcollage.dialod.CollageViewSendDialog;
import com.redmadrobottest.instagramcollage.provider.FileProvider;
import com.redmadrobottest.instagramcollage.receiver.Receiver;

public class ImageActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {

    public static class Params {
        public static final String Dialog = "Dialog";
        public static final String BitmapImage = "BitmapImage";
        public static final int MIN_LETTERS_LOGIN = 1;
    }

    private InstagramCollageApp application;
    private View getImagesButton;
    private View makeCollageButton;
    private EditText loginEdit;
    private View progress;
    private ImageAdapter adapter;
    private byte[] bitmapImage;
    private Receiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (InstagramCollageApp) getApplication();
        setContentView(R.layout.activity_images);

        adapter = new ImageAdapter(ImageActivity.this);
        ListView listView = (ListView) findViewById(R.id.AI_ImageList);
        listView.setAdapter(adapter);
        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(), true, false);
        listView.setOnScrollListener(listener);

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
        setStates();
    }

    public void setStates() {
        boolean isWork = application.isWorked();
        progress.setVisibility(isWork ? View.VISIBLE : View.INVISIBLE);
        loginEdit.setEnabled(!isWork);
        setGetImageButtonState(isWork);
        setMakeCollageButtonState(isWork);
    }

    public void setGetImageButtonState(boolean isWork) {
        getImagesButton.setEnabled(!isWork && loginEdit.getText().toString().length() >= Params.MIN_LETTERS_LOGIN);
    }

    public void setMakeCollageButtonState(boolean isWork) {
        makeCollageButton.setEnabled(!isWork && application.someIsCheckedImages());
    }

    @Override
    public void onResume() {
        super.onResume();

        receiver = new Receiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Receiver.Params.ACTION);
        application.getBroadcastManager().registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        application.getBroadcastManager().unregisterReceiver(receiver);
    }

    public void drawResults() {
        adapter.notifyDataSetChanged();
        setStates();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AI_GetImages:
                application.setWorked(true);
                setStates();
                (new FindUserByLoginThread(application, loginEdit.getText().toString())).start();
                break;
            case R.id.AI_MakeCollage:
                application.setWorked(true);
                setStates();
                (new LoadImagesThread(application)).start();
                break;
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

    public void sendCollage(byte[] bitmapImage) {
        this.bitmapImage = bitmapImage;
        Bundle dialogData = new Bundle();
        dialogData.putByteArray(CollageViewSendDialog.Params.BitmapArray, bitmapImage);
        CollageViewSendDialog dialog = new CollageViewSendDialog();
        dialog.setArguments(dialogData);
        dialog.show(getSupportFragmentManager(), Params.Dialog);
    }

}
package com.redmadrobottest.instagramcollage.dialod;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;

import com.redmadrobottest.instagramcollage.activity.ImageActivity;
import com.redmadrobottest.instagramcollage.R;

public class CollageViewSendDialog extends DialogFragment {

    public static class Params {
		public static final String BitmapArray = "BitmapArray";
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.Collage);
        dialog.setPositiveButton(R.string.Send, ((ImageActivity) getActivity()).collageViewSendDialogButtonsClick);
        dialog.setNegativeButton(R.string.Cancel, ((ImageActivity) getActivity()).collageViewSendDialogButtonsClick);
        byte[] bitmapArray = getArguments().getByteArray(Params.BitmapArray);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_collage, null);
        ((ImageView) dialogView.findViewById(R.id.DC_Collage)).setImageBitmap(imageBitmap);
        dialog.setView(dialogView);

        return dialog.create();
    }

}
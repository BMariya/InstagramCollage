package com.redmadrobottest.instagramcollage.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class FileProvider extends ContentProvider {

	public static final class Params {
		public static final Uri CONTENT_URI = Uri.parse("content://com.redmadrobottest.instagramcollage.providers.FileProvider/");
		public static final String FileData = "FileData";
	}
	
	private byte[] fileData;
	
	@Override
	public boolean onCreate() {
		fileData = null;
		return(true);
	}
	
	@Override
	public String getType(Uri uri) {
		return getFileType(uri);
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		ParcelFileDescriptor[] pipe;
		try {
			pipe = ParcelFileDescriptor.createPipe();
			new TransferThread(new AutoCloseOutputStream(pipe[1])).start();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileNotFoundException("Could not open pipe for: " + uri.toString());
		}
		return(pipe[0]);
	}
	
	@Override
	public Uri insert(Uri uri, final ContentValues initialValues) {
        
		Thread oThread = new Thread (

                new Runnable() {
                    
                    public void run() {
                        fileData = (byte[]) initialValues.get(Params.FileData);
                    }
                    
                }

		);
        
		oThread.start();
		return uri;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		throw new RuntimeException("Operation not supported");
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		throw new RuntimeException("Operation not supported");
	}
	
	@Override
	public Cursor query(Uri url, String[] projection, String selection, String[] selectionArgs, String sort) {
		throw new RuntimeException("Operation not supported");
	}
	
	class TransferThread extends Thread {

		OutputStream out;

		TransferThread(OutputStream out) {
			this.out = out;
		}
        
		@Override
		public void run() {
            
			try {
				out.write(fileData);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
		}
	}

    public static String getFileType(Uri uri) {
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimeType == null) {
            return "png/*";
        } else {
            return mimeType;
        }
    }

}
package com.codegreen.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.codegreen.businessprocess.handler.Handler;
import com.codegreen.common.Task;
import com.codegreen.util.Constants;

public class DownloadImageTask extends Task {

	private String imageUrl;
	private Handler handler;
	private Bitmap bitmap;
	public DownloadImageTask(String imageUrl, Handler handler) {
		this.imageUrl = imageUrl;
		this.handler = handler;
	}
	@Override
	public void run() {
		InputStream is ;
		try {
			is = new URL(imageUrl).openStream();
			BufferedInputStream bis = new BufferedInputStream(is);  
			bitmap = BitmapFactory.decodeStream(bis);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		handler.handleCallback((Object)bitmap, Constants.REQ_DOWNLOADIMAGE, (byte)0);
		
	}

	@Override
	public void deAllocate() {

	}

}
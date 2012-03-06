package com.codegreen.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
		FileInputStream fileInputStream = null;
		try {
			if(imageUrl != null){
				Log.i("Downloading image:", "" + imageUrl);
				if(imageUrl.startsWith("http")){
					imageUrl = imageUrl.replaceAll(" ", "%20");
					is = new URL(imageUrl).openStream();
					BufferedInputStream bis = new BufferedInputStream(is);  
					bitmap = BitmapFactory.decodeStream(bis);
				}else{
					File file = new File(imageUrl);
					if(file.exists()){
						fileInputStream = new FileInputStream(file);
						bitmap = BitmapFactory.decodeStream(fileInputStream);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		handler.handleCallback((Object)bitmap, Constants.REQ_DOWNLOADIMAGE, (byte)0);
		
	}

	@Override
	public void deAllocate() {

	}

}

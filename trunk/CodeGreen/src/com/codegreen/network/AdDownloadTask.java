package com.codegreen.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.codegreen.businessprocess.handler.Handler;
import com.codegreen.common.Task;
import com.codegreen.util.Constants;

public class AdDownloadTask extends Task {

	private String thumbnailURL;
	private String mainURL;
	private Handler handler;
	private Bitmap[] bitmap;
	public AdDownloadTask(String thumbnailURL, String mainURL, Handler handler) {
		this.thumbnailURL = thumbnailURL;
		this.mainURL = mainURL;
		this.handler = handler;
		bitmap = new Bitmap[2];
	}
	@Override
	public void run() {
		InputStream is ;
		FileInputStream fileInputStream = null;
		try {
			if(thumbnailURL != null){
				if(thumbnailURL.startsWith("http")){
					thumbnailURL = thumbnailURL.replaceAll(" ", "%20");
					is = new URL(thumbnailURL).openStream();
					BufferedInputStream bis = new BufferedInputStream(is);  
					bitmap[0] = BitmapFactory.decodeStream(bis);
				}	
			}
			
			if(mainURL != null){
				if(mainURL.startsWith("http")){
					thumbnailURL = thumbnailURL.replaceAll(" ", "%20");
					is = new URL(thumbnailURL).openStream();
					BufferedInputStream bis = new BufferedInputStream(is);  
					bitmap[1]= BitmapFactory.decodeStream(bis);
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		handler.handleCallback((Object)bitmap, Constants.REQ_DOWNLOADADDIMAGE, (byte)0);
		
	}

	@Override
	public void deAllocate() {

	}

}

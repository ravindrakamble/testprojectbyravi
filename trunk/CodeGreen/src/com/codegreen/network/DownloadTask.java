package com.codegreen.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.util.Log;

import com.codegreen.businessprocess.handler.Handler;
import com.codegreen.businessprocess.objects.DownloadInfoDAO;
import com.codegreen.common.Task;
import com.codegreen.util.Constants;

public class DownloadTask extends Task{
	
	private DownloadInfoDAO downloadInfo;
	private Handler handler;
	private byte callID;

	public DownloadTask(DownloadInfoDAO downloadInfo, Handler handler, byte requestType){
		this.downloadInfo = downloadInfo;
		this.handler = handler;
		this.callID =  requestType;
	}
	@Override
	public void run() {
		if(downloadInfo != null && downloadInfo.getUrlToDownload() != null){
			InputStream inputStream = null;
			try {
				Log.e("Downloading :", downloadInfo.getUrlToDownload());
				URL url = new URL(downloadInfo.getUrlToDownload());
				
				inputStream = url.openStream();
				
				File downloadDir = new File("/sdcard/codegreen/" + downloadInfo.getType() );
				if(!downloadDir.exists()){
					downloadDir.mkdirs();
				}
				
				File downloadFile = new File("/sdcard/codegreen/" + downloadInfo.getType()+ "/" + downloadInfo.getFileName() );
				downloadFile.createNewFile();
				
				FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
				byte[] fileData = new byte[200];
				while(inputStream.read(fileData) != -1){
					fileOutputStream.write(fileData);
				}
				fileOutputStream.flush();
				fileOutputStream.close();
				inputStream.close();
				
				handler.handleCallback(downloadFile.getAbsolutePath(), callID, (byte)0);
				
			} catch (Exception e) {
				e.printStackTrace();
				handler.handleCallback(null, callID, Constants.ERR_NETWORK_FAILURE);
			}
		}
	}

	@Override
	public void deAllocate() {
		
	}

}

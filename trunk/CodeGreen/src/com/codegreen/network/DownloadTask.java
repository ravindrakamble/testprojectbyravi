package com.codegreen.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Vector;

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
				String downloadURL = downloadInfo.getUrlToDownload();
				downloadURL = downloadURL.replaceAll(" ", "%20");
				Log.e("Downloading :", downloadURL);
				URL url = new URL(downloadURL);

				String filePath = null;

				if(downloadURL.contains("youtube")){
					//filePath = downloadYouTubeURL(downloadURL);
					handler.handleCallback(downloadURL, callID, (byte)0);
				}else{
					inputStream = url.openStream();

					File downloadDir = new File("/sdcard/codegreen/" + downloadInfo.getType() );
					if(!downloadDir.exists()){
						downloadDir.mkdirs();
					}

					File downloadFile = new File("/sdcard/codegreen/" + downloadInfo.getType()+ "/" + downloadInfo.getFileName() );
					
					if(downloadFile.exists()){
						downloadFile.delete();
					}
					downloadFile = new File("/sdcard/codegreen/" + downloadInfo.getType()+ "/" + downloadInfo.getFileName() );
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
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.handleCallback(null, callID, Constants.ERR_NETWORK_FAILURE);
			}
		}else{
			handler.handleCallback(null, callID, (byte)0);
		}
	}

	@Override
	public void deAllocate() {

	}

	private String downloadYouTubeURL(String uTubeUrl){
		int begin = 0;
		int end = 0;
		String tmpstr;
		try{
			URL url=new URL(uTubeUrl); 
			HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
			con.setRequestMethod("GET"); 
			InputStream stream=con.getInputStream(); 
			InputStreamReader reader=new InputStreamReader(stream); 
			StringBuffer buffer=new StringBuffer(); 
			char[] buf=new char[262144];                 
			int chars_read; 
			while ((chars_read = reader.read(buf, 0, 262144)) != -1) { 
				buffer.append(buf, 0, chars_read); 
			} 
			tmpstr = buffer.toString(); 
			System.out.println(tmpstr);
			begin  = tmpstr.indexOf("url_encoded_fmt_stream_map="); 
			end = tmpstr.indexOf("&", begin + 27); 
			if (end == -1) { 
				end = tmpstr.indexOf("\"", begin + 27); 
			} 
			tmpstr =URLDecoder.decode(tmpstr.substring(begin + 27, end)); 
			System.out.println(tmpstr);
		} catch (MalformedURLException e) { 
			throw new RuntimeException(); 
		} catch (IOException e) { 
			throw new RuntimeException(); 
		} 

		Vector url_encoded_fmt_stream_map = new Vector(); 
		begin = 0; 
		end   = tmpstr.indexOf(","); 

		while (end != -1) { 
			url_encoded_fmt_stream_map.addElement(tmpstr.substring(begin, end)); 
			begin = end + 1; 
			end   = tmpstr.indexOf(",", begin); 
		} 

		url_encoded_fmt_stream_map.addElement(tmpstr.substring(begin, tmpstr.length())); 
		String result = ""; 
		Enumeration url_encoded_fmt_stream_map_enum = url_encoded_fmt_stream_map.elements(); 
		while (url_encoded_fmt_stream_map_enum.hasMoreElements()) { 
			tmpstr = (String)url_encoded_fmt_stream_map_enum.nextElement(); 
			System.out.println(tmpstr);
			begin = tmpstr.indexOf("itag="); 
			if (begin != -1) { 
				end = tmpstr.indexOf("&", begin + 5); 

				if (end == -1) { 
					end = tmpstr.length(); 
				} 

				int fmt = Integer.parseInt(tmpstr.substring(begin + 5, end)); 

				if (fmt >= 35) { 
					begin = tmpstr.indexOf("url="); 
					if (begin != -1) { 
						end = tmpstr.indexOf("&", begin + 4); 
						if (end == -1) { 
							end = tmpstr.length(); 
						} 
						result = URLDecoder.decode(tmpstr.substring(begin + 4, end)); 
						if(tmpstr.indexOf("mp4") != -1){
							break; 
						}
					} 
				} 
			} 
		}          
		try { 
			URL u = new URL(result); 
			HttpURLConnection c = (HttpURLConnection) u.openConnection(); 
			c.setRequestMethod("GET"); 

			c.setDoOutput(true); 
			c.connect(); 

			File downloadDir = new File("/sdcard/codegreen/" + downloadInfo.getType() );
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}

			File downloadFile = new File("/sdcard/codegreen/" + downloadInfo.getType()+ "/" + downloadInfo.getFileName() );
			downloadFile.createNewFile();
			FileOutputStream f=new FileOutputStream(downloadFile); 

			InputStream in=c.getInputStream(); 
			byte[] buffer=new byte[1024]; 
			int sz = 0; 
			while ( (sz = in.read(buffer)) > 0 ) { 
				f.write(buffer,0, sz); 
			} 
			f.close(); 
			System.out.println("File downloaded:" + downloadFile.getAbsolutePath());
			tmpstr = downloadFile.getAbsolutePath();
		} catch (MalformedURLException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return tmpstr;
	} 

}

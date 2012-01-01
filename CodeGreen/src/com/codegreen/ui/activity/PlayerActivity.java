package com.codegreen.ui.activity;

import com.codegreen.R;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;


public class PlayerActivity extends Activity {
	/** Called when the activity is first created. */
	public static boolean isAudio=false;
	public static String streamUrl;

	private ProgressBar progressBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainvideo);

		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.VISIBLE);
		ImageView audioImg=(ImageView)findViewById(R.id.audioImg);
		if(isAudio==true)
		{
			audioImg.setVisibility(View.VISIBLE);
			videoPlayer("/sdcard/a.3gp", "a", true);
			
		}else{
			audioImg.setVisibility(View.GONE);
			videoPlayer("/sdcard/a.3gp", "a", true);
		}

	}
	public void videoPlayer(String path, String fileName, boolean autoplay){
		//get current window information, and set format, set it up differently, if you need some special effects
		getWindow().setFormat(PixelFormat.TRANSLUCENT); 
		//the VideoView will hold the video
		VideoView videoHolder = (VideoView)findViewById(R.id.vid);
		//MediaController is the ui control howering above the video (just like in the default youtube player).
		videoHolder.setMediaController(new MediaController(this)); 
		System.out.println(PlayerActivity.streamUrl);
		//assigning a video file to the video holder
		videoHolder.setVideoURI(Uri.parse(PlayerActivity.streamUrl)); 
		//get focus, before playing the video.
		videoHolder.requestFocus(); 


		if(true){
			try{
				videoHolder.start();
				progressBar.setVisibility(View.INVISIBLE);
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}

	}
	@Override
	protected void onStart()
	{
		super.onStart(); 
		System.out.println("start session");
	}
	@Override
	protected void onStop()
	{
		super.onStop();
		System.out.println("end session");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}
}

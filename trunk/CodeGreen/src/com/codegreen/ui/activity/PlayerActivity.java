package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.listener.Updatable;
import com.codegreen.util.Constants;
import com.codegreen.util.Utils;
import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;


public class PlayerActivity extends Activity implements Updatable {

	/** Called when the activity is first created. */
	public static boolean isAudio=false;
	public static String streamUrl;
	private ProgressBar progressBar;
	ArticleDAO articleDetails;
	String strSelectedArticleType = Constants.ARTCLETYPE_TEXT;
	String strSelectedArticleID = "";
	private final String TAG = "PlayerActivity";
	ImageView audioImg = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mainvideo);

		if(getIntent() != null){
			strSelectedArticleType = getIntent().getStringExtra(Constants.CURRENT_ARTICLE_TYPE);
			strSelectedArticleID = getIntent().getStringExtra("ArticleID");
		}
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.VISIBLE);
		audioImg=(ImageView)findViewById(R.id.audioImg);

		if(strSelectedArticleType != null && strSelectedArticleID != null){
			getArticleDetails();
		}
	}


	/**
	 * WS call to get article details
	 */
	private void getArticleDetails(){
		HttpHandler httpHandler =  HttpHandler.getInstance();
		ArticleDAO articleDAO = new ArticleDAO();
		articleDAO.setArticleID(strSelectedArticleID);
		articleDAO.setType(strSelectedArticleType);
		articleDAO.setCategoryID(Constants.CURRENT_CATEGORY_TYPE);
		httpHandler.handleEvent(articleDAO, Constants.REQ_GETARTICLEDETAILS, this);
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
				e.printStackTrace();
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


	@Override
	public void update(byte errorCode, byte callID, Object obj) {
	}


	@Override
	public void update(ENUM_PARSERRESPONSE updateData, final byte callId) {
		// TODO Auto-generated method stub
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG , "--------Response Received-------PARSERRESPONSE_SUCCESS");


			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressBar.setVisibility(View.GONE);
					if(callId == Constants.REQ_GETARTICLEDETAILS){
						String strDetails = "";
						articleDetails = (ArticleDAO) CacheManager.getInstance().get(Constants.C_ARTICLE_DETAILS);
						if(articleDetails != null){
							if(articleDetails.getUrl() != null && !articleDetails.getUrl().equals("")){

								if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO))
								{
									PlayerActivity.streamUrl = articleDetails.getUrl();
									audioImg.setVisibility(View.VISIBLE);
									videoPlayer("/sdcard/a.3gp", "a", true);

								}else{
									PlayerActivity.streamUrl = articleDetails.getUrl();
									audioImg.setVisibility(View.GONE);
									videoPlayer("/sdcard/a.3gp", "a", true);

								}
								//txt_reviews.setVisibility(View.VISIBLE);
							}else if(callId == Constants.REQ_GETREVIEWS){
								//txt_reviews.setVisibility(View.VISIBLE);

								//txt_reviews.setText();
							}else if(callId == Constants.REQ_SUBMITREVIEW){
								Utils.displayMessage(PlayerActivity.this, getString(R.string.review_submitted));
							}
						}
					}
				}
			});
		}
	}
}

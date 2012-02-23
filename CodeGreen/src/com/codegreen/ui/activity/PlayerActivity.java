package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.dialog.ReviewDialog;
import com.codegreen.ui.dialog.ShareDialog;
import com.codegreen.util.Constants;
import com.codegreen.util.Utils;
import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class PlayerActivity extends Activity implements Updatable {

	/** Called when the activity is first created. */
	public static boolean isAudio=false;
	public static String streamUrl;
	//private ProgressBar progressBar;
	ArticleDAO articleDetails;
	String strSelectedArticleType = Constants.ARTCLETYPE_TEXT;
	String strSelectedArticleID = "";
	private final String TAG = "PlayerActivity";
	ImageView audioImg = null;
	ProgressDialog progressDialog;
	private TextView txtTitle;
	private String title = "";

	private String locationOfData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		if(getIntent() != null){
			strSelectedArticleType = getIntent().getStringExtra(Constants.CURRENT_ARTICLE_TYPE);
			strSelectedArticleID = getIntent().getStringExtra("ArticleID");
			locationOfData =  getIntent().getStringExtra("savedarticle");
			title = getIntent().getStringExtra("savedarticleTitle");
		}
		if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO))
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.mainvideo);
		audioImg=(ImageView)findViewById(R.id.audioImg);
		txtTitle = (TextView)findViewById(R.id.player_title_view);
		if(strSelectedArticleType != null && strSelectedArticleID != null){
			if(locationOfData == null){
				showDialog(Constants.DIALOG_PROGRESS);
				getArticleDetails();
			}else{
				txtTitle.setText(title);
				videoPlayer(locationOfData, "", true);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.DIALOG_PROGRESS:
			if(progressDialog == null){
				showProgressBar();
			}
			return progressDialog;
		default:
			break;
		}
		return null;

	}


	private void showProgressBar(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO))
				progressDialog.setMessage("Downloading video, please wait...");
			else
				progressDialog.setMessage("Downloading Audio, please wait...");
			progressDialog.setIcon(android.R.id.icon);
			progressDialog.setCancelable(false);
			progressDialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
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
		if(locationOfData == null){
			videoHolder.setVideoURI(Uri.parse(PlayerActivity.streamUrl)); 
		}else{
			videoHolder.setVideoURI(Uri.parse(locationOfData)); 
		}
		//get focus, before playing the video.
		videoHolder.requestFocus(); 


		if(true){
			try{
				videoHolder.start();
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
	public void update(ENUM_PARSERRESPONSE updateData, final byte callId,byte errorCode) {

		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG , "--------Response Received-------PARSERRESPONSE_SUCCESS");


			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					removeDialog(Constants.DIALOG_PROGRESS);
					if(callId == Constants.REQ_GETARTICLEDETAILS){

						String strDetails = "";
						articleDetails = (ArticleDAO) CacheManager.getInstance().get(Constants.C_ARTICLE_DETAILS);
						if(articleDetails != null){
							txtTitle.setText(articleDetails.getTitle());
							
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
							}else{
								Utils.displayMessage(getApplicationContext(), "No Video URL Found");
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								finish();
							}
						}
					}else if(callId == Constants.REQ_GETREVIEWS){
						//txt_reviews.setVisibility(View.VISIBLE);

						//txt_reviews.setText();
					}else if(callId == Constants.REQ_SUBMITREVIEW){
						Utils.displayMessage(PlayerActivity.this, getString(R.string.review_submitted));
					}

				}
			});
		}else if(errorCode == Constants.ERR_NETWORK_FAILURE){
			Toast.makeText(this, "No Network Available.", Toast.LENGTH_LONG).show();	
		}

	}
}

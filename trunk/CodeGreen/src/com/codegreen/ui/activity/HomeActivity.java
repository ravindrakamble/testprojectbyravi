package com.codegreen.ui.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.adaptor.HomeScreenAdapter;
import com.codegreen.util.Constants;

public class HomeActivity extends ListActivity implements Updatable{

	HomeScreenAdapter mAdapter = null;
	String TAG = "HomeActivity";
	private static String CurrentTabSelected = Constants.ARTCLETYPE_TEXT;

	private ProgressBar progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		progressBar = (ProgressBar)findViewById(R.id.header_progress_circular);
		initWidgets();
		getArticleData(Constants.ARTICAL_TYPE_IMAGE); 
	}


	/**
	 * Initialize variables
	 */
	private void initWidgets() {
		Button btn_text = (Button)findViewById(R.id.text_btn);
		Button btn_audio = (Button)findViewById(R.id.audio_btn);
		Button btn_vedio = (Button)findViewById(R.id.vedio_btn);
		Button btn_image = (Button)findViewById(R.id.image_btn);

		// set click listener
		btn_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getArticleData(Constants.ARTICAL_TYPE_TEXT);
			}
		});
		btn_audio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getArticleData(Constants.ARTICAL_TYPE_AUDIO);
			}
		});

		btn_vedio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getArticleData(Constants.ARTICAL_TYPE_VEDIO);
			}
		});
		btn_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getArticleData(Constants.ARTICAL_TYPE_IMAGE);
			}
		});
	}


	/**
	 * WS to get Articles 
	 * @param articleType
	 */
	private void getArticleData(String articleType){
		try {
			HttpHandler httpHandler =  HttpHandler.getInstance();
			//Cancel previous request;
			httpHandler.cancelRequest();

			//Start progressbar
			progressBar.setVisibility(View.VISIBLE);
			
			//Prepare data for new request
			ArticleDAO articleDAO = new ArticleDAO();
			articleDAO.setType(articleType);
			articleDAO.setLastArticlePublishingDate("11/25/2011");

			//Send request
			httpHandler.handleEvent(articleDAO, Constants.REQ_GETARTICLESBYTYPE, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(byte errorCode, byte callID, Object obj) {

	}

	@Override
	public void update(Constants.ENUM_PARSERRESPONSE updateData) {

		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");
			if(mAdapter == null){ 
				mAdapter = new HomeScreenAdapter(this);
				setListAdapter(mAdapter);
			}else{
				mAdapter.notifyDataSetChanged();
				mAdapter.notifyDataSetInvalidated();
			}
		}else
			Log.e(TAG, "--------Response Received-------ENUM_PARSERRESPONSE.PARSERRESPONSE_FAILURE");
		
		//Send message to remove progress bar
		Message msg = new Message();
		msg.what = Constants.PROGRESS_INVISIBLE;
		uiUpdator.sendMessage(msg);
	} 

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ArticleDAO articleEntry = ((ArticleDAO)getListAdapter().getItem(position));
		// Launch details screen
		Intent intent = new Intent(getApplicationContext(), ArticleDetailsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constants.CURRENT_ARTICLE_TYPE, CurrentTabSelected);
		intent.putExtra("ArticleID", articleEntry.getArticleID());
		startActivity(intent);
	} 

	private Handler uiUpdator = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constants.PROGRESS_VISIBLE:
				progressBar.setVisibility(View.VISIBLE);
				break;
				
			case Constants.PROGRESS_INVISIBLE:
				progressBar.setVisibility(View.INVISIBLE);
				break;
			}
		};
	};
}

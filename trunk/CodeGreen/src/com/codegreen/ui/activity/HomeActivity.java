package com.codegreen.ui.activity;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.listener.MediaDialogListner;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.adaptor.HomeScreenAdapter;
import com.codegreen.ui.dialog.MediaDialog;
import com.codegreen.ui.dialog.ReviewDialog;
import com.codegreen.util.Constants;
import com.codegreen.util.Utils;
import com.facebook.android.Util;

public class HomeActivity extends ListActivity implements Updatable, MediaDialogListner{

	ReviewDialog reviewDialog;
	HomeScreenAdapter mAdapter = null;
	String TAG = "HomeActivity";
	private static String CurrentTabSelected = Constants.ARTCLETYPE_TEXT;



	private static final int MENU_OPTION_SAVED = 0x01;
	private static final int MENU_OPTION_SEARCH = 0x02;
	private static final int MENU_OPTION_INFO = 0x04;

	TextView mBtnGreenBasic = null;
	TextView mBtnDesignArcht = null;
	TextView mBtnScience = null;
	TextView mBtnTransport = null;
	TextView mBtnBusiness = null;
	TextView mBtnPolitics = null;
	TextView mBtnFood = null;
	TextView mBtnLatest = null;

	private static int CURRENT_SELECTED_CATEGORY = 0;
	private static String CURRENT_SELECTED_MEDIA = "ALL";
	private TextView mNoItems = null;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		initWidgets();
		CURRENT_SELECTED_CATEGORY = 0;
		CURRENT_SELECTED_MEDIA = "ALL";
		getArticleData("");
		getListView().setCacheColorHint(0);
	}

	private void showProgressBar(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading, please wait...");
			progressDialog.setIcon(android.R.id.icon);
			progressDialog.setCancelable(false);
			progressDialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
		}
	}

	ImageView btn_left_arrow = null;
	ImageView btn_right_arrow = null;

	/**
	 * Initialize variables
	 */
	private void initWidgets() {
		mBtnGreenBasic = (TextView)findViewById(R.id.btn_green_basics);
		mBtnDesignArcht = (TextView)findViewById(R.id.btn_design);
		mBtnScience = (TextView)findViewById(R.id.btn_secience);
		mBtnTransport = (TextView)findViewById(R.id.btn_transport);
		mBtnBusiness = (TextView)findViewById(R.id.btn_business);
		mBtnPolitics = (TextView)findViewById(R.id.btn_politics);
		mBtnFood = (TextView)findViewById(R.id.btn_food);
		mNoItems =(TextView) findViewById(android.R.id.empty);
		btn_left_arrow = (ImageView)findViewById(R.id.btn_left_arrow);
		btn_right_arrow = (ImageView)findViewById(R.id.btn_right_arrow);

		// set default as off 
		btn_left_arrow.setBackgroundResource(R.drawable.left_arrow_off);


		mBtnGreenBasic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_left_arrow.setBackgroundResource(R.drawable.left_arrow_on);
				refreshViews();
				mBtnGreenBasic.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 1;
				getArticleData("");
			}
		});
		mBtnDesignArcht.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				refreshViews();
				mBtnDesignArcht.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 2;
				getArticleData("");
			}
		});
		mBtnScience.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnScience.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 3;
				getArticleData("");
			}
		});
		mBtnTransport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnTransport.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 4;
				getArticleData("");
			}
		});
		mBtnBusiness.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnBusiness.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 5;
				getArticleData("");
			}
		});
		mBtnPolitics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_right_arrow.setBackgroundResource(R.drawable.right_arrow_on);
				refreshViews();
				mBtnPolitics.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 6;
				getArticleData("");
			}
		});
		mBtnFood.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_right_arrow.setBackgroundResource(R.drawable.right_arrow_off);
				refreshViews();
				mBtnFood.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 7;
				getArticleData("");
			}
		});
		final ImageView btn_Media = (ImageView) findViewById(R.id.btn_media);
		btn_Media.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMediaOptions();
			}
		});

		mBtnLatest = (TextView)findViewById(R.id.btn_latest);

		mBtnLatest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_left_arrow.setBackgroundResource(R.drawable.left_arrow_off);
				CURRENT_SELECTED_CATEGORY = 0;
				CURRENT_SELECTED_MEDIA = "ALL";
				refreshViews();
				mBtnLatest.setBackgroundResource(R.drawable.scrollbutton_off);
				getArticleData("");
			}
		});
		refreshViews();
		mBtnLatest.setBackgroundResource(R.drawable.scrollbutton_off);
	}


	private void refreshViews(){
		mBtnBusiness.setTextColor(Color.WHITE);
		mBtnDesignArcht.setTextColor(Color.WHITE);
		mBtnFood.setTextColor(Color.WHITE);
		mBtnGreenBasic.setTextColor(Color.WHITE);
		mBtnLatest.setTextColor(Color.WHITE);
		mBtnPolitics.setTextColor(Color.WHITE);
		mBtnScience.setTextColor(Color.WHITE);
		mBtnTransport.setTextColor(Color.WHITE);
		mBtnBusiness.setBackgroundDrawable(null);
		mBtnDesignArcht.setBackgroundDrawable(null);
		mBtnFood.setBackgroundDrawable(null);
		mBtnGreenBasic.setBackgroundDrawable(null);
		mBtnLatest.setBackgroundDrawable(null);
		mBtnPolitics.setBackgroundDrawable(null);
		mBtnScience.setBackgroundDrawable(null);
		mBtnTransport.setBackgroundDrawable(null);
	}


	private void showMediaOptions(){
		showDialog(Constants.DIALOG_MEDIA);
	}



	private void searchArticles(int type){
		try {
			HttpHandler httpHandler =  HttpHandler.getInstance();
			//Cancel previous request;
			httpHandler.cancelRequest();

			//Start progress bar
			showDialog(Constants.DIALOG_PROGRESS);
			mNoItems.setVisibility(View.GONE);
			//Prepare data for new request
			ArticleDAO articleDAO = new ArticleDAO();
			articleDAO.setTitle("");
			articleDAO.setType("");
			articleDAO.setCategoryID(String.valueOf(type));

			//Send request
			httpHandler.setApplicationContext(getApplicationContext());
			httpHandler.handleEvent(articleDAO, Constants.REQ_SEARCHARTICLES, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		// Restore the default value of media type
		CURRENT_SELECTED_MEDIA = "ALL";
	}


	/**
	 * WS to get Articles 
	 * @param articleType
	 */
	private void getArticleData(String articleType){
		try {
			if(Utils.isNetworkAvail(getApplicationContext())){
				HttpHandler httpHandler =  HttpHandler.getInstance();
				//Cancel previous request;
				httpHandler.cancelRequest();

				//Start progressbar
				showDialog(Constants.DIALOG_PROGRESS);
				mNoItems.setVisibility(View.GONE);
				//Prepare data for new request
				ArticleDAO articleDAO = new ArticleDAO();
				articleDAO.setType(articleType);
				//date format should be mm/dd/yyyy
				articleDAO.setLastArticlePublishingDate(Utils.getCurrentDate()); // TBD 
				articleDAO.setCategoryID(String.valueOf(CURRENT_SELECTED_CATEGORY));
				//Send request
				httpHandler.setApplicationContext(getApplicationContext());
				httpHandler.handleEvent(articleDAO, Constants.REQ_GETARTICLESBYTYPE, this);
			}else{
				Toast.makeText(getApplicationContext(),"No Network Available", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(byte errorCode, byte callID, Object obj) {

	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try{
			menu.removeGroup(0);
			menu.add(0, MENU_OPTION_SAVED,0 , "Saved Items").setIcon(android.R.drawable.ic_menu_gallery);
			menu.add(0, MENU_OPTION_SEARCH,0 , "Search").setIcon(android.R.drawable.ic_menu_search);
			menu.add(0, MENU_OPTION_INFO,0 , "Info").setIcon(android.R.drawable.ic_menu_help);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static final int ID_SEARCH =0x40;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_OPTION_SAVED:
			launchSavedActivity();
			break;
		case MENU_OPTION_SEARCH:
			launchSearchActivity();
			break; 
		case MENU_OPTION_INFO:
			Toast.makeText(getApplicationContext(),"Implimentation is in Progress...", Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.DIALOG_REVIEW:
			ReviewDialog reviewDialog = new ReviewDialog(this, (ArticleDAO)mAdapter.getItem(0));
			return reviewDialog;
		case Constants.DIALOG_PROGRESS:
			if(progressDialog == null){
				showProgressBar();
			}
			return progressDialog;
		case Constants.DIALOG_MEDIA:
			MediaDialog media = new MediaDialog(this);
			media.registerListner(this);
			return media;
		default:
			break;
		}
		return null;
	}

	@Override
	public void update(Constants.ENUM_PARSERRESPONSE updateData, final byte reqID, byte errorCode) {
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");

			if(mAdapter == null){
				runOnUiThread(new Runnable() { 
					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						int sizeOfData = 0;
						if(reqID == Constants.REQ_GETARTICLESBYTYPE)
							sizeOfData = ((ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ARTICLES)).size();
						else 
							sizeOfData = ((ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_SEARCH_ARTICLES)).size();

						if(sizeOfData == 0)
							mNoItems.setVisibility(View.VISIBLE);

						mAdapter = new HomeScreenAdapter(HomeActivity.this);
						mAdapter.setRequestID(reqID);
						setListAdapter(mAdapter); 
					}
				});

			}else{
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						int sizeOfData = 0;
						if(reqID == Constants.REQ_GETARTICLESBYTYPE)
							sizeOfData = ((ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ARTICLES)).size();
						else 
							sizeOfData = ((ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_SEARCH_ARTICLES)).size();

						if(sizeOfData == 0)
							mNoItems.setVisibility(View.VISIBLE);
						mAdapter.setRequestID(reqID);
						mAdapter.notifyDataSetChanged();
						mAdapter.notifyDataSetInvalidated();
					}
				});
			}
		}else if(errorCode == Constants.ERR_NETWORK_FAILURE){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "No Network Available.", Toast.LENGTH_SHORT).show();
				}
			});

		}
		else{
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "No news data found", Toast.LENGTH_SHORT).show();
				}
			});
		}

		//Send message to remove progress bar
		Message msg = new Message();
		msg.what = Constants.PROGRESS_INVISIBLE;
		uiUpdator.sendMessage(msg);
	} 

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(Utils.isNetworkAvail(getApplicationContext())){
			ArticleDAO articleEntry = ((ArticleDAO)getListAdapter().getItem(position));
			Constants.CURRENT_INDEX = position;
			// Launch details screen
			Intent intent = new Intent(getApplicationContext(), ArticleDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Constants.CURRENT_ARTICLE_TYPE, articleEntry.getType());
			intent.putExtra("ArticleID", articleEntry.getArticleID());
			intent.putExtra("desc", articleEntry.getShortDescription());
			intent.putExtra("thumburl", articleEntry.getThumbUrl());
			startActivity(intent);
		}else{
			Toast.makeText(getApplicationContext(),"No Network Available", Toast.LENGTH_SHORT).show();
		}
	} 


	private Handler uiUpdator = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constants.PROGRESS_VISIBLE:
				showDialog(Constants.DIALOG_PROGRESS);
				mNoItems.setVisibility(View.GONE);
				break;

			case Constants.PROGRESS_INVISIBLE:
				removeDialog(Constants.DIALOG_PROGRESS);
				break;
			}
		};
	};


	/**
	 *  Launch SearchCallhistoryActivity 
	 * @param filterStr
	 */
	private void launchSearchActivity(){
		try{
			Context cxt = getApplicationContext();
			Intent intent = new Intent(cxt, SearchActivity.class);     		
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("Selected_Category", ""+CURRENT_SELECTED_CATEGORY);
			intent.putExtra("Selected_Media", CURRENT_SELECTED_MEDIA);
			startActivity(intent);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Launch SearchCallhistoryActivity 
	 * @param filterStr
	 */
	private void launchSavedActivity(){
		try{
			Context cxt = getApplicationContext();
			Intent intent = new Intent(cxt, SavedArticlesActivity.class);     		
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleDialogClick(int item) {
		if(item == 0){//All
			CURRENT_SELECTED_MEDIA = "ALL";
		}
		else if(item == 1){//Multimedia
			CURRENT_SELECTED_MEDIA =  "multimedia";
		}	
		else if(item == 2){//text
			CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_TEXT;
		}
		getArticleData(CURRENT_SELECTED_MEDIA);
	}
}

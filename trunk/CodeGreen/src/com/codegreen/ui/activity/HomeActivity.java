package com.codegreen.ui.activity;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
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

public class HomeActivity extends ListActivity implements Updatable, MediaDialogListner{

	ReviewDialog reviewDialog;
	HomeScreenAdapter mAdapter = null;
	String TAG = "HomeActivity";
	private static String CurrentTabSelected = Constants.ARTCLETYPE_TEXT;
	
	

	private static final int MENU_OPTION_SAVED = 0x01;
	private static final int MENU_OPTION_SEARCH = 0x02;
	private static final int MENU_OPTION_SHARE = 0x03;
	private static final int MENU_OPTION_INFO = 0x04;

	TextView mBtnGreenBasic = null;
	TextView mBtnDesignArcht = null;
	TextView mBtnScience = null;
	TextView mBtnTransport = null;
	TextView mBtnBusiness = null;
	TextView mBtnPolitics = null;
	TextView mBtnFood = null;
	TextView mBtnLatest = null;
	//LinearLayout progress_Lay = null;

	private static int CURRENT_SELECTED_CATEGORY = 1;
	private static String CURRENT_SELECTED_MEDIA = "";
	private TextView mNoItems = null;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initWidgets();
		// Call the data by category default
		searchArticles(Constants.GREEN_BASICS);
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
		mBtnGreenBasic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnGreenBasic.setBackgroundResource(R.drawable.selectedbutton);
				CURRENT_SELECTED_CATEGORY = 1;
				searchArticles(Constants.GREEN_BASICS);
			}
		});
		mBtnDesignArcht.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnDesignArcht.setBackgroundResource(R.drawable.selectedbutton);
				CURRENT_SELECTED_CATEGORY = 2;
				searchArticles(Constants.DESIGN_AND_ARCHITECTURE);
			}
		});
		mBtnScience.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnScience.setBackgroundResource(R.drawable.selectedbutton);
				CURRENT_SELECTED_CATEGORY = 3;
				searchArticles(Constants.SCIENCE_ANDECHNOLOGY);
			}
		});
		mBtnTransport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnTransport.setBackgroundResource(R.drawable.selectedbutton);
				CURRENT_SELECTED_CATEGORY = 4;
				searchArticles(Constants.TRANSPORT);
			}
		});
		mBtnBusiness.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnBusiness.setBackgroundResource(R.drawable.selectedbutton);
				CURRENT_SELECTED_CATEGORY = 5;
				searchArticles(Constants.BUSINESS);
			}
		});
		mBtnPolitics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnPolitics.setBackgroundResource(R.drawable.selectedbutton);
				CURRENT_SELECTED_CATEGORY = 6;
				searchArticles(Constants.POLITICS);
			}
		});
		mBtnFood.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnFood.setBackgroundResource(R.drawable.selectedbutton);
				CURRENT_SELECTED_CATEGORY = 7;
				searchArticles(Constants.FOOD_AND_HEALTH);
			}
		});
		final ImageView btn_Media = (ImageView) findViewById(R.id.btn_media);
		btn_Media.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMediaOptions();
			}
		});
		
		btn_Media.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					btn_Media.setBackgroundResource(R.drawable.norgie_off);
				}else{
					btn_Media.setBackgroundResource(R.drawable.norgie_on);
				}
				return false;
			}
		});
		mBtnLatest = (TextView)findViewById(R.id.btn_latest);
		mBtnLatest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshViews();
				mBtnLatest.setBackgroundResource(R.drawable.selectedbutton);
				searchArticles(0);
			}
		});
	}

	/**
	 * refresh image backgrounds 
	 */
	private void refreshViews() {
		mBtnGreenBasic.setBackgroundDrawable(null);
		mBtnDesignArcht.setBackgroundDrawable(null);
		mBtnScience.setBackgroundDrawable(null);
		mBtnTransport.setBackgroundDrawable(null);
		mBtnBusiness.setBackgroundDrawable(null);
		mBtnPolitics.setBackgroundDrawable(null);
		mBtnLatest.setBackgroundDrawable(null);
		mBtnFood.setBackgroundDrawable(null);
	}


	private void showMediaOptions(){
		
		showDialog(Constants.DIALOG_MEDIA);
		/*final CharSequence[] items = {"Text", "Image", "Audio", "Video"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Media Options :");
		builder.setIcon(android.R.drawable.ic_media_rew);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if(item == 0){
					getArticleData(Constants.ARTICAL_TYPE_TEXT);
					CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_TEXT;
				}
				else if(item == 2){
					getArticleData(Constants.ARTICAL_TYPE_AUDIO);
					CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_AUDIO;
				}
				else if(item == 3){
					getArticleData(Constants.ARTICAL_TYPE_VEDIO);
					CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_VEDIO;
				}
				else if(item == 1){
					CURRENT_SELECTED_MEDIA =  Constants.ARTICAL_TYPE_IMAGE;
					getArticleData(Constants.ARTICAL_TYPE_IMAGE);
				}
			}
		});
		AlertDialog alert = builder.create();

		alert.show();
		
*/	}



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
		CURRENT_SELECTED_MEDIA = "";
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
			//menu.add(0, MENU_OPTION_SHARE,0 , "Share").setIcon(android.R.drawable.ic_menu_share);
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

			/*case MENU_OPTION_SHARE:
			showDialog(Constants.DIALOG_SHARE);
			break;
			 */
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
			Toast.makeText(this, "No Network Available.", Toast.LENGTH_LONG).show();	
		}
		else{
			Toast.makeText(this, "No news data found", Toast.LENGTH_LONG).show();
		}

		//Send message to remove progress bar
		Message msg = new Message();
		msg.what = Constants.PROGRESS_INVISIBLE;
		uiUpdator.sendMessage(msg);
	} 

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ArticleDAO articleEntry = ((ArticleDAO)getListAdapter().getItem(position));

		Constants.CURRENT_INDEX = position;
		// Launch details screen
		//if(articleEntry != null && articleEntry.getType().equalsIgnoreCase(Constants.ARTCLETYPE_IMAGE)||  articleEntry != null && articleEntry.getType().equalsIgnoreCase(Constants.ARTCLETYPE_TEXT)){
		Intent intent = new Intent(getApplicationContext(), ArticleDetailsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constants.CURRENT_ARTICLE_TYPE, articleEntry.getType());
		intent.putExtra("ArticleID", articleEntry.getArticleID());
		startActivity(intent);
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


	//@Override
	/*public boolean onKeyDown(int keyCode, KeyEvent event){
		try{
			if(keyCode == KeyEvent.KEYCODE_SEARCH){
				launchSearchActivity();
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}    		
		return false;
	}*/

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
		if(item == 0){
			getArticleData(Constants.ARTICAL_TYPE_TEXT);
			CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_TEXT;
		}
		else if(item == 2){
			getArticleData(Constants.ARTICAL_TYPE_AUDIO);
			CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_AUDIO;
		}
		else if(item == 3){
			getArticleData(Constants.ARTICAL_TYPE_VEDIO);
			CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_VEDIO;
		}
		else if(item == 1){
			CURRENT_SELECTED_MEDIA =  Constants.ARTICAL_TYPE_IMAGE;
			getArticleData(Constants.ARTICAL_TYPE_IMAGE);
		}		
	}
}

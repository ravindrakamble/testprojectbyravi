package com.codegreen.ui.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
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

public class HomeActivity extends ListActivity implements Updatable, MediaDialogListner{

	ReviewDialog reviewDialog;
	HomeScreenAdapter mAdapter = null;
	String TAG = "HomeActivity";
	private static String CurrentTabSelected = Constants.ARTCLETYPE_TEXT;



	private static final int MENU_OPTION_SAVED = 0x01;
	private static final int MENU_OPTION_SEARCH = 0x02;
	private static final int MENU_OPTION_INFO = 0x04;
	private static final int MENU_OPTION_REFRESH = 0x05;

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
	private static int CURRENT_SELECTED_ARTICLE = 0;
	private TextView mNoItems = null;
	private ProgressDialog progressDialog;
	ArrayList<ArticleDAO> advertiseData = null;
	ImageView addsImage = null;
	Timer adTimer;
	TimerTask adTask;
	boolean isLoadMoreArticles = false;
	View footerView  = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		WindowManager wm = getWindowManager(); 
		Display display = wm.getDefaultDisplay();
		Constants.SCREEN_WIDTH = display.getWidth();

		 initWidgets();
		 CURRENT_SELECTED_CATEGORY = 0;
		 CURRENT_SELECTED_MEDIA = "ALL";
		 CURRENT_SELECTED_ARTICLE = 0;

		 CacheManager.getInstance().resetAllArticles();
		 getArticleData("",Utils.getCurrentDate());
		 getListView().setCacheColorHint(0);

		 if(addDownloadListener != null){
			 IntentFilter filter = new IntentFilter();
			 filter.addAction(Constants.DOWNLOADED_ADDS);
			 registerReceiver(addDownloadListener, filter);
		 }   
		 
		 // Hide ads on landscape 
		 if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				addsImage.setVisibility(View.GONE);
		 }else{
				addsImage.setVisibility(View.VISIBLE);
		 }
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
		addsImage = (ImageView) findViewById(R.id.admarvel);

		// set default as off 
		btn_left_arrow.setBackgroundResource(R.drawable.left_arrow_off);


		mBtnGreenBasic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSelectedIndex = 0;
				btn_left_arrow.setBackgroundResource(R.drawable.left_arrow_on);
				refreshViews();
				mBtnGreenBasic.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 1;
				CURRENT_SELECTED_ARTICLE = 0;
				CacheManager.getInstance().resetAllArticles();
				getArticleData("",Utils.getCurrentDate());
			}
		});
		mBtnDesignArcht.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSelectedIndex = 0;
				refreshViews();
				mBtnDesignArcht.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 2;
				CURRENT_SELECTED_ARTICLE = 0;
				CacheManager.getInstance().resetAllArticles();
				getArticleData("",Utils.getCurrentDate());
			}
		});
		mBtnScience.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSelectedIndex = 0;
				refreshViews();
				mBtnScience.setBackgroundResource(R.drawable.scrollbutton_off);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 3;
				CURRENT_SELECTED_ARTICLE = 0;
				CacheManager.getInstance().resetAllArticles();
				getArticleData("",Utils.getCurrentDate());
			}
		});
		mBtnTransport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSelectedIndex = 0;
				refreshViews();
				mBtnTransport.setBackgroundResource(R.drawable.scrollbutton_long);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 4;
				CURRENT_SELECTED_ARTICLE = 0;
				CacheManager.getInstance().resetAllArticles();
				getArticleData("",Utils.getCurrentDate());
			}
		});
		mBtnBusiness.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSelectedIndex = 0;
				refreshViews();
				mBtnBusiness.setBackgroundResource(R.drawable.scrollbutton_long);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 5;
				CURRENT_SELECTED_ARTICLE = 0;
				CacheManager.getInstance().resetAllArticles();
				getArticleData("",Utils.getCurrentDate());
			}
		});
		mBtnPolitics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSelectedIndex = 0;
				btn_right_arrow.setBackgroundResource(R.drawable.right_arrow_on);
				refreshViews();
				mBtnPolitics.setBackgroundResource(R.drawable.scrollbutton_long);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 6;
				CURRENT_SELECTED_ARTICLE = 0;
				CacheManager.getInstance().resetAllArticles();
				getArticleData("",Utils.getCurrentDate());
			}
		});
		mBtnFood.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSelectedIndex = 0;
				btn_right_arrow.setBackgroundResource(R.drawable.right_arrow_off);
				refreshViews();
				mBtnFood.setBackgroundResource(R.drawable.scrollbutton_long);
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_CATEGORY = 7;
				CURRENT_SELECTED_ARTICLE = 0;
				CacheManager.getInstance().resetAllArticles();
				getArticleData("",Utils.getCurrentDate());
			}
		});
		final ImageView btn_Media = (ImageView) findViewById(R.id.btn_media);
		btn_Media.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//showMediaOptions();
				showDialog();
			}
		});

		mBtnLatest = (TextView)findViewById(R.id.btn_latest);

		mBtnLatest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSelectedIndex = 0;
				btn_left_arrow.setBackgroundResource(R.drawable.left_arrow_off);
				CURRENT_SELECTED_CATEGORY = 0;
				CURRENT_SELECTED_MEDIA = "ALL";
				CURRENT_SELECTED_ARTICLE = 0;
				refreshViews();
				mBtnLatest.setBackgroundResource(R.drawable.scrollbutton_off);
				CacheManager.getInstance().resetAllArticles();
				getArticleData("",Utils.getCurrentDate());
			}
		});
		refreshViews();
		mBtnLatest.setBackgroundResource(R.drawable.scrollbutton_off);

		addsImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showadDetails();

			}
		});
	}

	private void showDialog() {
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this); 
		builder.setTitle("Article Types");
		builder.setIcon(R.drawable.icon);
		final CharSequence[] items = {"All", "Multimedia", "Articles"};
	
    builder.setSingleChoiceItems(items, CURRENT_SELECTED_ARTICLE, new DialogInterface.OnClickListener(){
    	
		public void onClick(DialogInterface dialog, int mitems) {
			CacheManager.getInstance().resetAllArticles();
			switch(mitems){
				case 0:{
					CURRENT_SELECTED_MEDIA = "ALL";
					CURRENT_SELECTED_ARTICLE = 0;
				}
				break;
				case 1:{
					CURRENT_SELECTED_MEDIA =  "multimedia";
					CURRENT_SELECTED_ARTICLE =  1;
				}
				break;
				case 2:
					CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_TEXT;
					CURRENT_SELECTED_ARTICLE = 2;
					break;
					
				default:
					break;
			}
			getArticleData(CURRENT_SELECTED_MEDIA,Utils.getCurrentDate());
			mPresenceDlg = dialog;
			Handler myHandler = new Handler();
			myHandler.postDelayed(mPresenceDlgRunnable, 10);
		}  
		
    });
    AlertDialog View  = builder.create();
    View.show();
	/*	final CharSequence[] items = {"All", "Multimedia", "Articles"};
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		builder.setTitle("Article Types");
		builder.setIcon(R.drawable.icon);
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				CacheManager.getInstance().resetAllArticles();
				if(item == 0){//All
					CURRENT_SELECTED_MEDIA = "ALL";
				}
				else if(item == 1){//Multimedia
					CURRENT_SELECTED_MEDIA =  "multimedia";
				}	
				else if(item == 2){//text
					CURRENT_SELECTED_MEDIA = Constants.ARTICAL_TYPE_TEXT;
				}
				dialog.cancel();
				getArticleData(CURRENT_SELECTED_MEDIA,Utils.getCurrentDate());
			}
		});
		AlertDialog alert = builder.create();
		alert.show();*/
	} 

	private DialogInterface mPresenceDlg = null;
	/**
	 *  Presence Dialog Runnable
	 */
	private Runnable mPresenceDlgRunnable = new Runnable(){
         @Override
         public void run(){
        	 try{
	            //Change state here
	        	 if(mPresenceDlg != null)
	        		 mPresenceDlg.cancel();
	        	 mPresenceDlg = null;
        	 }catch (Exception e) {
				e.printStackTrace();
			}
 	     }
    };
   

	private void showadDetails(){
		ArrayList<ArticleDAO> advertiseData = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ADVERTISMENTS);

		if(advertiseData != null){
			Bitmap[] data = advertiseData.get(Constants.CURRENT_AD_INDEX).getAddsBitmap();
			if(data != null && data.length == 2 && data[1] != null){
				Intent intent = new Intent(getApplicationContext(), AdDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("adindex", Constants.CURRENT_AD_INDEX);
				startActivity(intent);
			}
		}
	}

	@Override
	protected void onDestroy() {
		if(adTask != null){
			adTask.cancel();
		}
		if(adTimer != null){
			adTimer.cancel();
		}
		
		if(addDownloadListener !=null){
			unregisterReceiver(addDownloadListener);
			addDownloadListener = null;
		}
		super.onDestroy();
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
	private void getArticleData(String articleType,String date){
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
				articleDAO.setLastArticlePublishingDate(date); // TBD 
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
			menu.add(0, MENU_OPTION_SAVED,0 , "Saved Items").setIcon(R.drawable.btn_save_unselected);
			menu.add(0, MENU_OPTION_SEARCH,0 , "Search").setIcon(R.drawable.btn_search_unselected);
			menu.add(0, MENU_OPTION_INFO,0 , "Info").setIcon(R.drawable.btn_info_unselected);
			menu.add(0, MENU_OPTION_REFRESH,0 , "Refresh").setIcon(R.drawable.btn_info_unselected);
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
		case MENU_OPTION_REFRESH:
			// clear the old data
			currentSelectedIndex = 0;
			CacheManager.getInstance().resetAllArticles();
			getArticleData("",Utils.getCurrentDate());
			break;
		case MENU_OPTION_INFO:
			try{
				Context cxt = getApplicationContext();
				Intent intent = new Intent(cxt, About.class);     		
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(Constants.DEVICE_MODEL.contains("I9000")&& (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount()==Constants.MENU_PRESS_COUNT_NO_SEARCH_KEY)){
			launchSearchActivity();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_SEARCH){
			launchSearchActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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


	private void addFooterView(){
		if(getListView().getFooterViewsCount() == 0)
			getListView().addFooterView(footerView);
	}

	private void RemoveFooterView(){
		if(footerView !=null){
			footerView.getLayoutParams().height = 0;
		}

	}

	@Override
	public void update(Constants.ENUM_PARSERRESPONSE updateData, final byte reqID, byte errorCode) {

		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){
			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");

			if(reqID == Constants.REQ_GETARTICLESBYTYPE){
				if(mAdapter == null){
					runOnUiThread(new Runnable() { 
						@SuppressWarnings("unchecked")
						@Override
						public void run() {
							int sizeOfData = 0;
							if(reqID == Constants.REQ_GETARTICLESBYTYPE)
								sizeOfData =  CacheManager.getInstance().getAllArticledetailsSize();
							if(sizeOfData == 0)
								mNoItems.setVisibility(View.VISIBLE);
							else
								mNoItems.setVisibility(View.GONE);
							
							mAdapter = new HomeScreenAdapter(HomeActivity.this);
							mAdapter.setRequestID(reqID);
							setListAdapter(mAdapter); 
							// Call adds 
							getAdvertisements();
						}
					});

				}else{
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							int sizeOfData = 0;
							if(reqID == Constants.REQ_GETARTICLESBYTYPE)
								sizeOfData =  CacheManager.getInstance().getAllArticledetailsSize();
							 //mAdapter.resetData();
							if(sizeOfData == 0)
								mNoItems.setVisibility(View.VISIBLE);
							else
								mNoItems.setVisibility(View.GONE);
							
							 mAdapter.setRequestID(reqID);
							 mAdapter.notifyDataSetChanged();
							 mAdapter.notifyDataSetInvalidated();
							getListView().setSelection(0);
						}
					});
				}
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

	private void getAdvertisements() {
		if(Utils.isNetworkAvail(getApplicationContext())){
			ArticleDAO articleDAO = new ArticleDAO();
			articleDAO.setArticleID("mdpi");
			HttpHandler httpHandler =  HttpHandler.getInstance();
			//Cancel previous request;
			httpHandler.cancelRequest();
			httpHandler.setApplicationContext(getApplicationContext());
			httpHandler.handleEvent(articleDAO, Constants.REQ_GETADVERTISMENTS, this);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			addsImage.setVisibility(View.GONE);
		}else{
			addsImage.setVisibility(View.VISIBLE);
		}
	}
	
	
	int currentSelectedIndex = 0;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(Utils.isNetworkAvail(getApplicationContext())){
			ArticleDAO articleEntry = ((ArticleDAO)getListAdapter().getItem(position));
			currentSelectedIndex = position;
			if(!articleEntry.isLoadMore()){
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
				ArrayList<ArticleDAO> tempdata = (ArrayList<ArticleDAO>) CacheManager.getInstance().getAllArticles();
				if(tempdata != null){
					String lastDate = tempdata.get(tempdata.size() - 1).getPublishedDate();
					if(lastDate != null && !lastDate.equals(""))
						getArticleData("",lastDate); // add last article date
				}
			}
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
		getArticleData(CURRENT_SELECTED_MEDIA,Utils.getCurrentDate());
	}

	static int count = 0;

	/**
	 * update adds
	 */
	private void updateAdvertisements(){
		adTimer = new Timer(); 
		count = 0;

		final Handler handler = new Handler();

		adTask = new TimerTask() {       
			public void run() {         
				handler.post(new Runnable() {   
					public void run() { 
						Log.i("Advertisments ", "Display started");
						if(advertiseData != null){
							if(Constants.CURRENT_AD_INDEX < (advertiseData.size() - 1)){
								Constants.CURRENT_AD_INDEX++;
							}else
							{
								Constants.CURRENT_AD_INDEX = 0;
							}
							Bitmap[] data = advertiseData.get(Constants.CURRENT_AD_INDEX).getAddsBitmap();
							if(data[0] != null){
								addsImage.setImageBitmap(data[0]);
							}
						}
					}       
				}); 
			}
		};
		adTimer.schedule(adTask, 2000, 30000);
	}


	/**
	 * Listen to Broadcast w.r.t avatar Settings
	 */
	private BroadcastReceiver addDownloadListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			try {
				String action = intent.getAction();
				if(action == null)
					return;
				if(action.equals(Constants.DOWNLOADED_ADDS)){
					advertiseData = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ADVERTISMENTS);
					updateAdvertisements();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	};




	private void timerMethod()
	{
		this.runOnUiThread(generate);
	}


	private Runnable generate= new Runnable() {

		@Override
		public void run() {

			/*ImageView toAdd = new ImageView(ImagePlayActivity.this);        
		Drawable imgContent = ImagePlayActivity.this.getResources().getDrawable(R.drawable.icon);
		toAdd.setImageDrawable(imgContent);
		toAdd.setTag("img"+counter++);

		Random rndGen = new Random();
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,rndGen.nextInt(300),rndGen.nextInt(300));
		toAdd.setLayoutParams(lp);
		toAdd.setBackgroundColor(Color.TRANSPARENT);

		AlphaAnimation anim = new AlphaAnimation(0, 1);
		anim.setDuration(1000);
		container.addView(toAdd);
		//container.invalidate(); 
		toAdd.startAnimation(anim);
			 */	}
	};

}

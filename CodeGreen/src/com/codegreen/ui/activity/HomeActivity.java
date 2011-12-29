package com.codegreen.ui.activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.adaptor.HomeScreenAdapter;
import com.codegreen.ui.dialog.ReviewDialog;
import com.codegreen.util.Constants;

public class HomeActivity extends ListActivity implements Updatable{

	HomeScreenAdapter mAdapter = null;
	String TAG = "HomeActivity";
	private static String CurrentTabSelected = Constants.ARTCLETYPE_TEXT;


	private static final int MENU_OPTION_SAVED = 0x01;
	private static final int MENU_OPTION_SEARCH = 0x02;
	private static final int MENU_OPTION_SHARE = 0x03;
	private static final int MENU_OPTION_INFO = 0x04;
	
	Button mBtnGreenBasic = null;
	Button mBtnDesignArcht = null;
	Button mBtnScience = null;
	Button mBtnTransport = null;
	Button mBtnBusiness = null;
	Button mBtnPolitics = null;
	Button mBtnFood = null;
	LinearLayout progress_Lay = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//progressBar = (ProgressBar)findViewById(R.id.header_progress_circular);
	     initWidgets();
	//	getArticleData(Constants.ARTICAL_TYPE_TEXT); 
	     searchArticles(Constants.GREEN_BASICS);
	}


	/**
	 * Initialize variables
	 */
	private void initWidgets() {
		/*Button btn_text = (Button)findViewById(R.id.text_btn);
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
		});*/
		
		progress_Lay = (LinearLayout)findViewById(R.id.progress_lay);
		
		 mBtnGreenBasic = (Button)findViewById(R.id.btn_green_basics);
		 mBtnDesignArcht = (Button)findViewById(R.id.btn_design);
		 mBtnScience = (Button)findViewById(R.id.btn_secience);
		 mBtnTransport = (Button)findViewById(R.id.btn_transport);
		 mBtnBusiness = (Button)findViewById(R.id.btn_business);
		 mBtnPolitics = (Button)findViewById(R.id.btn_politics);
		 mBtnFood = (Button)findViewById(R.id.btn_food);
		 mBtnGreenBasic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					searchArticles(Constants.GREEN_BASICS);
				}
			});
		 mBtnDesignArcht.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					searchArticles(Constants.DESIGN_AND_ARCHITECTURE);
				}
			});
		 mBtnScience.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					searchArticles(Constants.SCIENCE_ANDECHNOLOGY);
				}
			});
		 mBtnTransport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					searchArticles(Constants.TRANSPORT);
				}
			});
		 mBtnBusiness.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					searchArticles(Constants.BUSINESS);
				}
			});
		 mBtnPolitics.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					searchArticles(Constants.POLITICS);
				}
			});
		 mBtnFood.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					searchArticles(Constants.FOOD_AND_HEALTH);
				}
			});
	}
	
	
	private void searchArticles(int type){
		try {
			HttpHandler httpHandler =  HttpHandler.getInstance();
			//Cancel previous request;
			httpHandler.cancelRequest();

			//Start progress bar
			progress_Lay.setVisibility(View.VISIBLE);
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
			//progressBar.setVisibility(View.VISIBLE);

			//Prepare data for new request
			ArticleDAO articleDAO = new ArticleDAO();
			articleDAO.setType(articleType);
			articleDAO.setLastArticlePublishingDate("12/20/2011");

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
			menu.add(0, MENU_OPTION_SHARE,0 , "Share").setIcon(android.R.drawable.ic_menu_share);
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
			
			break;
		case MENU_OPTION_SEARCH:
			showDialog(ID_SEARCH);
			launchSearchActivity();

			break;

		case MENU_OPTION_SHARE:
			
			break;

		case MENU_OPTION_INFO:
			break;

		default:
			break;
		}
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ID_SEARCH:
			ReviewDialog reviewDialog = new ReviewDialog(this);
			return reviewDialog;
		default:
			break;
		}
		return null;
		
	}

	@Override
	public void update(Constants.ENUM_PARSERRESPONSE updateData) {

		/*if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");
			if(mAdapter == null){
				runOnUiThread(new Runnable() { 
					@Override
					public void run() {
						mAdapter = new HomeScreenAdapter(HomeActivity.this);
						setListAdapter(mAdapter); 
					}
				});

			}else{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
						mAdapter.notifyDataSetInvalidated();
					}
				});
			}
		}else
			Log.e(TAG, "--------Response Received-------ENUM_PARSERRESPONSE.PARSERRESPONSE_FAILURE");
*/
		
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");
						
			if(mAdapter == null){
				runOnUiThread(new Runnable() { 
					@Override
					public void run() {
						mAdapter = new HomeScreenAdapter(HomeActivity.this);
						setListAdapter(mAdapter); 
					}
				});

			}else{
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
						mAdapter.notifyDataSetInvalidated();
					}
				});
			}
		}
		//Send message to remove progress bar
		Message msg = new Message();
		msg.what = Constants.PROGRESS_INVISIBLE;
		uiUpdator.sendMessage(msg);
	} 

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ArticleDAO articleEntry = ((ArticleDAO)getListAdapter().getItem(position));
		
		if(articleEntry != null){
			Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
			PlayerActivity.streamUrl = articleEntry.getUrl();
			startActivity(intent);
			
			/*Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		    Uri data = Uri.parse(articleEntry.getUrl());
		    intent.setDataAndType(data, "video/mp4");
		    startActivity(intent);*/
			return;
		}
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
				progress_Lay.setVisibility(View.VISIBLE);
				break;

			case Constants.PROGRESS_INVISIBLE:
				progress_Lay.setVisibility(View.INVISIBLE);
				break;
			}
		};
	};
	
	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event){

	    	try{
	    		 if(keyCode == KeyEvent.KEYCODE_SEARCH)
	    			launchSearchActivity();
	    		
	    	}catch (Exception e) {
	    		e.printStackTrace();
			}    		
	    	return false;
	    
	    }
	 
	 /**
	     *  Launch SearchCallhistoryActivity 
	     * @param filterStr
	     */
	    private void launchSearchActivity(){
	    	try{
				Context cxt = getApplicationContext();
	    		Intent intent = new Intent(cxt, SearchActivity.class);     		
	    		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    		startActivity(intent);
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    }
}

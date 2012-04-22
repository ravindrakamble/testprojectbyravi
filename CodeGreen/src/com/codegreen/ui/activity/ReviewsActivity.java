package com.codegreen.ui.activity;

import java.util.List;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewsActivity extends  Activity implements Updatable {

	LinearLayout lay_container = null;
	String TAG = "ReviewsActivity";
	String selectedType = "";
	String strSelectedID = "";
	String strArticleName = "";
	private static int CREATE_REVIEW_CODE = 1;
	private TextView mNoItems = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.codegreen.R.layout.show_reviews);
		mNoItems =(TextView) findViewById(android.R.id.empty);
		ImageView view = (ImageView) findViewById(R.id.btn_submit);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
					showDialog(Constants.DIALOG_REVIEW);	
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		lay_container = (LinearLayout) findViewById(R.id.review_container);
		if(getIntent() != null){
			selectedType = getIntent().getStringExtra("SelectedType")	;
			strSelectedID = getIntent().getStringExtra("selectedID")	;
			strArticleName = getIntent().getStringExtra("ArticleName");
		}
		getReviews();
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CREATE_REVIEW_CODE) {   
			if (resultCode == RESULT_OK)
			{       
				getReviews();
			} 
		}
	}
	
	private static final int MENU_OPTION_HOME = 0x02;
	private static final int MENU_OPTION_SAVED = 0x03;
	private static final int MENU_OPTION_INFO = 0x04;
	private static final int MENU_OPTION_SEARCH = 0x05;
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try{
			menu.removeGroup(0);
			menu.add(0, MENU_OPTION_HOME,0 , "Home").setIcon(R.drawable.btn_home_unselected);
			menu.add(0, MENU_OPTION_SAVED,0 , "Saved Items").setIcon(R.drawable.btn_save_unselected);
			menu.add(0, MENU_OPTION_SEARCH,0 , "Search").setIcon(R.drawable.btn_search_unselected);
			menu.add(0, MENU_OPTION_INFO,0 , "Info").setIcon(R.drawable.btn_info_unselected);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void launchHomeActivity(){
		try{
			Context cxt = getApplicationContext();
			Intent intent = new Intent(cxt, HomeActivity.class);     		
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_OPTION_HOME:
			launchHomeActivity();
			break; 
		case MENU_OPTION_SAVED:
			launchSavedActivity();
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
		case MENU_OPTION_SEARCH:
			launchSearchActivity();
			break;
		default:
			break;
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
			intent.putExtra("Selected_Category", 0);
			intent.putExtra("Selected_Media", "ALL");
			startActivity(intent);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getReviews(){
		try {
			if(Utils.isNetworkAvail(getApplicationContext())){
				HttpHandler httpHandler =  HttpHandler.getInstance();
				//Cancel previous request;
				httpHandler.cancelRequest();

				//Start progress bar
				Message msg = new Message();
				msg.what = SHOW_REVIEW_PROGRESS;
				handler.sendMessage(msg);

				//Prepare data for new request
				ReviewDAO reviewDAO = new ReviewDAO();
				reviewDAO.setArticleID(strSelectedID);
				reviewDAO.setArticleType(selectedType);
				//Send request
				httpHandler.setApplicationContext(getApplicationContext());
				httpHandler.handleEvent(reviewDAO, Constants.REQ_GETREVIEWS, this);
			}else{
				Toast.makeText(getApplicationContext(), "No Network Available", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void update(byte errorCode, byte callID, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(ENUM_PARSERRESPONSE updateData, byte callID,
			byte errorCode) {
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");


			if(callID == Constants.REQ_GETREVIEWS){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						List<ReviewDAO> reviewList = (List<ReviewDAO>) CacheManager.getInstance().get(Constants.C_REVIEWS);
						ReviewDAO reviewDAO = null;

						if(reviewList != null && reviewList.size() > 0){
							mNoItems.setVisibility(View.GONE);
							for(int i = 0; i < reviewList.size(); i++){
								reviewDAO = reviewList.get(i);
								View view = ((LayoutInflater)ReviewsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.review_layout, null, false);
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
								params.setMargins(5, 5, 5, 5);
								view.setLayoutParams(params);
								TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
								TextView txt_review = (TextView) view.findViewById(R.id.txt_review);
								TextView txt_date = (TextView)view.findViewById(R.id.txt_date);
								if(reviewDAO.getUserName() != null && !reviewDAO.getUserName().equalsIgnoreCase(""))
									txt_name.setText(reviewDAO.getUserName());
								else
									txt_name.setText(getString(R.string.submitted_by_anonymous));

								txt_review.setText(reviewDAO.getReviewComments());
								txt_date.setText(reviewDAO.getReviewDate());

								lay_container.addView(view);
							}
						}else{
							mNoItems.setVisibility(View.VISIBLE);
						}
					}
				});

			}else if(callID == Constants.REQ_SUBMITREVIEW){
				if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Utils.displayMessage(getApplicationContext(), getString(R.string.review_submitted));
							//update the reviews
							getReviews();
						}
					});
				}else{
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Utils.displayMessage(getApplicationContext(), "Review Submit failed, Please try again.");
						}
					});
				}
			}
		}
		Message msg = new Message();
		msg.what = REMOVE_REVIEW_PROGRESS;
		handler.sendMessage(msg);

	}

	private ProgressDialog reviewProgressDialog = null;
	private final int SHOW_REVIEW_PROGRESS = 3;
	private final int REMOVE_REVIEW_PROGRESS = 4;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.DIALOG_REVIEW:
			ArticleDAO articledata = new ArticleDAO();
			articledata.setArticleID(strSelectedID);
			articledata.setType(selectedType);
			ReviewDialog reviewDialog = new ReviewDialog(this,articledata);
			return reviewDialog;
		case Constants.DIALOG_REVIEW_PROGRESS:
			showReviewProgressBar();
			return reviewProgressDialog;
		default:
			break;
		}
		return null;

	}

	private void showReviewProgressBar(){

		if(reviewProgressDialog == null){
			reviewProgressDialog = new ProgressDialog(this);
			reviewProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			reviewProgressDialog.setMessage("Downloading reviews,please wait...");
			reviewProgressDialog.setIcon(android.R.id.icon);
			reviewProgressDialog.setCancelable(false);
			reviewProgressDialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//getReviews();
		mNoItems.setVisibility(View.GONE);
	}



	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch(what){
			case SHOW_REVIEW_PROGRESS:
				showDialog(Constants.DIALOG_REVIEW_PROGRESS);
				break;
			case REMOVE_REVIEW_PROGRESS:
				if(reviewProgressDialog != null){
					reviewProgressDialog.dismiss();
					reviewProgressDialog.cancel();
				}
				break;
			}
		};
	};



}
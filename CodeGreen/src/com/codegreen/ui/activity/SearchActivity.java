package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.adaptor.SearchScreenAdapter;
import com.codegreen.util.Constants;
import com.codegreen.util.Utils;
import com.facebook.android.Util;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends ListActivity implements Updatable{

	private static final String TAG = "SEARCHACTIVITY";
	/**
	 *  Search View consists of EditText and ImageView Widgets
	 */
	private View mSearchView = null; 
	private EditText mSearchText = null;
	private SearchScreenAdapter mAdapter = null;
	private ImageView searchView;

	/*
	 * Variable to hold the empty screen Text
	 */
	private TextView mNoItems;
	LinearLayout progressLayout = null;

	private  int CURRENT_SELECTED_CATEGORY = 1;
	private  String CURRENT_SELECTED_MEDIA  ="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_view);
		mSearchView = findViewById(R.id.search_view1);
		mSearchText = (EditText)mSearchView.findViewById(R.id.search_editview);

		mSearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		        }
		    }
		});
		if(getIntent() !=null){
			CURRENT_SELECTED_CATEGORY = getIntent().getIntExtra("Selected_Category",1);
			CURRENT_SELECTED_MEDIA = getIntent().getStringExtra("Selected_Media");
		}
		//mSearchText.addTextChangedListener(mSearchTextWatcher);
		progressLayout = (LinearLayout)findViewById(R.id.progreeView);
		progressLayout.setVisibility(View.GONE);
		searchView = (ImageView)mSearchView.findViewById(R.id.search_imageview);
		searchView.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchView.setEnabled(false);
				mNoItems.setVisibility(View.GONE);
				progressLayout.setVisibility(View.VISIBLE);
				//Call the search web service
				searchArticles(mSearchText.getText().toString().trim(), CURRENT_SELECTED_CATEGORY);
			}
		});
		//	mSearchText.setFilters(new InputFilter[] {mSearchTextFilter , mSearchTextLengthFilter});
		mNoItems = (TextView) findViewById(android.R.id.empty);
		// First time display no match found 
		mNoItems.setText(Constants.NO_MATCH_FOUND);  
		mAdapter = new SearchScreenAdapter(this,"");
		setListAdapter(mAdapter);
	}

	private void searchArticles(String searchString, int type){
		try {
			if(Utils.isNetworkAvail(getApplicationContext())){
				HttpHandler httpHandler =  HttpHandler.getInstance();
				//Cancel previous request;
				httpHandler.cancelRequest();
				//Start progress bar
				//Prepare data for new request
				ArticleDAO articleDAO = new ArticleDAO();
				articleDAO.setTitle(searchString);
				articleDAO.setType(CURRENT_SELECTED_MEDIA);
				articleDAO.setCategoryID(String.valueOf(type));

				//Send request
				httpHandler.setApplicationContext(getApplicationContext());
				httpHandler.handleEvent(articleDAO, Constants.REQ_SEARCHARTICLES, this);
			}else{
				progressLayout.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(),"No Network Available", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Filter
	 */
	private InputFilter mSearchTextFilter = new InputFilter(){

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			for(int i = start; i < end; i++){
				if(source.charAt(i) == '\n')
					return "";
			}
			return source;
		}

	};

	/**
	 * Length Filter
	 */
	//private InputFilter mSearchTextLengthFilter = new InputFilter.LengthFilter(30);



	/*private TextWatcher mSearchTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			try{
				String filterStr = null;
				int filteredCount = 0;
				try{
					filterStr = mSearchText.getText().toString();

				}catch (NullPointerException ne) {
					ne.printStackTrace();
				}
				filteredCount = mAdapter.applyFilter(filterStr);
				if(filteredCount == 0)
					mNoItems.setText("No match found.");
			}catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	 */
	@Override
	public void update(byte errorCode, byte callID, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Constants.ENUM_PARSERRESPONSE updateData, byte ReqID,byte errorCode) {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(progressLayout != null)
					progressLayout.setVisibility(View.GONE);
			}
		});
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					searchView.setEnabled(true);
					searchView.setVisibility(View.VISIBLE);
				}
			});
			if(mAdapter == null){
				runOnUiThread(new Runnable() { 
					@Override
					public void run() {
						mAdapter = new SearchScreenAdapter(SearchActivity.this, "");
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
		}else if(errorCode == Constants.ERR_NETWORK_FAILURE){
			Toast.makeText(this, "No Network Available.", Toast.LENGTH_SHORT).show();	
		}
	}

	
	private static final int MENU_OPTION_SAVED = 0x01;
	private static final int MENU_OPTION_HOME = 0x02;
	private static final int MENU_OPTION_INFO = 0x04;

	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try{
			menu.removeGroup(0);
			menu.add(0, MENU_OPTION_HOME,0 , "Home").setIcon(android.R.drawable.ic_menu_gallery);
			menu.add(0, MENU_OPTION_SAVED,0 , "Saved Items").setIcon(android.R.drawable.ic_menu_gallery);
			menu.add(0, MENU_OPTION_INFO,0 , "Info").setIcon(android.R.drawable.ic_menu_help);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_OPTION_SAVED:
			launchSavedActivity();
			break;
		case MENU_OPTION_HOME:
			launchHomeActivity();
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
	protected void onListItemClick(ListView l, View v, int position, long id) {

		if(Utils.isNetworkAvail(getApplicationContext())){
			ArticleDAO articleEntry = ((ArticleDAO)getListAdapter().getItem(position));
			Intent intent = new Intent(getApplicationContext(), ArticleDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Constants.CURRENT_ARTICLE_TYPE, articleEntry.getType());
			intent.putExtra("ArticleID", articleEntry.getArticleID());
			startActivity(intent);
		}else{
			Toast.makeText(getApplicationContext(),"No Network Available", Toast.LENGTH_SHORT).show();
		}
	}  

}

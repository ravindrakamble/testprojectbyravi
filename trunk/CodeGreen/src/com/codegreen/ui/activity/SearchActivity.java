package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.adaptor.SearchScreenAdapter;
import com.codegreen.util.Constants;
import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_view);
		mSearchView = findViewById(R.id.search_view1);
		mSearchText = (EditText)mSearchView.findViewById(R.id.search_editview);
		mSearchText.addTextChangedListener(mSearchTextWatcher);
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
				searchArticles(mSearchText.getText().toString().trim(), 1);
			}
		});
		mSearchText.setFilters(new InputFilter[] {mSearchTextFilter , mSearchTextLengthFilter});
		mNoItems = (TextView) findViewById(android.R.id.empty);
		// First time display no match found 
		mNoItems.setText(Constants.NO_MATCH_FOUND);  
		mAdapter = new SearchScreenAdapter(this,"");
		setListAdapter(mAdapter);
	}

	private void searchArticles(String searchString, int type){
		try {
			HttpHandler httpHandler =  HttpHandler.getInstance();
			//Cancel previous request;
			httpHandler.cancelRequest();

			//Start progress bar

			//Prepare data for new request
			ArticleDAO articleDAO = new ArticleDAO();
			articleDAO.setTitle(searchString);
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
	private InputFilter mSearchTextLengthFilter = new InputFilter.LengthFilter(30);



	private TextWatcher mSearchTextWatcher = new TextWatcher() {
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


	@Override
	public void update(byte errorCode, byte callID, Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Constants.ENUM_PARSERRESPONSE updateData) {

		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");
			
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					searchView.setEnabled(true);
					searchView.setVisibility(View.VISIBLE);
					progressLayout.setVisibility(View.GONE);
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
		}
		
	}



}

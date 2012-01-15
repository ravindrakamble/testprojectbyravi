package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.ui.adaptor.HomeScreenAdapter;
import com.codegreen.ui.adaptor.SavedArticlesAdapter;
import com.codegreen.util.Constants;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class SavedArticlesActivity extends ListActivity {

	private SavedArticlesAdapter mAdapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_articles);
		
		loadSavedArticles();
	}
	
	private void loadSavedArticles(){
		mAdapter = new SavedArticlesAdapter(SavedArticlesActivity.this);
		setListAdapter(mAdapter); 
		mAdapter.notifyDataSetInvalidated();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ArticleDAO articleEntry = ((ArticleDAO)getListAdapter().getItem(position));
		CacheManager.getInstance().store(Constants.C_ARTICLE_DETAILS, articleEntry);
		// Launch details screen
		//if(articleEntry != null && articleEntry.getType().equalsIgnoreCase(Constants.ARTCLETYPE_IMAGE)||  articleEntry != null && articleEntry.getType().equalsIgnoreCase(Constants.ARTCLETYPE_TEXT)){
			Intent intent = new Intent(getApplicationContext(), ArticleDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(Constants.CURRENT_ARTICLE_TYPE, articleEntry.getType());
			intent.putExtra("ArticleID", articleEntry.getArticleID());
			intent.putExtra("savedarticle", true);
			startActivity(intent);
	}
}

package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.database.DBAdapter;
import com.codegreen.ui.adaptor.SavedArticlesAdapter;
import com.codegreen.util.Constants;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SavedArticlesActivity extends ListActivity {

	private DBAdapter dbAdapter;
	private SavedArticlesAdapter mAdapter = null;
	private static final int MENU_OPTION_DELETE = 0x01;
	private static final int MENU_OPTION_DELETEALL = 0x02;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.saved_articles);

		loadSavedArticles();
		registerForContextMenu(getListView());
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
		Constants.CURRENT_INDEX = position;
		Intent intent = new Intent(getApplicationContext(), ArticleDetailsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constants.CURRENT_ARTICLE_TYPE, articleEntry.getType());
		intent.putExtra("ArticleID", articleEntry.getArticleID());
		intent.putExtra("savedarticle", true);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == MENU_OPTION_DELETEALL){
			deleteAll();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Delete Article");
		menu.add(0, MENU_OPTION_DELETE, 0, "Delete");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.removeGroup(0);
		menu.add(0, MENU_OPTION_DELETEALL,0 , "Delete All").setIcon(android.R.drawable.ic_menu_delete);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		if(item.getItemId() == MENU_OPTION_DELETE){
			deleteArticle(info.position);
			return true;
		}
		return false;
	}
	
	private void deleteArticle(int position){
		ArticleDAO articleEntry = ((ArticleDAO)getListAdapter().getItem(position));
		dbAdapter = DBAdapter.getInstance(this);
		dbAdapter.open();
		boolean status = dbAdapter.deleteArticle(articleEntry);
		dbAdapter.close();
		

		if(status){
			mAdapter.removeItemAt(position);
			mAdapter.notifyDataSetInvalidated();
			Toast.makeText(this, "Article deleted", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "Failed to delete article.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void deleteAll(){
		if(mAdapter.getCount() == 0){
			Toast.makeText(this, "No articles to delete.", Toast.LENGTH_SHORT).show();
			return;
		}
		dbAdapter = DBAdapter.getInstance(this);
		dbAdapter.open();
		boolean status = dbAdapter.deleteAllArticles();
		dbAdapter.close();
		

		if(status ){
			mAdapter.removeAll();
			mAdapter.notifyDataSetInvalidated();
			Toast.makeText(this, "All articles deleted.", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "Failed to delete articles.", Toast.LENGTH_SHORT).show();
		}
	}
}

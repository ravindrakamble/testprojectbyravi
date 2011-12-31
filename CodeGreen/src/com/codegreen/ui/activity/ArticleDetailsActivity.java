package com.codegreen.ui.activity;

import java.util.ArrayList;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.listener.Updatable;
import com.codegreen.util.Constants;
import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ArticleDetailsActivity extends Activity implements Updatable{

	String strSelectedArticleType = Constants.ARTCLETYPE_TEXT;
	String strSelectedArticleID = "";
	String TAG = "ArticleDetailsActivity";
	LinearLayout progress_Lay = null;
	WebView imageView = null;
	private static final int MENU_OPTION_SAVED = 0x01;
	private static final int MENU_OPTION_SEARCH = 0x02;
	private static final int MENU_OPTION_SHARE = 0x03;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent() != null){
			strSelectedArticleType = getIntent().getStringExtra(Constants.CURRENT_ARTICLE_TYPE);
			strSelectedArticleID = getIntent().getStringExtra("ArticleID");
		}
		if(strSelectedArticleType != null){
			if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_TEXT) || strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_IMAGE)){
				setContentView(R.layout.atrticle_text);
				 txtDetails = (TextView) findViewById(R.id.article_details_view);
				 progress_Lay = (LinearLayout)findViewById(R.id.progress_lay);
				 imageView = (WebView)findViewById(R.id.webview);
				//initTextView();
				//get the controls
			}else if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO)){
				setContentView(R.layout.article_audio);
				//get the controls
			}else if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
				setContentView(R.layout.article_vedio);
				//get the controls
			}
		}
		
		if(strSelectedArticleType != null && strSelectedArticleID != null){
			getArticleDetails();
		}
	}

	TextView txtDetails = null;
	
	private void initTextView() {
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try{
			menu.removeGroup(0);
			menu.add(0, MENU_OPTION_SAVED,0 , "Saved Items").setIcon(android.R.drawable.ic_menu_gallery);
			menu.add(0, MENU_OPTION_SEARCH,0 , "Search").setIcon(android.R.drawable.ic_menu_search);
			menu.add(0, MENU_OPTION_SHARE,0 , "Share").setIcon(android.R.drawable.ic_menu_share);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	

	/**
	 * WS call to get article details
	 */
	private void getArticleDetails(){
		progress_Lay.setVisibility(View.VISIBLE);
		HttpHandler httpHandler =  HttpHandler.getInstance();
		ArticleDAO articleDAO = new ArticleDAO();
	    articleDAO.setArticleID(strSelectedArticleID);
		articleDAO.setType(strSelectedArticleType);
		articleDAO.setCategoryID(Constants.CURRENT_CATEGORY_TYPE);
		httpHandler.handleEvent(articleDAO, Constants.REQ_GETARTICLEDETAILS, this);
	}

	@Override
	public void update(byte errorCode, byte callID, Object obj) {
		
	}

	@Override
	public void update(ENUM_PARSERRESPONSE updateData, byte callId) {
		
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progress_Lay.setVisibility(View.GONE);
					String strDetails = "";
					ArticleDAO data = (ArticleDAO) CacheManager.getInstance().get(Constants.C_ARTICLE_DETAILS);
					if(data != null){
						if(data.getUrl() != null){
							imageView.setVisibility(View.VISIBLE);
							imageView.loadUrl(data.getUrl());
						}else
							imageView.setVisibility(View.GONE);
						
						strDetails = "Title : "+ data.getTitle() + "<br/>"+ "Date :" + data.getPublishedDate() + "<br/>" + data.getShortDescription() + "<br/>" + data.getDetailedDescription();
						if(!strDetails.equals("")){
							txtDetails.setVisibility(View.VISIBLE);
							txtDetails.setText(Html.fromHtml(strDetails));
						}
						else
							txtDetails.setVisibility(View.GONE);
					}
				}
			});
		}
	}
}

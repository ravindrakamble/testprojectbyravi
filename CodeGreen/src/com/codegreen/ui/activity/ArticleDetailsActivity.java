package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.util.Constants;
import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;
import android.app.Activity;
import android.os.Bundle;


public class ArticleDetailsActivity extends Activity implements Updatable{

	String strSelectedArticleType = Constants.ARTCLETYPE_TEXT;
	String strSelectedArticleID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent() != null){
			strSelectedArticleType = getIntent().getStringExtra(Constants.CURRENT_ARTICLE_TYPE);
			strSelectedArticleID = getIntent().getStringExtra("ArticleID");
		}
		if(strSelectedArticleType != null){
			if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_TEXT)){
				setContentView(R.layout.atrticle_text);
				//get the controls
			}else if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_IMAGE)){
				setContentView(R.layout.article_image);
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

	/**
	 * WS call to get article details
	 */
	private void getArticleDetails(){
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
	public void update(Constants.ENUM_PARSERRESPONSE updateData) {
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){
			//Update the details
		}
	}

}

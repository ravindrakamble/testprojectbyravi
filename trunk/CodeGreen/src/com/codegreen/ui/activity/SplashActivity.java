package com.codegreen.ui.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.util.Constants;

public class SplashActivity extends Activity implements Updatable{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        HttpHandler httpHandler = new HttpHandler();
        ArticleDAO articleDAO = new ArticleDAO();
        articleDAO.setType("IMAGE");
        articleDAO.setLastArticlePublishingDate("11/25/2011");
        httpHandler.handleEvent(articleDAO, Constants.REQ_GETARTICLESBYTYPE, this);
    }

	@Override
	public void update(byte errorCode, byte callID, Object obj) {
		Log.e("SPLASH", "Response Received");
		
	}

	@Override
	public void update(Object updateData) {
		if(updateData instanceof List<?>){
			@SuppressWarnings("unchecked")
			List<ArticleDAO> articles = (List<ArticleDAO>)updateData;
			
			Log.e("SPLASH", "Response Received" + articles.size());
		}
		Log.e("SPLASH", "Response Received");
		
	}
}
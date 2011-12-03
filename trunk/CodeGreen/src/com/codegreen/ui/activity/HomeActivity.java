package com.codegreen.ui.activity;


import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.adaptor.HomeScreenAdapter;
import com.codegreen.util.Constants;

public class HomeActivity extends ListActivity implements Updatable{

	HomeScreenAdapter mAdapter = null;
	String TAG = "HomeActivity";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initWidgets();
		getArticleData(Constants.ARTICAL_TYPE_IMAGE); 
	}


	/**
	 * Initialize vars
	 */
	private void initWidgets() {
		
	}


	private void getArticleData(String articleType){
		try {
			HttpHandler httpHandler = new HttpHandler();
			ArticleDAO articleDAO = new ArticleDAO();
			articleDAO.setType(articleType);
			articleDAO.setLastArticlePublishingDate("11/25/2011");
			httpHandler.handleEvent(articleDAO, Constants.REQ_GETARTICLESBYTYPE, this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void update(byte errorCode, byte callID, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Constants.ENUM_PARSERRESPONSE updateData) {
		
		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){
			
			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");
			if(mAdapter == null){ 
				mAdapter = new HomeScreenAdapter(this);
				setListAdapter(mAdapter);}else{
					mAdapter.notifyDataSetChanged();
					mAdapter.notifyDataSetInvalidated();
				}
		}else
			Log.e(TAG, "--------Response Received-------ENUM_PARSERRESPONSE.PARSERRESPONSE_FAILURE");
	} 

}

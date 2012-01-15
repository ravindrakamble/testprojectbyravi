package com.codegreen.ui.activity;

import java.util.List;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.DownloadHandler;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.DownloadInfoDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.database.DBAdapter;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.dialog.ReviewDialog;
import com.codegreen.ui.dialog.ShareDialog;
import com.codegreen.util.Constants;
import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;
import com.codegreen.util.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ArticleDetailsActivity extends Activity implements Updatable{

	String strSelectedArticleType = Constants.ARTCLETYPE_TEXT;
	String strSelectedArticleID = "";
	String TAG = "ArticleDetailsActivity";
	LinearLayout progress_Lay = null;
	ImageView imageView = null;
	private static final int MENU_OPTION_SAVED = 0x01;
	private static final int MENU_OPTION_SEARCH = 0x02;
	private static final int MENU_OPTION_SHARE = 0x03;
	private TextView txt_reviews = null;
	private static final int MENU_OPTION_ADD_REVIEW = 0x04;
	private static final int MENU_OPTION_SAVE = 0x05;
	ArticleDAO articleDetails;
	TextView txt_player_select = null;

	private boolean savedArticle;

	private final int SHOW_PROGRESS = 1;
	private final int REMOVE_PROGRESS = 2;

	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent() != null){
			strSelectedArticleType = getIntent().getStringExtra(Constants.CURRENT_ARTICLE_TYPE);
			strSelectedArticleID = getIntent().getStringExtra("ArticleID");
			savedArticle =  getIntent().getBooleanExtra("savedarticle", false);
		}
		if(strSelectedArticleType != null){
			setContentView(R.layout.atrticle_text);
			txtDetails = (TextView) findViewById(R.id.article_details_view);
			progress_Lay = (LinearLayout)findViewById(R.id.progress_lay);
			imageView = (ImageView)findViewById(R.id.webview);
			imageView.setBackgroundColor(Color.LTGRAY);
			txt_reviews = (TextView)findViewById(R.id.txt_reviews);
			txt_reviews.setVisibility(View.GONE);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) || strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
						Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(Constants.CURRENT_ARTICLE_TYPE, strSelectedArticleType);
						intent.putExtra("ArticleID", strSelectedArticleID);
						if(savedArticle){
							intent.putExtra("savedarticle", articleDetails.getUrl());
						}
						startActivity(intent);
					}
				}
			});

			txt_reviews.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					getReviews(articleDetails);
				}
			});
		}

		if(strSelectedArticleType != null && strSelectedArticleID != null){
			if(!savedArticle){
				getArticleDetails();
			}else{
				update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,Constants.REQ_GETARTICLEDETAILS,(byte)0);
			}
		}
	}

	TextView txtDetails = null;



	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try{
			menu.removeGroup(0);
			menu.add(0, MENU_OPTION_SAVED,0 , "Saved Items").setIcon(android.R.drawable.ic_menu_gallery);
			//menu.add(0, MENU_OPTION_SEARCH,0 , "Search").setIcon(android.R.drawable.ic_menu_search);
			menu.add(0, MENU_OPTION_SHARE,0 , "Share").setIcon(android.R.drawable.ic_menu_share);
			menu.add(0, MENU_OPTION_ADD_REVIEW,0 , "Add Review").setIcon(android.R.drawable.ic_menu_add);
			menu.add(0, MENU_OPTION_SAVE,0 , "Save Article").setIcon(android.R.drawable.ic_menu_add);

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
	@SuppressWarnings("unchecked")
	public void update(ENUM_PARSERRESPONSE updateData, final byte callId,byte errorCode) {

		if(updateData == Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS){

			Log.e(TAG, "--------Response Received-------PARSERRESPONSE_SUCCESS");


			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progress_Lay.setVisibility(View.GONE);
					if(callId == Constants.REQ_GETARTICLEDETAILS){
						String strDetails = "";
						articleDetails = (ArticleDAO) CacheManager.getInstance().get(Constants.C_ARTICLE_DETAILS);
						if(articleDetails != null){
							if(articleDetails.getUrl() != null && !articleDetails.getUrl().equals("") || articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) && !articleDetails.getThumbUrl().equals("") || articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO) && !articleDetails.getThumbUrl().equals("") ){
								imageView.setVisibility(View.VISIBLE);
								if(!articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) && !articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
								}else if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO)){
									PlayerActivity.streamUrl = articleDetails.getUrl();
									PlayerActivity.isAudio = true;
								}else{
									PlayerActivity.streamUrl = articleDetails.getUrl();
									PlayerActivity.isAudio = false;
								}
							}else
								imageView.setVisibility(View.GONE);

							strDetails = "<b>Title : </b>"+ articleDetails.getTitle() + "<br/>"+ "<b>Date :</b>" + articleDetails.getPublishedDate() + "<br/>" + articleDetails.getDetailedDescription();
							if(strDetails != null && !strDetails.equals("")){
								txtDetails.setVisibility(View.VISIBLE);
								txtDetails.setText(Html.fromHtml(strDetails));
							}
							else{
								txtDetails.setVisibility(View.GONE);
							}
							txt_reviews.setVisibility(View.VISIBLE);

							downloadImage();
						}
					}else if(callId == Constants.REQ_GETREVIEWS){
						txt_reviews.setVisibility(View.VISIBLE);
						List<ReviewDAO> reviewList = (List<ReviewDAO>) CacheManager.getInstance().get(Constants.C_REVIEWS);
						ReviewDAO reviewDAO = null;
						StringBuilder reviews = new StringBuilder();
						if(reviewList != null && reviewList.size() > 0){
							reviews.append("<b>" + getString(R.string.reviews) +"</b><br>");
							for(int i = 0; i < reviewList.size(); i++){
								reviewDAO = reviewList.get(i);

								reviews.append(reviewDAO.getReviewComments() + "<br>");
								if(reviewDAO.getUserName() != null && !reviewDAO.getUserName().equalsIgnoreCase("")){
									reviews.append(getString(R.string.submitted_by) + " " + reviewDAO.getUserName());
								}else{
									reviews.append(getString(R.string.submitted_by_anonymous));
								}
								if(!reviewDAO.getReviewDate().equals(""))
									reviews.append(" <i>" + reviewDAO.getReviewDate() + "</i> <br><br>");

							}

							txt_reviews.setTextColor(Color.BLACK);
							txt_reviews.setTextSize(15);
							txt_reviews.setText(Html.fromHtml(reviews.toString()));
						}else{
							txt_reviews.setText(getString(R.string.no_reviews));
						}
						//txt_reviews.setText();
					}else if(callId == Constants.REQ_SUBMITREVIEW){
						Utils.displayMessage(getApplicationContext(), getString(R.string.review_submitted));

						//update the reviews
						getReviews(articleDetails);
					}else if(callId == Constants.REQ_DOWNLOADIMAGE){
						if(CacheManager.getInstance().getLatestArticleBitmap() != null){
							imageView.setImageBitmap(CacheManager.getInstance().getLatestArticleBitmap());
						}
					}else if(callId == Constants.REQ_DOWNLOADARTICLE){

						Message msg = new Message();
						msg.what = REMOVE_PROGRESS;
						handler.sendMessage(msg);
					}
				}
			});
		}else if(errorCode == Constants.ERR_NETWORK_FAILURE){
			if(callId == Constants.REQ_DOWNLOADARTICLE){
				Toast.makeText(this, "Article cannot be downloaded. Please try later.", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "No Network Available.", Toast.LENGTH_LONG).show();
			}
		}

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_OPTION_SAVED:
			Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
			startActivity(intent);
			break;

		case MENU_OPTION_SHARE:
			showDialog(Constants.DIALOG_SHARE);
			break;

		case MENU_OPTION_ADD_REVIEW:
			showDialog(Constants.DIALOG_REVIEW);
			break;

		case MENU_OPTION_SAVE:
			if(!Utils.isSdCardPresent()){
				Toast.makeText(getApplicationContext(),"SD Card is required to download article data.", Toast.LENGTH_LONG).show();
				return false;
			}

			DBAdapter dbAdapter = DBAdapter.getInstance(getApplicationContext());
			dbAdapter.open();
			int count =  dbAdapter.getArticles(articleDetails);

			if(count > 0){
				Toast.makeText(getApplicationContext(),getString(R.string.saved_already), Toast.LENGTH_LONG).show();
			}
			dbAdapter.close();

			if(count == 0){
				DownloadHandler downloadHandler = DownloadHandler.getInstance();
				downloadHandler.handleEvent(articleDetails, Constants.REQ_DOWNLOADARTICLE, this);

				Message msg = new Message();
				msg.what = SHOW_PROGRESS;
				handler.sendMessage(msg);
			}
			break;
		default:
			break;
		}
		return false;
	}


	private void saveArticle(ArticleDAO article){
		DBAdapter dbAdapter = DBAdapter.getInstance(getApplicationContext());
		dbAdapter.open();
		int count =  dbAdapter.getArticles(article);

		if(count == 0){
			dbAdapter.insertArticle(article);
			Toast.makeText(getApplicationContext(),getString(R.string.record_saved), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(getApplicationContext(),getString(R.string.saved_already), Toast.LENGTH_LONG).show();
		}
		dbAdapter.close();


	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.DIALOG_REVIEW:
			ReviewDialog reviewDialog = new ReviewDialog(this, articleDetails);
			return reviewDialog;
		case Constants.DIALOG_SHARE:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			ShareDialog shareDialog = new ShareDialog(this, articleDetails,prefs);
			return shareDialog; 

		case Constants.DIALOG_PROGRESS:
			if(progressDialog == null){
				showProgressBar();
			}
			return progressDialog;
		default:
			break;
		}
		return null;

	}

	private void getReviews(ArticleDAO articleDAO){
		try {
			HttpHandler httpHandler =  HttpHandler.getInstance();
			//Cancel previous request;
			httpHandler.cancelRequest();

			//Start progress bar
			progress_Lay.setVisibility(View.VISIBLE);
			//Prepare data for new request
			ReviewDAO reviewDAO = new ReviewDAO();
			reviewDAO.setArticleID(articleDAO.getArticleID());
			reviewDAO.setArticleType(articleDAO.getType());
			//Send request
			httpHandler.setApplicationContext(getApplicationContext());
			httpHandler.handleEvent(reviewDAO, Constants.REQ_GETREVIEWS, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadImage(){
		if(articleDetails != null){
			HttpHandler httpHandler =  HttpHandler.getInstance();
			//Cancel previous request;
			httpHandler.cancelRequest();
			String url = null;
			if(!articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) && !articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
				url = articleDetails.getUrl();
			}else{
				url = articleDetails.getThumbUrl();
			}
			httpHandler.handleEvent(url, Constants.REQ_DOWNLOADIMAGE, ArticleDetailsActivity.this);
		}
	}

	private void shareDialog(){
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sample text");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Example text");    

		startActivity(Intent.createChooser(shareIntent, "TestApp"));
	}

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch(what){
			case SHOW_PROGRESS:
				showDialog(Constants.DIALOG_PROGRESS);
				break;

			case REMOVE_PROGRESS:
				progressDialog.dismiss();
				progressDialog.cancel();
				ArticleDAO dao = (ArticleDAO)CacheManager.getInstance().get(Constants.C_DOWNLOADED_ARTICLE);
				if(dao != null){
					saveArticle(dao);
				}


				break;
			}
		};
	};

	private void showProgressBar(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Downloading article data, please wait...");
			progressDialog.setIcon(android.R.id.icon);
			progressDialog.setCancelable(false);
			progressDialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
		}
	}
}

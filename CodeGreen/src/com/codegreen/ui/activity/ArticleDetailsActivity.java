package com.codegreen.ui.activity;

import java.util.List;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.listener.Updatable;
import com.codegreen.ui.dialog.ReviewDialog;
import com.codegreen.util.Constants;
import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;
import com.codegreen.util.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
	ArticleDAO articleDetails;
	TextView txt_player_select = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent() != null){
			strSelectedArticleType = getIntent().getStringExtra(Constants.CURRENT_ARTICLE_TYPE);
			strSelectedArticleID = getIntent().getStringExtra("ArticleID");
		}
		if(strSelectedArticleType != null){
			setContentView(R.layout.atrticle_text);
			txtDetails = (TextView) findViewById(R.id.article_details_view);
			progress_Lay = (LinearLayout)findViewById(R.id.progress_lay);
			imageView = (ImageView)findViewById(R.id.webview);
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
			//menu.add(0, MENU_OPTION_SEARCH,0 , "Search").setIcon(android.R.drawable.ic_menu_search);
			menu.add(0, MENU_OPTION_SHARE,0 , "Share").setIcon(android.R.drawable.ic_menu_share);
			menu.add(0, MENU_OPTION_ADD_REVIEW,0 , "Add Review").setIcon(android.R.drawable.ic_menu_add);
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
	public void update(ENUM_PARSERRESPONSE updateData, final byte callId) {

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
								if(!articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) && articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
									//imageView.loadUrl(articleDetails.getUrl());
								}else if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO)){
									//imageView.loadUrl(articleDetails.getThumbUrl());
									//txt_player_select.setVisibility(View.VISIBLE);
									PlayerActivity.streamUrl = articleDetails.getUrl();
									PlayerActivity.isAudio = true;
								}else{
									//imageView.loadUrl(articleDetails.getThumbUrl());
									//txt_player_select.setVisibility(View.VISIBLE);
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
						}
					}else if(callId == Constants.REQ_GETREVIEWS){
						txt_reviews.setVisibility(View.VISIBLE);
						List<ReviewDAO> reviewList = (List<ReviewDAO>) CacheManager.getInstance().get(Constants.C_REVIEWS);
						ReviewDAO reviewDAO = null;
						StringBuilder reviews = new StringBuilder();
						if(reviewList != null && reviewList.size() > 0){
							reviews.append("<b>Reviews:</b><br>");
							for(int i = 0; i < reviewList.size(); i++){
								reviewDAO = reviewList.get(i);

								reviews.append(reviewDAO.getReviewComments() + "<br>");
								if(reviewDAO.getUserName() != null && !reviewDAO.getUserName().equalsIgnoreCase("")){
									reviews.append("Submitted by: " + reviewDAO.getUserName());
								}
								if(!reviewDAO.getReviewDate().equals(""))
									reviews.append("<i>" + reviewDAO.getReviewDate() + "</i> <br><br>");

							}

							txt_reviews.setTextColor(Color.BLACK);
							txt_reviews.setTextSize(15);
							txt_reviews.setText(Html.fromHtml(reviews.toString()));
						}else{
							txt_reviews.setText("No Reviews submitted yet.");
						}
						//txt_reviews.setText();
					}else if(callId == Constants.REQ_SUBMITREVIEW){
						Utils.displayMessage(getApplicationContext(), getString(R.string.review_submitted));

						//update the reviews
						getReviews(articleDetails);
					}
				}
			});
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_OPTION_SAVED:
			Toast.makeText(getApplicationContext(),"Implimentation is in Progress...", Toast.LENGTH_LONG).show();
			break;
			case MENU_OPTION_SHARE:
			//getReviews(articleDetails);
			Toast.makeText(getApplicationContext(),"Implimentation is in Progress...", Toast.LENGTH_LONG).show();
			break;

		case MENU_OPTION_ADD_REVIEW:
			showDialog(Constants.DIALOG_REVIEW);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.DIALOG_REVIEW:
			ReviewDialog reviewDialog = new ReviewDialog(this, articleDetails);
			return reviewDialog;
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

}

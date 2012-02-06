package com.codegreen.ui.activity;

import java.util.List;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.DownloadHandler;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class ArticleDetailsActivity extends Activity implements Updatable{

	String strSelectedArticleType = Constants.ARTCLETYPE_TEXT;
	String strSelectedArticleID = "";
	String TAG = "ArticleDetailsActivity";
	LinearLayout progress_Lay = null;
	ImageView imageView = null;
	private static final int MENU_OPTION_SAVED = 0x01;
	private static final int MENU_OPTION_SEARCH = 0x02;
	private static final int MENU_OPTION_SHARE = 0x03;
	private static final int MENU_OPTION_PLAY = 0x06;
	private TextView txt_reviews = null;
	private static final int MENU_OPTION_ADD_REVIEW = 0x04;
	private static final int MENU_OPTION_SAVE = 0x05;
	ArticleDAO articleDetails;
	TextView txt_player_select = null;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private boolean notAFling = false;

	private LinearLayout scrollLinearlayout;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 300;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private boolean savedArticle;
	private ViewFlipper mFlipper;
	private final int SHOW_PROGRESS = 1;
	private final int REMOVE_PROGRESS = 2;
	TextView txtDetails = null;
	TextView txtTitle = null;
	TextView txtDate = null;
	private List<ArticleDAO> listOfArticles ;
	private ProgressDialog progressDialog;
	Typeface _tfBigBold, _tfMediumBold, _tfSmallNormal;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		_tfBigBold = Typeface.createFromAsset(getAssets(), Constants.SANS_BOLD); 
		_tfMediumBold =  Typeface.createFromAsset(getAssets(), Constants.SANS_SEMIBOLD);
		_tfSmallNormal = Typeface.createFromAsset(getAssets(), Constants.SANS_BOOK);
		if(getIntent() != null){
			strSelectedArticleType = getIntent().getStringExtra(Constants.CURRENT_ARTICLE_TYPE);
			strSelectedArticleID = getIntent().getStringExtra("ArticleID");
			savedArticle =  getIntent().getBooleanExtra("savedarticle", false);
		}

		listOfArticles = (List<ArticleDAO>)CacheManager.getInstance().get(Constants.C_SEARCH_ARTICLES);
		if(listOfArticles != null){
			Constants.TOTAL_ARTICLES = listOfArticles.size();
		}
		if(strSelectedArticleType != null){
			setContentView(R.layout.atrticle_text);
			txtDetails = (TextView) findViewById(R.id.article_details_view);
			txtDetails.setTypeface(_tfMediumBold);
			
			txtTitle = (TextView) findViewById(R.id.article_title_view);
			txtTitle.setTypeface(_tfBigBold);
			
			txtDate = (TextView) findViewById(R.id.article_date_view);
			txtDate.setTypeface(_tfSmallNormal);
			
			progress_Lay = (LinearLayout)findViewById(R.id.progress_lay);
			imageView = (ImageView)findViewById(R.id.webview);
			if(strSelectedArticleType == Constants.ARTCLETYPE_TEXT){
				imageView.setBackgroundResource(R.drawable.default_bg_text);
			}else{
				imageView.setBackgroundResource(R.drawable.bg_images_sample);
			}
			
			txt_reviews = (TextView)findViewById(R.id.txt_reviews);
			txt_reviews.setVisibility(View.GONE);
			
			scrollLinearlayout = (LinearLayout)findViewById(R.id.scrollLinearlayout);

			mFlipper = ((ViewFlipper)findViewById(R.id.tutorial_flipper));
			Animation anim = (Animation) AnimationUtils.loadAnimation(ArticleDetailsActivity.this, R.anim.push_left_in);
			findViewById(R.id.content_container).startAnimation(anim);

			gestureDetector = new GestureDetector(new MyGestureDetector());
			gestureListener = new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					boolean rv = gestureDetector.onTouchEvent(event);
					if (rv) {
						event.setAction(MotionEvent.ACTION_CANCEL);
					}
					return false;
				}
				
			};
			txtDetails.setOnTouchListener(gestureListener);
			mFlipper.setOnTouchListener(gestureListener);
			scrollLinearlayout.setOnTouchListener(gestureListener);
			//imageView.setOnTouchListener(gestureListener);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					notAFling = true;
					startPlayer();
				}
			});

			/*txt_reviews.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//getReviews(articleDetails);
				}
			});
*/		}

		if(strSelectedArticleType != null && strSelectedArticleID != null){
			if(!savedArticle){
				getArticleDetails();
			}else{
				update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,Constants.REQ_GETARTICLEDETAILS,(byte)0);
			}
		}
	}

	



	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try{
			menu.removeGroup(0);
			menu.add(0, MENU_OPTION_SEARCH,0 , "Comments").setIcon(android.R.drawable.ic_menu_gallery);
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
							if(articleDetails.getUrl() != null && !articleDetails.getUrl().equals("") && !articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_TEXT) || articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) && !articleDetails.getThumbUrl().equals("") || articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO) && !articleDetails.getThumbUrl().equals("") ){
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

							txtTitle.setText("Title : "+ articleDetails.getTitle());
							txtDate.setText("Date :" + articleDetails.getPublishedDate());
							txtTitle.setVisibility(View.VISIBLE);
							txtDate.setVisibility(View.VISIBLE);
							
							strDetails =  articleDetails.getDetailedDescription();
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
									reviews.append("<b>" + getString(R.string.submitted_by) + "</b>" + " " + reviewDAO.getUserName());
								}else{
									reviews.append(getString(R.string.submitted_by_anonymous));
								}
								if(!reviewDAO.getReviewDate().equals(""))
									reviews.append("<br><i><b>" + "Submitted on:" + "</b>" + reviewDAO.getReviewDate() + "</i> <br><br>");

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
							if(imageView.getDrawable() != null){
								imageView.getDrawable().setCallback(null);
							}
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
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progress_Lay.setVisibility(View.GONE);
					if(callId == Constants.REQ_DOWNLOADARTICLE){
						Toast.makeText(ArticleDetailsActivity.this, "Article cannot be downloaded. Please try later.", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(ArticleDetailsActivity.this, "No Network Available.", Toast.LENGTH_LONG).show();
					}
					Message msg = new Message();
					msg.what = REMOVE_PROGRESS;
					msg.arg1 = REMOVE_PROGRESS;
					handler.sendMessage(msg);
				}
			});

		}

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_OPTION_SEARCH:
			getReviews(articleDetails);
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
			dbAdapter.close();
			if(count > 0){
				Toast.makeText(getApplicationContext(),getString(R.string.saved_already), Toast.LENGTH_LONG).show();
			}


			if(count == 0){
				if(articleDetails.getType().equalsIgnoreCase(Constants.ARTICAL_TYPE_TEXT)){
					saveArticle(articleDetails);
				}else{
					DownloadHandler downloadHandler = DownloadHandler.getInstance();
					downloadHandler.handleEvent(articleDetails, Constants.REQ_DOWNLOADARTICLE, this);

					Message msg = new Message();
					msg.what = SHOW_PROGRESS;
					handler.sendMessage(msg);
				}
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

		String toastMessage = null;
		if(count == 0){
			dbAdapter.insertArticle(article);
			toastMessage = getString(R.string.record_saved);
		}else{
			toastMessage =getString(R.string.saved_already);
		}
		dbAdapter.close();
		Toast.makeText(getApplicationContext(),toastMessage, Toast.LENGTH_LONG).show();


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

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch(what){
			case SHOW_PROGRESS:
				showDialog(Constants.DIALOG_PROGRESS);
				break;

			case REMOVE_PROGRESS:
				if(progressDialog != null){
					progressDialog.dismiss();
					progressDialog.cancel();
				}
				if(msg.arg1 != REMOVE_PROGRESS){
					ArticleDAO dao = (ArticleDAO)CacheManager.getInstance().get(Constants.C_DOWNLOADED_ARTICLE);
					if(dao != null){
						saveArticle(dao);
					}
				}

				break;
			}
		};
	};

	private void startPlayer(){
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if(notAFling){
					if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) 
							|| strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){

						String urlToPlay = articleDetails.getUrl();
						Log.e("---------Play Url------- ", urlToPlay);
						if(urlToPlay.contains("youtube")){
							try{
								Intent videoClient = new Intent(Intent.ACTION_VIEW); 
								videoClient.setData(Uri.parse(urlToPlay)); 
								startActivity(videoClient);
							}catch (Exception e) {
								Toast.makeText(getApplicationContext(),"YouTube Player not found.", Toast.LENGTH_LONG).show();
							}
						}else{
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
				}
			}
		},  1000);
	}
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

	private void showNextArticle(){
		if(Constants.CURRENT_INDEX < Constants.TOTAL_ARTICLES){
			mFlipper.clearAnimation();
			mFlipper.setInAnimation(getApplicationContext(),R.anim.push_left_in);
			mFlipper.setOutAnimation(getApplicationContext(),R.anim.push_left_out); 
			mFlipper.showNext();
			Constants.CURRENT_INDEX++;
			getArticle();
		}else{
			Toast.makeText(this, "This is last article.", Toast.LENGTH_SHORT).show();
		}
	}


	private void getArticle(){
		try {
			if(listOfArticles != null && Constants.CURRENT_INDEX < listOfArticles.size()){
				this.articleDetails = listOfArticles.get(Constants.CURRENT_INDEX);
				strSelectedArticleID = articleDetails.getArticleID();
				strSelectedArticleType = articleDetails.getType();
				txtDetails.setVisibility(View.GONE);
				txtTitle.setVisibility(View.GONE);
				txtDate.setVisibility(View.GONE);
				txt_reviews.setVisibility(View.GONE);
				imageView.setImageResource(R.drawable.bg_images_sample);
				getArticleDetails();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void showPreviousArticle(){
		if(Constants.CURRENT_INDEX > 0){
			mFlipper.clearAnimation();
			mFlipper.setOutAnimation(getApplicationContext(),R.anim.push_right_out);
			mFlipper.setInAnimation(getApplicationContext(),R.anim.push_right_in);
			mFlipper.showPrevious();
			Constants.CURRENT_INDEX--;
			getArticle();
		}else{
			Toast.makeText(this, "This is first article.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
	    super.dispatchTouchEvent(ev);
	    return gestureDetector.onTouchEvent(ev);
	} 
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.i("ON FLING", "GESTURE" + Math.abs(e1.getY() - e2.getY()));
			notAFling = true;
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
					return false;
				}
				notAFling = false;
				// right to left swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					showNextArticle();
				}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					showPreviousArticle();
				}
			} catch (Exception e) {
				
			}
			return true;

		}


		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			//Log.i("OnScroll", "distanceX : " + distanceX + " distanceY : " + distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
}

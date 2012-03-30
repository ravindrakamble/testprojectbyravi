package com.codegreen.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.codegreen.R;
import com.codegreen.businessprocess.handler.DownloadHandler;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.common.CacheManager;
import com.codegreen.database.DBAdapter;
import com.codegreen.listener.Updatable;
import com.codegreen.share.Share;
import com.codegreen.ui.dialog.ReviewDialog;
import com.codegreen.ui.dialog.ShareDialog;
import com.codegreen.util.Constants;
import com.codegreen.util.Constants.ENUM_PARSERRESPONSE;
import com.codegreen.util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
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
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class ArticleDetailsActivity extends Activity implements Updatable{

	String strSelectedArticleType = Constants.ARTCLETYPE_TEXT;
	String strSelectedArticleID = "";
	private boolean search = false;
	String TAG = "ArticleDetailsActivity";
	ProgressBar imageViewProgressBar = null;
	ImageView imageView = null;
	private static final int MENU_OPTION_SEARCH = 0x02;
	private static final int MENU_OPTION_SHARE = 0x03;
	private TextView txt_reviews = null;
	private static final int MENU_OPTION_ADD_REVIEW = 0x04;
	private static final int MENU_OPTION_SAVE = 0x05;
	ArticleDAO articleDetails;
	TextView txt_player_select = null;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private boolean notAFling = false;
	TextView progress_text = null;
	private RelativeLayout scrollLinearlayout;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 300;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private boolean savedArticle;
	private ViewFlipper mFlipper;
	private final int SHOW_PROGRESS = 1;
	private final int REMOVE_PROGRESS = 2;
	private final int SHOW_REVIEW_PROGRESS = 3;
	private final int REMOVE_REVIEW_PROGRESS = 4;
	TextView txtDetails = null;
	TextView txtTitle = null;
	TextView txtDate = null;
	TextView txtAuther = null;
	private List<ArticleDAO> listOfArticles ;
	private ProgressDialog progressDialog;
	Typeface _tfBigBold, _tfMediumBold, _tfSmallNormal;
	RelativeLayout lay_main = null;
	private String shortDesc = null;
	private String thumbUrl;
	ArrayList<ArticleDAO> advertiseData = null;
	ImageView addsImage = null;
	Timer adTimer;
	TimerTask adTask;
	private ProgressDialog reviewProgressDialog = null;
	String progres_text = "Loading article data,Please wait...";
	RelativeLayout lay_imageView = null;
	ImageView play_img = null;

	private boolean playerScreenOpened;
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
			shortDesc = getIntent().getStringExtra("desc");
			thumbUrl = getIntent().getStringExtra("thumburl");
			search = getIntent().getBooleanExtra("search", false);
		}

		if(search){
			listOfArticles = (List<ArticleDAO>)CacheManager.getInstance().get(Constants.C_SEARCH_ARTICLES);
		}else{
			listOfArticles = (List<ArticleDAO>)CacheManager.getInstance().get(Constants.C_ARTICLES);
		}
		if(listOfArticles != null){
			Constants.TOTAL_ARTICLES = listOfArticles.size();
		}
		if(strSelectedArticleType != null){
			setContentView(R.layout.atrticle_text);

			LinearLayout lay_main_scroll = (LinearLayout)findViewById(R.id.lay_main_scroll);

			imageViewProgressBar = (ProgressBar) findViewById(R.id.progress_imageView);
			txt_reviews = (TextView)findViewById(R.id.txt_reviews);
			txt_reviews.setVisibility(View.GONE);

			lay_main = (RelativeLayout)findViewById(R.id.lay_details_main);
			txtDetails = (TextView) findViewById(R.id.article_details_view);
			txtDetails.setTypeface(_tfMediumBold);

			txtTitle = (TextView) findViewById(R.id.article_title_view);
			txtTitle.setTypeface(_tfBigBold);

			txtDate = (TextView) findViewById(R.id.article_date_view);
			txtDate.setTypeface(_tfSmallNormal);

			txtAuther = (TextView)findViewById(R.id.article_auther_view);
			txtAuther.setTypeface(_tfSmallNormal);

			//progress_Lay = (LinearLayout)findViewById(R.id.progress_lay);
			imageView = (ImageView)findViewById(R.id.webview);

			play_img = (ImageView)findViewById(R.id.btn_play);
			play_img.setVisibility(View.GONE);

			lay_imageView = (RelativeLayout)findViewById(R.id.image_layout);

			progress_text = (TextView) findViewById(R.id.progress_text_color);

			if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_TEXT)){
				lay_main.setBackgroundColor(Color.WHITE);
				txtTitle.setTextColor(Color.BLACK);
				txtDate.setTextColor(Color.BLACK);
				txtDetails.setTextColor(Color.BLACK);
				txt_reviews.setTextColor(Color.BLACK);
				progress_text.setTextColor(Color.BLACK);

			}else{
				lay_main.setBackgroundColor(Color.BLACK);
				txtTitle.setTextColor(Color.WHITE);
				txtDate.setTextColor(Color.WHITE);
				txtDetails.setTextColor(Color.WHITE);
				txt_reviews.setTextColor(Color.WHITE);
				progress_text.setTextColor(Color.WHITE);
			}


			scrollLinearlayout = (RelativeLayout)findViewById(R.id.scrollLinearlayout);
			addsImage = (ImageView)findViewById(R.id.admarveldetailsscreen);
			addsImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					showadDetails();

				}
			});

			mFlipper = ((ViewFlipper)findViewById(R.id.tutorial_flipper));
			Animation anim = (Animation) AnimationUtils.
			loadAnimation(ArticleDetailsActivity.this, R.anim.push_left_in);
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
			txtTitle.setOnTouchListener(gestureListener);
			txtDate.setOnTouchListener(gestureListener);
			mFlipper.setOnTouchListener(gestureListener);
			txtAuther.setOnTouchListener(gestureListener);
			scrollLinearlayout.setOnTouchListener(gestureListener);
			lay_main_scroll.setOnTouchListener(gestureListener);
			lay_imageView.setOnTouchListener(gestureListener);
			//imageView.setOnTouchListener(gestureListener);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					notAFling = true;
					startPlayer();
				}
			});

		}

		if(strSelectedArticleType != null && strSelectedArticleID != null){
			if(!savedArticle){
				getArticleDetails();
			}else{
				listOfArticles = (ArrayList<ArticleDAO>)CacheManager.getInstance().get(Constants.C_SAVED_ARTICLES);
				update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,Constants.REQ_GETARTICLEDETAILS,(byte)0);
			}
		}
		updateAdvertisements();
	}



	private void showadDetails(){
		ArrayList<ArticleDAO> advertiseData = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ADVERTISMENTS);

		if(advertiseData != null){
			Bitmap[] data = advertiseData.get(Constants.CURRENT_AD_INDEX).getAddsBitmap();
			if(data != null && data.length == 2 && data[1] != null){
				Intent intent = new Intent(getApplicationContext(), AdDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("adindex", Constants.CURRENT_AD_INDEX);
				startActivity(intent);
			}
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		try{
			DBAdapter dbAdapter = DBAdapter.getInstance(getApplicationContext());
			dbAdapter.open();
			int count =  dbAdapter.getArticles(articleDetails);
			dbAdapter.close();
			menu.removeGroup(0);
			menu.add(0, MENU_OPTION_SEARCH,0 , "Comments").setIcon(android.R.drawable.ic_menu_gallery);
			menu.add(0, MENU_OPTION_SHARE,0 , "Share").setIcon(android.R.drawable.ic_menu_share);
			if(!savedArticle && count == 0){
			menu.add(0, MENU_OPTION_SAVE,0 , "Save").setIcon(android.R.drawable.ic_menu_add);
			}

			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			addsImage.setVisibility(View.GONE);
		}else{
			addsImage.setVisibility(View.VISIBLE);
		}
	}



	/**
	 * WS call to get article details
	 */
	private void getArticleDetails(){
		if(savedArticle){
			update(Constants.ENUM_PARSERRESPONSE.PARSERRESPONSE_SUCCESS,Constants.REQ_GETARTICLEDETAILS,(byte)0);
		}else{
			if(Utils.isNetworkAvail(getApplicationContext())){
				//progress_Lay.setVisibility(View.VISIBLE);
				Message msg = new Message();
			msg.what = SHOW_PROGRESS;
			handler.sendMessage(msg);
				HttpHandler httpHandler =  HttpHandler.getInstance();
				ArticleDAO articleDAO = new ArticleDAO();
				articleDAO.setArticleID(strSelectedArticleID);
				articleDAO.setType(strSelectedArticleType);
				articleDAO.setCategoryID(Constants.CURRENT_CATEGORY_TYPE);
				httpHandler.handleEvent(articleDAO, Constants.REQ_GETARTICLEDETAILS, this);
			}else{
				Toast.makeText(getApplicationContext(), "No Network Available.", Toast.LENGTH_SHORT).show();
			}
		}
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
					if(callId == Constants.REQ_GETARTICLEDETAILS){
						String strDetails = "";
						if(savedArticle){
							articleDetails = listOfArticles.get(Constants.CURRENT_INDEX);
						}else{
							articleDetails = (ArticleDAO) CacheManager.getInstance().get(Constants.C_ARTICLE_DETAILS);
						}
						if(articleDetails != null){
							if(!savedArticle){
								articleDetails.setThumbUrl(thumbUrl);
								articleDetails.setShortDescription(shortDesc);
							}
							// Set the flags in case of audio 
							if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO)){
								PlayerActivity.streamUrl = articleDetails.getUrl();
								PlayerActivity.isAudio = true;
							}else if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
								PlayerActivity.streamUrl = articleDetails.getUrl();
								PlayerActivity.isAudio = false;
							}
							/////////////////////// For Image ///////////////////////////////////////////
							// If text/image then check for url it should not be null
							txtTitle.setText(articleDetails.getTitle());
							txtDate.setText(articleDetails.getPublishedDate());
							txtTitle.setVisibility(View.VISIBLE);
							txtDate.setVisibility(View.VISIBLE);
							txtAuther.setText(articleDetails.getAuthor());
							txtAuther.setVisibility(View.VISIBLE);

							strDetails =  articleDetails.getDetailedDescription();
							if(strDetails != null && !strDetails.equals("")){

								txtDetails.setVisibility(View.VISIBLE);
								txtDetails.setText(strDetails);
							}
							else{
								txtDetails.setVisibility(View.INVISIBLE);
							}
							txt_reviews.setVisibility(View.INVISIBLE);

							Message msg = new Message();
							msg.what = REMOVE_PROGRESS;
							msg.arg1 = REMOVE_PROGRESS;
							handler.sendMessage(msg);

							if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_TEXT) || articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_IMAGE)){

								if(articleDetails.getUrl()== null || articleDetails.getUrl().equals("")){
									imageView.setVisibility(View.GONE);
									lay_imageView.setVisibility(View.GONE);
									play_img.setVisibility(View.GONE);
								}else{

									// Display by default
									imageView.setVisibility(View.VISIBLE);
									lay_imageView.setVisibility(View.VISIBLE);
									// then download image with thumbnail url
									downloadImage(articleDetails.getUrl());
								}

								// If audio/ video then use thumbnail to display if thumbnail is null then display default image
							}else if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) || articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
								if(articleDetails.getThumbUrl() == null || articleDetails.getThumbUrl().equals("")){
									imageView.setImageResource(R.drawable.bg_images_sample);
									lay_imageView.setVisibility(View.VISIBLE);
									play_img.setVisibility(View.VISIBLE);
									imageViewProgressBar.setVisibility(View.GONE);
								}else{
									play_img.setVisibility(View.GONE);
									// then download image with thumbnail url
									downloadImage(articleDetails.getThumbUrl());
								}
							}
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

							txt_reviews.setTextSize(15);
							txt_reviews.setText(Html.fromHtml(reviews.toString()));
						}else{
							txt_reviews.setText(getString(R.string.no_reviews));
						}

						Message msg = new Message();
						msg.what = REMOVE_REVIEW_PROGRESS;
						handler.sendMessage(msg);

					}else if(callId == Constants.REQ_SUBMITREVIEW){
						Utils.displayMessage(getApplicationContext(), getString(R.string.review_submitted));
						//update the reviews
						getReviews(articleDetails);
					}else if(callId == Constants.REQ_DOWNLOADIMAGE){

						imageViewProgressBar.setVisibility(View.GONE);
						if(CacheManager.getInstance().getLatestArticleBitmap() != null){
							// Play image should display on 
							if(!articleDetails.getType().equalsIgnoreCase(Constants.ARTICAL_TYPE_IMAGE) && !articleDetails.getType().equalsIgnoreCase(Constants.ARTICAL_TYPE_TEXT)){
								play_img.setVisibility(View.VISIBLE);
							}else{
								play_img.setVisibility(View.GONE);
							}

							if(imageView.getDrawable() != null){
								imageView.getDrawable().setCallback(null);
							}
							imageView.setBackgroundDrawable(null);
							imageView.setImageBitmap(CacheManager.getInstance().getLatestArticleBitmap());
							CacheManager.getInstance().setLatestArticleBitmap(null);
						}else{
							if(articleDetails.getType().equals(Constants.ARTCLETYPE_AUDIO) || articleDetails.getType().equals(Constants.ARTCLETYPE_VIDEO)){
								imageView.setImageResource(R.drawable.bg_images_sample);
								imageView.setVisibility(View.VISIBLE);
								play_img.setVisibility(View.VISIBLE);
								lay_imageView.setVisibility(View.VISIBLE);
							}else{
								lay_imageView.setVisibility(View.GONE);
								imageView.setVisibility(View.GONE);
								play_img.setVisibility(View.GONE);
							}
							//Toast.makeText(getApplicationContext(),"Image download failed.", Toast.LENGTH_SHORT).show();
						}
					}else if(callId == Constants.REQ_DOWNLOADARTICLE){

						Message msg = new Message();
						msg.what = REMOVE_PROGRESS;
						msg.arg1 = 5;
						handler.sendMessage(msg);
					}
				}
			});
		}else if(errorCode == Constants.ERR_NETWORK_FAILURE){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(callId == Constants.REQ_DOWNLOADARTICLE){
						Toast.makeText(ArticleDetailsActivity.this, "Article cannot be saved at this time. Please try later.", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(ArticleDetailsActivity.this, "No Network Available.", Toast.LENGTH_SHORT).show();
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
	protected void onDestroy() {
		if(adTask != null){
			adTask.cancel();
		}
		if(adTimer != null){
			adTimer.cancel();
		}
		HttpHandler httpHandler =  HttpHandler.getInstance();
		//Cancel previous request;
		httpHandler.cancelRequest();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_OPTION_SEARCH:
			try{
				Context cxt = getApplicationContext();
				Intent intent = new Intent(cxt, ReviewsActivity.class);     		
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("SelectedType", strSelectedArticleType);
				intent.putExtra("selectedID", strSelectedArticleID);
				intent.putExtra("ArticleName", articleDetails.getTitle());
				startActivity(intent);
			}catch (Exception e) {
				e.printStackTrace();
			}
			//getReviews(articleDetails);
			break;
		case MENU_OPTION_SHARE:
			//showDialog(Constants.DIALOG_SHARE);
			showShareDialog();
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
				DownloadHandler downloadHandler = DownloadHandler.getInstance();
				downloadHandler.cleardata();
				downloadHandler.handleEvent(articleDetails, Constants.REQ_DOWNLOADARTICLE, this);
				if(articleDetails != null){
					if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO)){
						progres_text = "Saving audio Please wait...";
					}else if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
						progres_text = "Saving video Please wait...";
					}else if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_IMAGE)){
						progres_text = "Saving image Please wait...";
					}else {
						progres_text = "Saving article Please wait...";
					}
				}
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

	
	private void showShareDialog() {
			final CharSequence[] items = {"Facebook", "Twitter", "Email"};
			AlertDialog.Builder builder = new AlertDialog.Builder(ArticleDetailsActivity.this);
			builder.setTitle("Share using...");
			builder.setIcon(R.drawable.icon);
			builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					CacheManager.getInstance().resetAllArticles();
					if(item == 0){//Facebook
						onSelectFacebook();
					}
					else if(item == 1){//Twiiter
						onSelectTwitter();
					}	
					else if(item == 2){//Email
						onSelectEmail();	
					}
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
	}
	
	
	String message = "";
	
	/**
	 * On email selection
	 */
	private void onSelectEmail() {
		Spanned message = Html.fromHtml(articleDetails.getTitle() + "<br/><br/>"+ articleDetails.getDetailedDescription());
		String subject = "Sending Article details for " + articleDetails.getTitle();
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		if(articleDetails.getUrl() != null)
			emailIntent.putExtra(Intent.EXTRA_STREAM, articleDetails.getUrl()); 
		
		startActivity(Intent.createChooser(emailIntent, "Send Email.."));
	}
	
	private void onSelectTwitter(){
		Share testShare = new Share(ArticleDetailsActivity.this);
		if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_TEXT))
			message = articleDetails.getTitle()+ "\n www.codegreenonline.com"; // HeadLine + Website Link
		else
			message = articleDetails.getTitle() + "\n" + articleDetails.getUrl(); // for vedio/ audio/image = HeadeLine + URL
		testShare.share(message, Share.TYPE_TWITTER,articleDetails);
	}

	
	private void onSelectFacebook(){
		Share testShare = new Share(ArticleDetailsActivity.this);
		testShare.share(message, Share.TYPE_FACEBOOK,articleDetails);
	}

	private void saveArticle(ArticleDAO article){
		DBAdapter dbAdapter = DBAdapter.getInstance(getApplicationContext());
		dbAdapter.open();
		int count =  dbAdapter.getArticles(article);

		String toastMessage = null;
		if(count == 0){
			article.setShortDescription(shortDesc);
			dbAdapter.insertArticle(article);
			if(article != null){
				if(article.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO)){
					toastMessage = "Audio saved successfully.";
				}else if(article.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
					toastMessage = "Video saved successfully.";
				}else if(article.getType().equalsIgnoreCase(Constants.ARTCLETYPE_IMAGE)){
					toastMessage = "Image saved successfully.";
				}else{
					toastMessage = "Article saved successfully.";
				}
			}
		}else{
			if(article != null){
				if(article.getType().equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO)){
					toastMessage = "Audio already saved.";
				}else if(article.getType().equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){
					toastMessage = "Video already saved.";
				}else if(article.getType().equalsIgnoreCase(Constants.ARTCLETYPE_IMAGE)){
					toastMessage = "Image already saved";
				}else{
					toastMessage =getString(R.string.saved_already);
				}
			}
		}
		
		dbAdapter.close();
		Toast.makeText(getApplicationContext(),toastMessage, Toast.LENGTH_LONG).show();
		CacheManager.getInstance().removeFromCache(Constants.C_DOWNLOADED_ARTICLE);
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
			showProgressBar();
			return progressDialog;
		case Constants.DIALOG_REVIEW_PROGRESS:
			showReviewProgressBar();
			return reviewProgressDialog;
		default:
			break;
		}
		return null;
	}

	private void getReviews(ArticleDAO articleDAO){
		try {
			if(Utils.isNetworkAvail(getApplicationContext())){
				HttpHandler httpHandler =  HttpHandler.getInstance();
				//Cancel previous request;
				httpHandler.cancelRequest();

				//Start progress bar
				Message msg = new Message();
				msg.what = SHOW_REVIEW_PROGRESS;
				handler.sendMessage(msg);

				//Prepare data for new request
				ReviewDAO reviewDAO = new ReviewDAO();
				reviewDAO.setArticleID(articleDAO.getArticleID());
				reviewDAO.setArticleType(articleDAO.getType());
				//Send request
				httpHandler.setApplicationContext(getApplicationContext());
				httpHandler.handleEvent(reviewDAO, Constants.REQ_GETREVIEWS, this);
			}else{
				Toast.makeText(getApplicationContext(), "No Network Available", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadImage(String url){
		if(Utils.isNetworkAvail(getApplicationContext())){
			imageViewProgressBar.setVisibility(View.VISIBLE);
			HttpHandler httpHandler =  HttpHandler.getInstance();
			//Cancel previous request;
			httpHandler.cancelRequest();
			if(url != null && !url.equals(""))
				httpHandler.handleEvent(url, Constants.REQ_DOWNLOADIMAGE, ArticleDetailsActivity.this);
		}else
			Toast.makeText(getApplicationContext(), "No Network Available", Toast.LENGTH_SHORT).show();
	}

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch(what){
			case SHOW_PROGRESS:
				showDialog(Constants.DIALOG_PROGRESS);
				break;
			case SHOW_REVIEW_PROGRESS:
				showDialog(Constants.DIALOG_REVIEW_PROGRESS);
				break;
			case REMOVE_REVIEW_PROGRESS:
				if(reviewProgressDialog != null){
					reviewProgressDialog.dismiss();
					reviewProgressDialog.cancel();
				}
				break;
			case REMOVE_PROGRESS:
				// remove progress dialog
				removeDialog(Constants.DIALOG_PROGRESS);
				if(msg.arg1 == 5){
					ArticleDAO dao = (ArticleDAO)CacheManager.getInstance().get(Constants.C_DOWNLOADED_ARTICLE);
					if(dao != null){
						saveArticle(dao);
						CacheManager.getInstance().removeFromCache(Constants.C_DOWNLOADED_ARTICLE);
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
				if(notAFling && !playerScreenOpened){
					if(strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_AUDIO) 
							|| strSelectedArticleType.equalsIgnoreCase(Constants.ARTCLETYPE_VIDEO)){

						progres_text = "Downloading media data, please wait...";

						Message msg = new Message();
						msg.what = SHOW_PROGRESS;
						handler.sendMessage(msg);

						String urlToPlay = articleDetails.getUrl();
						Log.e("---------Play Url------- ", ""+ urlToPlay);
						playerScreenOpened = true;
						if(urlToPlay != null && urlToPlay.contains("youtube")){
							try{
								Intent videoClient = new Intent(Intent.ACTION_VIEW); 
								videoClient.setData(Uri.parse(urlToPlay));
								//progress_Lay.setVisibility(View.GONE);
								msg = new Message();
								msg.what = REMOVE_PROGRESS;
								handler.sendMessage(msg);
								startActivity(videoClient);
							}catch (Exception e) {
								Toast.makeText(getApplicationContext(),"YouTube Player not found.", Toast.LENGTH_SHORT).show();
							}
						}else{
							Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra(Constants.CURRENT_ARTICLE_TYPE, strSelectedArticleType);
							intent.putExtra("ArticleID", strSelectedArticleID);
							if(savedArticle){
								intent.putExtra("savedarticle", articleDetails.getUrl());
								intent.putExtra("savedarticleTitle", articleDetails.getTitle());
							}
							//progress_Lay.setVisibility(View.GONE);
							msg = new Message();
							msg.what = REMOVE_PROGRESS;
							handler.sendMessage(msg);
							startActivity(intent);
						}
					}
				}
			}
		},  1000);
	}

	@Override
	protected void onResume() {
		playerScreenOpened = false;
		super.onResume();
	}
	private void showProgressBar(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(progres_text);
		progressDialog.setIcon(android.R.id.icon);
		progressDialog.setCancelable(false);
		progressDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});
	}

	/**
	 * 
	 */
	private void showReviewProgressBar(){

		if(reviewProgressDialog == null){
			reviewProgressDialog = new ProgressDialog(this);
			reviewProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			reviewProgressDialog.setMessage("Downloading reviews,please wait...");
			reviewProgressDialog.setIcon(android.R.id.icon);
			reviewProgressDialog.setCancelable(false);
			reviewProgressDialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
		}
	}

	private void showNextArticle(){
		if(listOfArticles != null){
			Constants.TOTAL_ARTICLES = listOfArticles.size();
		}
		if(Constants.TOTAL_ARTICLES > 1){
			Log.i("Current index:" + Constants.CURRENT_INDEX, "Total articles:"  + Constants.TOTAL_ARTICLES);
			if(Constants.CURRENT_INDEX < (Constants.TOTAL_ARTICLES - 1)){
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
	}


	private void getArticle(){
		try {
			if(listOfArticles != null && Constants.CURRENT_INDEX < listOfArticles.size()){
				this.articleDetails = listOfArticles.get(Constants.CURRENT_INDEX);
				if(articleDetails.getType().equalsIgnoreCase(Constants.ARTCLETYPE_TEXT)){
					lay_main.setBackgroundColor(Color.WHITE);
					txtTitle.setTextColor(Color.BLACK);
					txtDate.setTextColor(Color.BLACK);
					txtAuther.setTextColor(Color.BLACK);
					txtDetails.setTextColor(Color.BLACK);
					txt_reviews.setTextColor(Color.BLACK);
					progress_text.setTextColor(Color.BLACK);
				}else{
					lay_main.setBackgroundColor(Color.BLACK);
					txtTitle.setTextColor(Color.WHITE);
					txtDate.setTextColor(Color.WHITE);
					txtAuther.setTextColor(Color.WHITE);
					txtDetails.setTextColor(Color.WHITE);
					txt_reviews.setTextColor(Color.WHITE);
					progress_text.setTextColor(Color.WHITE);
				}
				play_img.setVisibility(View.GONE);
				strSelectedArticleID = articleDetails.getArticleID();
				strSelectedArticleType = articleDetails.getType();
				txtDetails.setVisibility(View.INVISIBLE);
				txtTitle.setVisibility(View.INVISIBLE);
				txtDate.setVisibility(View.INVISIBLE);
				txt_reviews.setVisibility(View.INVISIBLE);
				txtAuther.setVisibility(View.INVISIBLE);
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageResource(R.drawable.default_bg_text);
				getArticleDetails();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void showPreviousArticle(){
		if(listOfArticles != null){
			Constants.TOTAL_ARTICLES = listOfArticles.size();
		}
		if(Constants.TOTAL_ARTICLES > 1){
			Log.i("Current index:" + Constants.CURRENT_INDEX, "Total articles:"  + Constants.TOTAL_ARTICLES);
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
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return super.onTouchEvent(event);
	}

	/**
	 * update adds
	 */
	private void updateAdvertisements(){
		adTimer = new Timer(); 

		final Handler handler = new Handler();

		adTask = new TimerTask() {       
			public void run() {         
				handler.post(new Runnable() {   
					public void run() {
						if(advertiseData == null){
							advertiseData = (ArrayList<ArticleDAO>) CacheManager.getInstance().get(Constants.C_ADVERTISMENTS);
						}
						Log.i("Advertisments ", "Display started");
						if(advertiseData != null){
							if(Constants.CURRENT_AD_INDEX < (advertiseData.size() - 1)){
								Constants.CURRENT_AD_INDEX++;
							}else
							{
								Constants.CURRENT_AD_INDEX = 0;
							}
							Bitmap[] data = advertiseData.get(Constants.CURRENT_AD_INDEX).getAddsBitmap();
							if(data != null && data[0] != null){
								addsImage.setImageBitmap(data[0]);
							}
						}
					}       
				}); 
			}
		};
		adTimer.schedule(adTask, 2000, 30000);
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

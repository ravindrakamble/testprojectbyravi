package com.codegreen.ui.dialog;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.share.DialogError;
import com.codegreen.share.Facebook;
import com.codegreen.share.Facebook.DialogListener;
import com.codegreen.share.FacebookError;
import com.codegreen.util.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShareDialog extends AlertDialog implements OnClickListener{

	//Views
	private Context mContext = null;
	Button btn_facebook = null;
	Button btn_twitter = null;
	Button btn_email = null;
	Button btn_cancel = null;
	private ArticleDAO articleDAO;
	
	private static final String APP_ID = "269876589726953";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};

	private static final String TOKEN = "access_token";
        private static final String EXPIRES = "expires_in";
        private static final String KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost;


	
	protected ShareDialog(Context context) {
		super(context);
	}
	
	/**
	 * Constructor for class <strong>CustomDialog</strong>,
	 * to get dialogue with default theme.
	 * @param context
	 */
	public ShareDialog(Context context, ArticleDAO articleDAO) {
		super(context, android.R.style.Theme_Dialog); 
		mContext = context;
		this.articleDAO = articleDAO;
		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);
		String facebookMessage = articleDAO.getArticleID();
		if (facebookMessage == null){
			facebookMessage = "Test wall post";
		}
		messageToPost = facebookMessage;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initDialogLayout();
	}


	/**
	 * To initialize Custom Dialog's view.
	 */
	private void initDialogLayout(){
		setContentView(R.layout.share_dialog);
		setTitle("Share Using...");
		
		//Assign the controls
		btn_facebook = (Button)findViewById(R.id.btn_facebook);
		//Set text
		btn_twitter = (Button)findViewById(R.id.btn_twitter);
		
		btn_email = (Button)findViewById(R.id.btn_email);
		btn_cancel = (Button)findViewById(R.id.btn_cancel);
		
		btn_facebook.setOnClickListener(this);
		btn_twitter.setOnClickListener(this);
		btn_email.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		
	}	
	

	@Override
	public void dismiss() {
		super.dismiss();	
	}

	@Override
	public void cancel() {
		super.cancel();
	}

	@Override
	public void onClick(View view) {
		if(view == btn_facebook){
			this.cancel();
			this.dismiss();
			share();
		}else if(view == btn_email){
			onSelectEmail();
			this.cancel();
			this.dismiss();
		}else if(view == btn_twitter){
			
		}else if(view == btn_cancel){
			this.cancel();
			this.dismiss();
		}
	}
	
	/**
	 * On email selection
	 */
	private void onSelectEmail() {
		Spanned message = Html.fromHtml("Article Title : " + articleDAO.getTitle() + "<br/>"+ "Article Description :" + articleDAO.getDetailedDescription());
		String subject = "Sending Article details of " + articleDAO.getTitle();
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		mContext.startActivity(Intent.createChooser(emailIntent, "Send Email.."));
	}
	
	private void OnFaceBookSelection(){
		
	}

	
	public boolean saveCredentials(Facebook facebook) {
    	Editor editor = mContext.getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
    	editor.putString(TOKEN, facebook.getAccessToken());
    	editor.putLong(EXPIRES, facebook.getAccessExpires());
    	return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
    	SharedPreferences sharedPreferences = mContext.getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
    	return facebook.isSessionValid();
	}

	
	public void share(){
		if (! facebook.isSessionValid()) {
			loginAndPostToWall();
		}
		else {
			postToWall(messageToPost);
		}
	}

	public void loginAndPostToWall(){
		 facebook.authorize((Activity)mContext, PERMISSIONS, new LoginDialogListener());
	}

	public void postToWall(String message){
		Bundle parameters = new Bundle();
                parameters.putString("message", message);
                parameters.putString("description", "topic share");
                try {
        	        facebook.request("me");
			String response = facebook.request("me/feed", parameters, "POST");
			Log.d("Tests", "got response: " + response);
			if (response == null || response.equals("") ||
			        response.equals("false")) {
				showToast("Blank response.");
			}
			else {
				showToast("Message posted to your facebook wall!");
			}
			 dismiss();
		        cancel();
		} catch (Exception e) {
			showToast("Failed to post to wall!");
			e.printStackTrace();
			 dismiss();
		        cancel();
		}
	}
	
	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	if (messageToPost != null){
			postToWall(messageToPost);
		}
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("Authentication with Facebook failed!");
	    	 dismiss();
		     cancel();
	    }
	    public void onError(DialogError error) {
	    	showToast("Authentication with Facebook failed!");
	    	 dismiss();
		     cancel();
	    }
	    public void onCancel() {
	    	showToast("Authentication with Facebook cancelled!");
	        dismiss();
	        cancel();
	    }
	}

	private void showToast(String message){
		Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}


	
	

}

package com.codegreen.ui.dialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.share.Share;
import com.codegreen.share.ShareData;
import com.codegreen.share.DialogError;
import com.codegreen.util.Constants;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	private Twitter twitter;
	final private int TWITTER_DIALOG = 12345;
	private static String userString;
	private static String passString;
	private final Handler mTwitterHandler = new Handler();
	private SharedPreferences prefs;
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private DefaultOAuthProvider httpOauthprovider;
	private static String toast_message="";

	private static final String APP_ID = "269876589726953";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};

	private static final String TOKEN = "access_token";
        private static final String EXPIRES = "expires_in";
        private static final String KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost;
	private final static int TOASET_MSG = 0;
	private final static int DISMISS = 1;
	private final static int SHOW = 2;
	TextView status_msg;
	String message = "";



	
	protected ShareDialog(Context context) {
		super(context);
	}

	/**
	 * Constructor for class <strong>CustomDialog</strong>,
	 * to get dialogue with default theme.
	 * @param context
	 */
	public ShareDialog(Context context, ArticleDAO articleDAO,SharedPreferences pref) {
		super(context); 
		mContext = context;
		this.articleDAO = articleDAO; 
		/*facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);*/
		String facebookMessage = "Posting Article " +  articleDAO.getDetailedDescription();
		if (facebookMessage == null){
			facebookMessage = "Test wall post";
		}
		messageToPost = facebookMessage;
		message = "Posting Article " +  articleDAO.getDetailedDescription();
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
			//share();
			onSelectFacebook();
		}else if(view == btn_email){
			onSelectEmail();
			this.cancel();
			this.dismiss();
		}else if(view == btn_twitter){
			this.cancel();
			this.dismiss();
			onSelectTwitter();
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
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(articleDAO.getUrl()));
		mContext.startActivity(Intent.createChooser(emailIntent, "Send Email.."));
	}
	
	private void validateTwitter(String user, String pass){

		try {
			httpOauthConsumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
			httpOauthprovider = new DefaultOAuthProvider("https://twitter.com/oauth/request_token", 
					"https://twitter.com/oauth/access_token", 
					"https://twitter.com/oauth/authorize");
			String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, "oob");

			System.out.println("-------------------------------------------------------" + authUrl + "---------------------------------------");
			System.out.println(authUrl.substring(authUrl.indexOf("=") + 1));

			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(new HttpGet(authUrl));

			int status = response.getStatusLine().getStatusCode();

			if (status != HttpStatus.SC_OK){
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				response.getEntity().writeTo(ostream);
			}else{
				StringBuilder buffer = getBody(response);
				HttpPost post = new HttpPost("https://twitter.com/oauth/authorize");

				String temp = buffer.substring(buffer.indexOf("authenticity_token"));
				temp = temp.substring(temp.indexOf("value"));
				temp = temp.substring(temp.indexOf("\"") + 1);
				temp = temp.substring(0, temp.indexOf("\""));

				System.out.println(temp + ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("authenticity_token", temp));
				pairs.add(new BasicNameValuePair("oauth_token", authUrl.substring(authUrl.indexOf("=") + 1)));
				pairs.add(new BasicNameValuePair("session[username_or_email]", user));
				pairs.add(new BasicNameValuePair("session[password]", pass));
				post.setEntity(new UrlEncodedFormEntity(pairs));
				post.getParams().setBooleanParameter("http.protocol.expect-continue", false);

				response = client.execute(post);

				buffer = getBody(response);
				
				if(buffer.indexOf("\"oauth_pin\"") > 0){
		    		temp = buffer.substring(buffer.indexOf("\"oauth_pin\""));
		    		System.out.println("====================" + temp +"----------------------");
		    		temp = temp.substring(temp.indexOf(">")+1,temp.indexOf("<"));
		    		temp = temp.trim();
		    		System.out.println("====================" + temp +"----------------------");
		    		httpOauthprovider.retrieveAccessToken(httpOauthConsumer, temp);

		    		AccessToken accessToken = new AccessToken(httpOauthConsumer.getToken(), httpOauthConsumer.getTokenSecret());
		    		
		    		ShareData.getShareData().put("Twitter_Token", accessToken);
		    		
		    		postTweet(accessToken);
	    		}
	    		else{
	    			toast_message=  "Wrong username/email or password";
					Message msg = new Message();
					msg.what = TOASET_MSG;
					screenHandler.sendMessage(msg);
	    		}
			}
		}catch (Exception e){
			toast_message=  e.getMessage();
			Message msg = new Message();
			msg.what = TOASET_MSG;
			screenHandler.sendMessage(msg);
			e.printStackTrace();
		}
	}
	
	
	private StringBuilder getBody(HttpResponse response) throws Exception {
		InputStream content = response.getEntity().getContent();
		InputStreamReader reader = new InputStreamReader(content, HTTP.DEFAULT_CONTENT_CHARSET);
		StringBuilder buffer = new StringBuilder();

		try{
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1){
				buffer.append(tmp, 0, l);
			}
			return buffer;
		}finally{
			reader.close();
			content.close();
		}
	}


	private void postTweet(AccessToken accessToken){
		try {		
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
			twitter.setOAuthAccessToken(accessToken);
			twitter.updateStatus(message + " -via @MyTestApp");
			System.out.println("Twitter shared data:" + message);
			toast_message=  "Posted Successfully";
			Message msg = new Message();
			msg.what = TOASET_MSG;
			screenHandler.sendMessage(msg);

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}



	Dialog outerDialog;
	
	private void onSelectTwitter(){
		Share testShare = new Share(mContext);
		testShare.share(message, Share.TYPE_TWITTER,articleDAO);
		/*ShareData shareData = ShareData.getShareData();
		outerDialog = new Dialog(mContext);
		showDialog(TWITTER_DIALOG);*/
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
		/*if (! facebook.isSessionValid()) {*/
			//loginAndPostToWall();
	//	}
		/*else {
			postToWall(messageToPost);
		}*/
	}
	
	private void showDialog(int id){
		switch (id){
		case TWITTER_DIALOG:
			outerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			outerDialog.setContentView(R.layout.twitterlogin);
			final TextView user = (TextView) outerDialog.findViewById(R.id.usernameEditText);
			final TextView pass = (TextView) outerDialog.findViewById(R.id.passwordEditText);
			status_msg = (TextView) outerDialog.findViewById(R.id.status_msg);
			Button button = (Button) outerDialog.findViewById(R.id.signinButton);
			status_msg.setText(message);
			button.setOnClickListener(new Button.OnClickListener(){

				public void onClick(View v){
					if ((user.getText() == null || pass.getText() == null) || (user.getText().toString().length() <= 0 || pass.getText().toString().length() <= 0)){
						Message msg = new Message();
						msg.what = TOASET_MSG;
						toast_message= "Please enter username/email and password";
						screenHandler.sendMessage(msg);
					}
					else{
						userString = user.getText().toString();
						passString = pass.getText().toString();
						Message msg = new Message();
						msg.what = DISMISS;
						screenHandler.sendMessage(msg);
						new DoShareTwitter().execute();
					}
				}
			});
			Message msg = new Message();
			msg.what = SHOW;
			screenHandler.sendMessage(msg);
			break;
		}
	}

	public Handler screenHandler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what){
			case TOASET_MSG:
				outerDialog.dismiss();
				outerDialog.cancel();
				Toast.makeText(mContext, toast_message, Toast.LENGTH_LONG).show();
				break;
			case DISMISS:
				outerDialog.dismiss();
				break;
			case SHOW:
				outerDialog.show();
				break;
			}
		}
	};

	class DoShareTwitter extends AsyncTask<String, Void, Void>{
		ProgressDialog dialog;
		int pos;
		protected void onProgressUpdate(Void... progress){
			dialog = new ProgressDialog(mContext);
			dialog.setTitle("");
			dialog.setMessage("Posting Please Wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected void onPostExecute(Void params){
			if (dialog != null){
				dialog.cancel();
			}
		}

		@Override
		protected Void doInBackground(String... params){
			publishProgress();
			if (userString != null && userString.length() > 0 && passString != null && passString.length() > 0){
				validateTwitter(userString, passString);
			}
			return null;
		}

	}

	/*public void loginAndPostToWall(){
		 facebook.authorize((Activity)mContext, PERMISSIONS, new LoginDialogListener());
	}
*/
	/*public void postToWall(String message){
		Bundle parameters = new Bundle();
                parameters.putString("message", message);
                parameters.putString("description", "topic share");
				if (articleDAO.getTitle() != null)
					parameters.putString("message", articleDAO.getTitle());// the
				if (articleDAO.getUrl() != null)
					parameters.putString("link",articleDAO.getUrl());
				if (articleDAO.getThumbUrl() != null)
					parameters.putString("picture", articleDAO.getThumbUrl());
				if (articleDAO.getTitle() != null)
					parameters.putString("name", articleDAO.getTitle());
				if (articleDAO.getShortDescription() != null)
					parameters.putString("caption", "www.codegreenonline.com");
				if (articleDAO.getDetailedDescription() != null)
					parameters.putString("description", articleDAO.getDetailedDescription());
                try {
        	        facebook.request("me");
			String response = facebook.request("me/feed", parameters, "POST");
                facebook.dialog(mContext, "stream.publish", parameters,this);
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
	}*/
	
	
	/*
	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	if (messageToPost != null){
			postToWall(messageToPost);
	    	Bundle parameters = new Bundle();
            parameters.putString("message", message);
            parameters.putString("description", "topic share");
			if (articleDAO.getTitle() != null)
				parameters.putString("message", articleDAO.getTitle());// the
			if (articleDAO.getUrl() != null)
				parameters.putString("link",articleDAO.getUrl());
			if (articleDAO.getThumbUrl() != null)
				parameters.putString("picture", articleDAO.getThumbUrl());
			if (articleDAO.getTitle() != null)
				parameters.putString("name", articleDAO.getTitle());
			if (articleDAO.getShortDescription() != null)
				parameters.putString("caption", "www.codegreenonline.com");
			if (articleDAO.getDetailedDescription() != null)
				parameters.putString("description", articleDAO.getDetailedDescription());
            try {
    	        facebook.request("me");
		String response = facebook.request("me/feed", parameters, "POST");
            facebook.dialog(mContext, "stream.publish", parameters,this);
            }catch (Exception e) {
            	
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
*/
	
	private void onSelectFacebook(){
		Share testShare = new Share(mContext);
		testShare.share(message, Share.TYPE_FACEBOOK,articleDAO);
    }
    
       
	private void showToast(String message){
		Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	} 
	

}

/**
 * =================================================================================================================
 * File Name       : Share.java
 * Author          : Piyush
 * Version         : 
 * Date            : 
 * Copyright       : 
 * Description     : This class will Share the data to different web services(facebook,twitter,email).
 * History         :
 * =====================================================================================================================
 *  Sr. No.  Date        Name                    Reviewed By             Description
 * =====================================================================================================================
 *  01.      		    Piyush		     								Initial Version.
 * =====================================================================================================================
 */
package com.codegreen.share;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
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
import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Share {
	private Twitter twitter;
	private Facebook facebook;
	private static Context context;
	private String message;
	private String subject = "Codegreen : Share via Email";
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private DefaultOAuthProvider httpOauthprovider;

	public static String facebookID = "269876589726953";
	public static String consumerKey = "fR0ggPX4cduwLKzyNTp0w";//1BjA123Qd4IFUuBZJUB47Q"; //79307067a2dee7e3d0cf20fcdd11e3d9
	public static String consumerSecret = "UY9lH4aefr4yhPbxuF3iVcvcDMOROiGAYF9yLWonM8";//"kIDzQl9YZJM1U45x6daDuYfN9EwWLaiYfrdgBR1gL4"; //7f7dd34754f324e76d1e98f956338f20

	final private int TWITTER_DIALOG = 12345;


	final public static int TYPE_FACEBOOK = 55555;
	final public static int TYPE_TWITTER = 55556;
	final public static int TYPE_EMAIL = 55557;

	private static String toast_message="";
	private static String userString;
	private static String passString;
	Dialog outerDialog;
	DoShareTwitter _doShareTwitter;
	ArticleDAO articleDAO = null;

	/**
	 * This is the contructor
	 * @param context
	 */
	public Share(Context context){
		Share.context = context;
	}	

	/**
	 * This method will share the message through particular web service
	 * @param message
	 * @param type
	 */
	public void share(String message,int type,ArticleDAO articleDAO){
		this.message = message;
		this.articleDAO = articleDAO;
		switch(type){
		case TYPE_FACEBOOK: onSelectFacebook();break;
		case TYPE_TWITTER: onSelectTwitter();break;
		case TYPE_EMAIL: onSelectEmail(subject);break;
		default:throw new IllegalArgumentException("please enter the type for facebook,twitter or email");
		}
	}

	/**
	 * This is a setter method, will explicitly set the current context
	 * @param contexts
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * This is a setter method, will explicitly set the subject of an email(used when sharing by email).
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	///////////////////Private Methods not Exposed//////////////////////////
	private void onSelectFacebook(){
		if(facebook == null){
			facebook = new Facebook(facebookID);
		}
		facebook.authorize((Activity) context, new String[] { "publish_stream","read_stream", "offline_access" }, Facebook.FORCE_DIALOG_AUTH ,new FacebookDialogListener(context));
	}

	private class FacebookDialogListener implements DialogListener{
		private Context context;

		public FacebookDialogListener(Context context){
			this.context = context;
		}

		public void onCancel() {
			releaseFacebook();
		}

		public void onComplete(Bundle values) {
			if (values.isEmpty())
			{
				return;
			}

			if (!values.containsKey("post_id"))
			{
				try
				{
					Bundle parameters = new Bundle();
					parameters.putString("message", message);
					parameters.putString("description", "topic share");
					if(articleDAO != null){
						if (articleDAO.getTitle() != null)
							parameters.putString("message", articleDAO.getTitle());// the
						if (articleDAO.getUrl() != null)
							parameters.putString("link",articleDAO.getUrl());
						if (articleDAO.getThumbUrl() != null)
							parameters.putString("picture", articleDAO.getThumbUrl());
						if (articleDAO.getTitle() != null)
							parameters.putString("name", articleDAO.getTitle());
							parameters.putString("caption", "www.codegreenonline.com");
						if (articleDAO.getDetailedDescription() != null)
							parameters.putString("description", articleDAO.getDetailedDescription());
					}
					facebook.dialog(context, "stream.publish", parameters, this);
					
				}
				catch (Exception e)
				{
					toast_message= "Posted Error--" + e.getMessage();
					Message msg = new Message();
					msg.what = TOASET_MSG;
					screenHandler.sendMessage(msg);
				}
			}
			else
			{
				toast_message= "Article posted successfully.";
				Message msg = new Message();
				msg.what = TOASET_MSG;
				screenHandler.sendMessage(msg);
			}
		}
		@Override
		public void onError(com.facebook.android.DialogError arg0) {
		}

		@Override
		public void onFacebookError(FacebookError arg0) {
			Toast.makeText(context, "Facebook Error--" + arg0.getMessage(), Toast.LENGTH_LONG).show();
			releaseFacebook();

		} 

	}


	private void releaseFacebook() {
		if(facebook != null) {
			try {
				facebook.logout(Share.context);
				facebook = null;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private void onSelectTwitter(){
		ShareData shareData = ShareData.getShareData();
		outerDialog  = new Dialog(Share.context);
		showDialog(TWITTER_DIALOG);
	}

	private void validateTwitter(String user,String pass){
		try{
			System.setProperty("twitter4j.http.useSSL","false");
			httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);

			httpOauthprovider = new DefaultOAuthProvider("http://api.twitter.com/oauth/request_token",
					"http://api.twitter.com/oauth/access_token",
			"http://api.twitter.com/oauth/authorize");
			String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, "oob");


			System.out.println("-------------------------------------------------------" + authUrl + "---------------------------------------");
			System.out.println(authUrl.substring(authUrl.indexOf("=")+1));

			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(new HttpGet(authUrl));

			int status = response.getStatusLine().getStatusCode();

			if (status != HttpStatus.SC_OK) {
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				response.getEntity().writeTo(ostream);
			} else {
				StringBuilder buffer = getBody(response);

				String temp = buffer.substring(buffer.indexOf("authenticity_token"));
				temp = temp.substring(temp.indexOf("value"));
				temp = temp.substring(temp.indexOf("\"")+1);
				temp = temp.substring(0,temp.indexOf("\""));
				HttpPost post = new HttpPost("http://api.twitter.com/oauth/authorize");

				System.out.println(temp + ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("authenticity_token", temp));
				pairs.add(new BasicNameValuePair("oauth_token", authUrl.substring(authUrl.indexOf("=")+1)));
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
					Message msg = new Message();
					toast_message = "Wrong username/email or password";
					msg.what = WRONG_PSW;
					screenHandler.sendMessage(msg);
				}
			}
		} catch (Exception e) {
			toast_message=  e.getMessage();
			Message msg = new Message();
			msg.what = TOASET_MSG;
			screenHandler.sendMessage(msg);
			e.printStackTrace();
		}
	}

	private void postTweet(AccessToken accessToken){
		try{
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			twitter.setOAuthAccessToken(accessToken);
			if(status_msg != null && status_msg.getText() != null) {
				message = status_msg.getText().toString();
			}
			twitter.updateStatus(message );
			Toast.makeText(context, "Posted Successfully", Toast.LENGTH_LONG).show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private StringBuilder getBody(HttpResponse response)throws Exception{
		InputStream content = response.getEntity().getContent();
		InputStreamReader reader = new InputStreamReader(content, HTTP.DEFAULT_CONTENT_CHARSET);

		StringBuilder buffer = new StringBuilder();

		try {

			char[] tmp = new char[1024];

			int l;

			while ((l = reader.read(tmp)) != -1) {

				buffer.append(tmp, 0, l);

			}
			return buffer;
		} finally {

			reader.close();
			content.close();

		}
	}


	private void onSelectEmail(String subject){
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.ACTION_SENDTO, ""); 
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		context.startActivity(Intent.createChooser(emailIntent, "Send Email.."));
	}


	private final static int TOASET_MSG = 0;
	private final static int DISMISS = 1;
	private final static int SHOW = 2;
	private final static int WRONG_PSW = 3;
	TextView status_msg;

	private void showDialog(int id){
		switch(id){
		case TWITTER_DIALOG:

			outerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			outerDialog.setContentView(R.layout.twitterlogin);

			final TextView user = (TextView)outerDialog.findViewById(R.id.usernameEditText);
			final TextView pass = (TextView)outerDialog.findViewById(R.id.passwordEditText);
			status_msg = (TextView)outerDialog.findViewById(R.id.status_msg);
			Button button = (Button)outerDialog.findViewById(R.id.signinButton);
			status_msg.setText(message);
			button.setOnClickListener(new Button.OnClickListener() {


				public void onClick(View v) {
					if((user.getText() == null || pass.getText() == null) || (user.getText().toString().length() <= 0 || pass.getText().toString().length() <= 0)) {
						Message msg = new Message();
						toast_message = "Please enter username/email and password";
						msg.what = TOASET_MSG;
						screenHandler.sendMessage(msg);
					} else{
						userString = user.getText().toString();
						passString = pass.getText().toString();
						Message msg = new Message();
						msg.what = DISMISS;
						screenHandler.sendMessage(msg);
						_doShareTwitter = new DoShareTwitter();
						_doShareTwitter.execute();
					}
				}
			});
			Message msg = new Message();
			msg.what = SHOW;
			screenHandler.sendMessage(msg);
			break;
		}
	}


	public Handler screenHandler = new Handler() 
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case TOASET_MSG:
				Toast.makeText(Share.context, toast_message, Toast.LENGTH_LONG).show();
				break; 
			case DISMISS: 
				outerDialog.dismiss();
				break;
			case SHOW: 
				outerDialog.show();
				break;
			case WRONG_PSW:
				Toast.makeText(Share.context, "Wrong username/email or password",Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	ProgressDialog dialog;

	class DoShareTwitter extends AsyncTask<String, Void, Void> {

		int pos;

		protected void onProgressUpdate(Void... progress) {

			dialog = new ProgressDialog(Share.context);
			dialog.setTitle("");
			dialog.setMessage("Sending Please Wait...");
			dialog.setCancelable(false);

			dialog.show();

		}

		@Override
		protected void onPostExecute(Void params) {
			if (dialog!=null) {
				dialog.cancel();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			publishProgress();
			validateTwitter(Share.userString, Share.passString);
			return null;
		}
	}
}

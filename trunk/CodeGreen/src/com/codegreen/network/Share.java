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
 *//*

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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.RedEye_CTA.R;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class Share {
	private Twitter twitter;
	private Facebook facebook;
	private Context context;
	private String message;
	private String subject = "Share via Email";
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private DefaultOAuthProvider httpOauthprovider;

	public static String facebookID = "138396132897712";
	public static String consumerKey = "fR0ggPX4cduwLKzyNTp0w";
	public static String consumerSecret = "UY9lH4aefr4yhPbxuF3iVcvcDMOROiGAYF9yLWonM8";

	final private int TWITTER_DIALOG = 12345;
	protected String userName;
	protected String paasWord;

	final public static int TYPE_FACEBOOK = 55555;
	final public static int TYPE_TWITTER = 55556;
	final public static int TYPE_EMAIL = 55557;

	*//**
	 * This is the contructor
	 * 
	 * @param context
	 *//*
	public Share(Context context) {
		this.context = context;
	}

	*//**
	 * This method will share the message through particular web service
	 * 
	 * @param message
	 * @param type
	 *//*
	public void share(String message, int type) {
		this.message = message;
		switch (type) {
		case TYPE_FACEBOOK:
			onSelectFacebook();
			break;
		case TYPE_TWITTER:
			onSelectTwitter();
			break;
		case TYPE_EMAIL:
			onSelectEmail(subject);
			break;
		default:
			throw new IllegalArgumentException(
					"please enter the type for facebook,twitter or email");
		}
	}

	*//**
	 * This is a setter method, will explicitly set the current context
	 * 
	 * @param context
	 *//*
	public void setContext(Context context) {
		this.context = context;
	}

	*//**
	 * This is a setter method, will explicitly set the subject of an email(used
	 * when sharing by email).
	 * 
	 * @param subject
	 *//*
	public void setSubject(String subject) {
		this.subject = subject;
	}

	// /////////////////Private Methods not Exposed//////////////////////////
	private void onSelectFacebook() {
		if (facebook == null) {
			facebook = new Facebook(facebookID);
		}
		facebook.authorize((Activity) context, new String[] { "publish_stream",
				"read_stream", "offline_access" }, Facebook.FORCE_DIALOG_AUTH,
				new FacebookDialogListener(context));
	}

	private class FacebookDialogListener implements DialogListener {
		private Context context;

		public FacebookDialogListener(Context context) {
			this.context = context;
		}

		public void onCancel() {
			releaseFacebook();
		}

		public void onComplete(Bundle values) {
			if (values.isEmpty()) {
				return;
			}

			if (!values.containsKey("post_id")) {
				try {
					Bundle parameters = new Bundle();
					parameters.putString("message", message);// the message to
																// post to the
																// wall
					facebook.dialog(context, "stream.publish", parameters, this);// "stream.publish"
																					// is
																					// an
																					// API
																					// call
				} catch (Exception e) {
					Toast.makeText(context, "Posted Error--" + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(context, "Posted Successfully",
						Toast.LENGTH_LONG).show();
			}
		}

		public void onError(DialogError arg0) {
			Toast.makeText(context, "Dialog Error--" + arg0.getMessage(),
					Toast.LENGTH_LONG).show();
			releaseFacebook();
		}

		public void onFacebookError(FacebookError arg0) {
			Toast.makeText(context, "Facebook Error--" + arg0.getMessage(),
					Toast.LENGTH_LONG).show();
			releaseFacebook();
		}
	}

	private void releaseFacebook() {
    	if(facebook != null) {
    		try {
				facebook.logout(context);
				facebook = null;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
	
	private void onSelectTwitter() {
		ShareData shareData = ShareData.getShareData();
		if (shareData.get("Twitter_Token") == null) {
			showDialog(TWITTER_DIALOG);
		} else {
			System.out.println((AccessToken) shareData.get("Twitter_Token"));
			postTweet((AccessToken) shareData.get("Twitter_Token"));

		}
	}

	private void validateTwitter(String user, String pass) {
		try {
			httpOauthConsumer = new CommonsHttpOAuthConsumer(consumerKey,
					consumerSecret);
			httpOauthprovider = new DefaultOAuthProvider(
					"http://twitter.com/oauth/request_token",
					"http://twitter.com/oauth/access_token",
					"http://twitter.com/oauth/authorize");
			String authUrl = httpOauthprovider.retrieveRequestToken(
					httpOauthConsumer, "oob");

			System.out
					.println("-------------------------------------------------------"
							+ authUrl
							+ "---------------------------------------");
			System.out.println(authUrl.substring(authUrl.indexOf("=") + 1));

			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(new HttpGet(authUrl));

			int status = response.getStatusLine().getStatusCode();

			if (status != HttpStatus.SC_OK) {
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				response.getEntity().writeTo(ostream);
			} else {
				StringBuilder buffer = getBody(response);

				HttpPost post = new HttpPost(
						"https://twitter.com/oauth/authorize");

				String temp = buffer.substring(buffer
						.indexOf("authenticity_token"));
				temp = temp.substring(temp.indexOf("value"));
				temp = temp.substring(temp.indexOf("\"") + 1);
				temp = temp.substring(0, temp.indexOf("\""));

				System.out.println(temp
						+ ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("authenticity_token", temp));
				pairs.add(new BasicNameValuePair("oauth_token", authUrl
						.substring(authUrl.indexOf("=") + 1)));
				pairs.add(new BasicNameValuePair("session[username_or_email]",
						user));
				pairs.add(new BasicNameValuePair("session[password]", pass));
				post.setEntity(new UrlEncodedFormEntity(pairs));
				post.getParams().setBooleanParameter(
						"http.protocol.expect-continue", false);

				response = client.execute(post);

				buffer = getBody(response);

				if (buffer.indexOf("\"oauth_pin\"") > 0) {
					temp = buffer.substring(buffer.indexOf("\"oauth_pin\""));
					System.out.println("====================" + temp
							+ "----------------------");
					temp = temp.substring(temp.indexOf(">") + 1,
							temp.indexOf("<"));
					temp = temp.trim();
					System.out.println("====================" + temp
							+ "----------------------");
					httpOauthprovider.retrieveAccessToken(httpOauthConsumer,
							temp);

					AccessToken accessToken = new AccessToken(
							httpOauthConsumer.getToken(),
							httpOauthConsumer.getTokenSecret());

					ShareData.getShareData().put("Twitter_Token", accessToken);

					postTweet(accessToken);
				} else {
					Toast.makeText(context, "Wrong username/email or password",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	private void postTweet(AccessToken accessToken) {
		try {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			twitter.setOAuthAccessToken(accessToken);
			twitter.updateStatus(message);
			Toast.makeText(context, "Posted Successfully", Toast.LENGTH_LONG)
					.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private StringBuilder getBody(HttpResponse response) throws Exception {
		InputStream content = response.getEntity().getContent();
		InputStreamReader reader = new InputStreamReader(content,
				HTTP.DEFAULT_CONTENT_CHARSET);

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

	private void onSelectEmail(String subject) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		context.startActivity(Intent.createChooser(emailIntent, "Send Email.."));
	}

	private void showDialog(int id) {
		switch (id) {
		case TWITTER_DIALOG:
			final Dialog dialog = new Dialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.twitterlogin);

			final TextView user = (TextView) dialog
					.findViewById(R.id.usernameEditText);
			final TextView pass = (TextView) dialog
					.findViewById(R.id.passwordEditText);

			Button button = (Button) dialog.findViewById(R.id.signinButton);

			button.setOnClickListener(new Button.OnClickListener() {

				

				public void onClick(View v) {
					if (user.getText() == null | pass.getText() == null)
						Toast.makeText(v.getContext(),
								"Please enter username/email and password",
								Toast.LENGTH_LONG).show();
					else {
						userName=user.getText().toString();
						paasWord=pass.getText().toString();
						new PerformPost().execute();
						dialog.dismiss();
					}
				}
			});
			dialog.show();
			break;
		}
	}

	class PerformPost extends AsyncTask<String, Void, Void> {
		ProgressDialog dialog;
		int pos;

		protected void onProgressUpdate(Void... progress) {

			dialog = new ProgressDialog(context);
			dialog.setTitle("");
			dialog.setMessage("Send");
			dialog.setCancelable(true);

			dialog.show();

		}

		@Override
		protected void onPostExecute(Void params) {

		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			publishProgress();

			return null;
		}

	}
	  class DoShareTwitter extends AsyncTask<String, Void, Void> {
			ProgressDialog dialog;
			int pos;

			protected void onProgressUpdate(Void... progress) {

				dialog = new ProgressDialog(context);
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
				 validateTwitter(userName, paasWord);
				return null;
				

			}

		}
	    
}
*/
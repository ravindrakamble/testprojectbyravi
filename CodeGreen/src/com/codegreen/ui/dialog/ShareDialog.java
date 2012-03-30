package com.codegreen.ui.dialog;

import com.codegreen.R;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.share.Share;
import com.codegreen.util.Constants;
import com.facebook.android.Facebook;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
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
	private static String toast_message="";


	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

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
		Spanned message = Html.fromHtml(articleDAO.getTitle() + "<br/><br/>"+ articleDAO.getDetailedDescription());
		String subject = "Sending Article details for " + articleDAO.getTitle();
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		if(articleDAO.getUrl() != null)
			emailIntent.putExtra(Intent.EXTRA_STREAM, articleDAO.getUrl()); 
		
		mContext.startActivity(Intent.createChooser(emailIntent, "Send Email.."));
	}

	Dialog outerDialog;

	private void onSelectTwitter(){
		Share testShare = new Share(mContext);
		if(articleDAO.getType().equalsIgnoreCase(Constants.ARTCLETYPE_TEXT))
			message = articleDAO.getTitle()+ "\n www.codegreenonline.com"; // HeadLine + Website Link
		else
			message = articleDAO.getTitle() + "\n" + articleDAO.getUrl(); // for vedio/ audio/image = HeadeLine + URL
		testShare.share(message, Share.TYPE_TWITTER,articleDAO);
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

		
	private void onSelectFacebook(){
		Share testShare = new Share(mContext);
		testShare.share(message, Share.TYPE_FACEBOOK,articleDAO);
	}


	

}

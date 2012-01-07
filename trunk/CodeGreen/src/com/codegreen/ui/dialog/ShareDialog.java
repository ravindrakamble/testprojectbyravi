package com.codegreen.ui.dialog;

import com.codegreen.R;
import com.codegreen.businessprocess.handler.HttpHandler;
import com.codegreen.businessprocess.objects.ArticleDAO;
import com.codegreen.businessprocess.objects.ReviewDAO;
import com.codegreen.listener.Updatable;
import com.codegreen.util.Constants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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

	
	

}

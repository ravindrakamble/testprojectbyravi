package com.codegreen.ui.activity;

import com.codegreen.R;
import com.codegreen.share.Share;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class About extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		TextView txt_featute = (TextView) findViewById(R.id.btn_feature_req);
		txt_featute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelectEmail("Sending Feature Request.","","");
			}
		});
		TextView btn_about  = (TextView)findViewById(R.id.btn_info);
		btn_about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					Context cxt = getApplicationContext();
					Intent intent = new Intent(cxt, AboutDetailsActivty.class);     		
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		TextView btn_query = (TextView)findViewById(R.id.btn_query);
		btn_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelectEmail("Sending Query...","","");
			}
		});
		
		TextView btn_app_review = (TextView)findViewById(R.id.btn_app_review);
		btn_app_review.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelectEmail("Sending Application Review...","","");
			}
		});
		
		TextView btn_facebook = (TextView)findViewById(R.id.btn_facebook);
		btn_facebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSelectFacebook();
			}
		});
		TextView btn_twitter = (TextView)findViewById(R.id.btn_twitter);
		btn_twitter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSelectTwitter();
			}
		});
		
		TextView btn_email = (TextView)findViewById(R.id.btn_email);
		btn_email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSelectEmail("", "", "");
			}
		});
	}
	
	private void onSelectFacebook(){
		Share testShare = new Share(About.this);
		testShare.share("Codegreen App info", Share.TYPE_FACEBOOK,null);
	}
	
	/**
	 * on twitter
	 */
	private void onSelectTwitter(){
		Share testShare = new Share(About.this);
		testShare.share("CodeGreen App Info...", Share.TYPE_TWITTER,null);
	}

	/**
	 * On email selection
	 */
	private void onSelectEmail(String sub,String message,String to) {
		String subject = sub;
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		startActivity(Intent.createChooser(emailIntent, "Send Email.."));
	}
	
}

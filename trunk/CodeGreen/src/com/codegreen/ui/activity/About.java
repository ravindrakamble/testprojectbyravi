package com.codegreen.ui.activity;

import com.codegreen.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class About extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView txt_featute = (TextView) findViewById(R.id.btn_feature_req);
		txt_featute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelectEmail("Sending Feature Request.","","");
			}
		});
	}
	
	
	/**
	 * On email selection
	 */
	private void onSelectEmail(String sub,String message,String to) {
		//Spanned message = Html.fromHtml("Article Title : " + articleDAO.getTitle() + "<br/>"+ "Article Description :" + articleDAO.getDetailedDescription());
		String subject = sub;
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		//emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(articleDAO.getUrl()));
		startActivity(Intent.createChooser(emailIntent, "Send Email.."));
	}
	
}
